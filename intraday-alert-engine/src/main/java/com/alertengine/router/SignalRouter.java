package com.alertengine.router;

import com.alertengine.engine.*;
import com.alertengine.model.*;
import com.alertengine.risk.*;
import com.alertengine.filter.*;
import com.alertengine.util.MarketTimeUtil;
import com.alertengine.watchlist.SectorIndexRegistry;
import com.alertengine.watchlist.SectorRegistry;

import java.util.*;

public class SignalRouter {

    private TickProcessor processor;
    private StrategyPipeline pipeline;

    private RankingEngine ranking;
    private AlertDispatcher dispatcher;

    private PositionManager positionManager;
    private TrailingStopManager trailing;

    private CooldownEngine cooldown;
    private IndexTracker indexTracker;

    private SymbolContextRegistry contextRegistry;

    private PositionSizer sizer;

    private RelativeStrengthFilter relativeStrengthFilter =
            new RelativeStrengthFilter();

    private MomentumMonitor momentumMonitor =
            new MomentumMonitor();

    /*
     Signal buffer for cross-stock ranking
    */

    private List<Signal> signalBuffer =
            new ArrayList<>();

    /*
     Number of signals to evaluate before ranking
    */

    private static final int BUFFER_THRESHOLD = 3;

    /*
     Max trades per scan
    */

    private static final int MAX_TRADES = 3;

    private SectorTracker sectorTracker =
            new SectorTracker();

    private double currentIndexMove = 0;

    public SignalRouter(TickProcessor processor,
                        StrategyPipeline pipeline,
                        RankingEngine ranking,
                        AlertDispatcher dispatcher,
                        PositionManager positionManager,
                        TrailingStopManager trailing,
                        CooldownEngine cooldown,
                        IndexTracker indexTracker,
                        SymbolContextRegistry contextRegistry,
                        PositionSizer sizer){

        this.processor = processor;
        this.pipeline = pipeline;
        this.ranking = ranking;
        this.dispatcher = dispatcher;
        this.positionManager = positionManager;
        this.trailing = trailing;
        this.cooldown = cooldown;
        this.indexTracker = indexTracker;
        this.contextRegistry = contextRegistry;
        this.sizer = sizer;
    }

    public void updateSector(String sector,double price){

        sectorTracker.update(sector,price);
    }

    public void updateIndex(double price){

        currentIndexMove =
                indexTracker.update(price);
    }

    public void onTick(TickData tick){
//        System.out.println("Router received tick: " + tick.symbol);

        /*
         MARKET HOURS
        */

        if(!MarketTimeUtil.isMarketOpen(tick.timestamp))
            return;

        /*
         BUILD CANDLES
        */

//        System.out.println("Processing tick " + tick.symbol);
        CandleBundle bundle =
                processor.process(tick);

        if(bundle == null || bundle.c1m == null)
            return;

//        /*
//         Only evaluate on new 1m candle
//        */
//
//        if(!bundle.newCandle)
//            return;
//
//        Candle c1 = bundle.prev1m;
//        if(c1 == null)
//            return;
//        bundle.c1m = c1;

        Candle c1 = bundle.c1m;
//        if(c1 == null)
//            return;

//        System.out.println("Evaluating "+c1.symbol);

        /*
         CONTEXT
        */

        SymbolContext ctx =
                contextRegistry.get(c1.symbol);

        double vwap =
                ctx.vwap.update(
                        c1.symbol,
                        c1.close,
                        c1.volume
                );

        double avgVolume =
                ctx.avgVolume.update(
                        c1.symbol,
                        c1.volume
                );


        /*
         STRATEGY PIPELINE
        */

        Signal signal =
                pipeline.evaluate(
                        bundle,
                        vwap,
                        avgVolume
                );

//        System.out.println("Signal result :: "+signal);

        if(signal == null)
            return;

        double diff = signal.entry - tick.price;
        signal.entry = tick.price;
        signal.stopLoss -= diff;
        signal.target -= diff;

        String sector =
                SectorRegistry.getSector(tick.symbol);

        if(sector == null)
            return;

        String sectorIndex =
                SectorIndexRegistry.getSectorIndex(sector);

        if(sectorIndex == null)
            return;

        double sectorMove =
                sectorTracker.getMove(sectorIndex);

        if(sectorMove == 0)
            return;

        double marketMove =
                currentIndexMove;

        double stockMove =
                ctx.rs.update(
                        c1.symbol,
                        c1.close
                );

//        System.out.println(
//                "RS: " + stockMove +
//                        " sector: " + sectorMove
//        );

        if(signal.side.equals("LONG")){

            if(!(stockMove > sectorMove &&
                    sectorMove > currentIndexMove))
                return;
        }

        if(signal.side.equals("SHORT")){

            if(!(stockMove < sectorMove &&
                    sectorMove < currentIndexMove))
                return;
        }

        double rsSpread =
                stockMove - sectorMove;

        if(signal.side.equals("LONG") &&
                rsSpread > 0.003)
            signal.score += 2;

        if(signal.side.equals("SHORT") &&
                rsSpread < -0.003)
            signal.score += 2;

        /*
         COOLDOWN FILTER
        */

        if(!cooldown.allow(signal.symbol))
            return;

        /*
         Already in position?
        */

        if(positionManager.exists(signal.symbol))
            return;

        /*
         POSITION SIZING
        */

        int qty =
                sizer.calculateQuantity(signal);

//        System.out.println("Qty cal :: "+qty);

        if(qty <= 0)
            return;

        signal.quantity = qty;

        /*
         Add signal to buffer
        */

        signalBuffer.add(signal);

        /*
         Execute ranking when buffer threshold reached
        */

        if(signalBuffer.size() >= BUFFER_THRESHOLD || signal.score >= 7 ){
            evaluateRanking();
        }

        /*
         MONITOR EXISTING POSITIONS
        */

        List<Position> toRemove = new ArrayList<>();
        for(Position p : positionManager.getAll()){

            /*
             trailing stop
            */

            if(p.symbol.equals(tick.symbol)){

                trailing.update(p, tick.price);

                /*
             momentum monitor
            */

                String update =
                        momentumMonitor.check(p, tick.price);
                if(update != null){

                    dispatcher.push(
                            "Update: " +
                                    p.symbol + " " + update
                    );
                    cooldown.reset(p.symbol);
                    toRemove.add(p);
                }
            }
        }
        for(Position p : toRemove)
            positionManager.remove(p.symbol);
    }

    private void evaluateRanking(){

        if(signalBuffer.isEmpty())
            return;

        ranking.clear();

        for(Signal s : signalBuffer)
            ranking.add(s);

        List<Signal> bestSignals =
                ranking.top(MAX_TRADES);

        for(Signal best : bestSignals){

            if(positionManager.exists(best.symbol))
                continue;

            positionManager.addPosition(best);

            String msg =
                    "Signal: " + best.symbol +
                            " " + best.side +
                            " Qty:" + best.quantity +
                            " Entry:" + best.entry +
                            " SL:" + best.stopLoss +
                            " Target:" + best.target +
                            " Score:" + best.score;

            dispatcher.push(msg);
        }

        signalBuffer.clear();
    }
}