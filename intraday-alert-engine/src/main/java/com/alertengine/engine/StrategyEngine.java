package com.alertengine.engine;

import com.alertengine.model.*;
import com.alertengine.strategy.*;
import com.alertengine.indicator.*;
import com.alertengine.filter.*;
import com.alertengine.risk.*;

import java.util.HashMap;
import java.util.Map;

public class StrategyEngine {

    private MomentumStrategy momentum =
            new MomentumStrategy();

    private VWAPReversalStrategy vwapStrategy =
            new VWAPReversalStrategy();

    private OpeningRangeBreakoutStrategy orb =
            new OpeningRangeBreakoutStrategy();

    /*
     ATR per symbol
    */
    private Map<String, ATRCalculator> atrMap =
            new HashMap<>();

    /*
     Volatility detector per symbol
    */
    private Map<String, VolatilityDetector> volatilityMap =
            new HashMap<>();

    private MarketRegimeDetector regime =
            new MarketRegimeDetector();

    private LiquidityFilter liquidity =
            new LiquidityFilter();

    private ProfitabilityFilter profitFilter =
            new ProfitabilityFilter();


    public Signal evaluate(CandleBundle bundle,
                           double vwap,
                           double avgVolume){

        if(bundle == null || bundle.c1m == null)
            return null;

        Candle c1 = bundle.c1m;
        Candle c5 = bundle.c5m;
        Candle c15 = bundle.c15m;

//        System.out.println(
//                "StrategyEngine: " +
//                        c1.symbol +
//                        " O:" + c1.open +
//                        " H:" + c1.high +
//                        " L:" + c1.low +
//                        " C:" + c1.close
//        );

        /*
         1️⃣ Liquidity filter
        */

        if(!liquidity.allow(avgVolume, c1.close))
            return null;

        /*
         2️⃣ Volatility detector (remove dead stocks)
        */

        VolatilityDetector volatility =
                volatilityMap.computeIfAbsent(
                        c1.symbol,
                        s -> new VolatilityDetector()
                );

        int timeframe =
                volatility.getOptimalTimeframe(c1.symbol, c1.close);

        if(timeframe > 3)
            return null;

        /*
         3️⃣ ATR volatility filter
        */

        ATRCalculator atrCalc =
                atrMap.computeIfAbsent(
                        c1.symbol,
                        s -> new ATRCalculator()
                );

        double atr =
                atrCalc.update(c1);

        double atrRatio =
                atr / c1.close;

        if(atrRatio < 0.0004)
            return null;

        /*
         4️⃣ ORB range must always update
        */

        orb.updateRange(c1);

        /*
         5️⃣ Detect market regime
        */

        String marketType =
                regime.detect(c5, c15, atr);

        Signal signal = null;

        /*
         TRENDING MARKET
        */

        Signal best = null;

/*
Run ALL strategies
*/

        Signal s1 =
                momentum.evaluate(
                        c1,
                        c5,
                        c15,
                        vwap
                );

        Signal s2 =
                vwapStrategy.evaluate(
                        c1,
                        vwap
                );

        Signal s3 =
                orb.evaluate(
                        c1,
                        c5,
                        c15,
                        vwap
                );

/*
Pick highest scoring signal
*/

        if(s1 != null)
            best = s1;

        if(s2 != null &&
                (best == null || s2.score > best.score))
            best = s2;

        if(s3 != null &&
                (best == null || s3.score > best.score))
            best = s3;

/*
Profitability filter
*/

        if(best != null &&
                !profitFilter.allow(best))
            return null;

        signal = best;
        /*
         6️⃣ Profitability filter
        */

        if(signal != null &&
                !profitFilter.allow(signal))
            return null;

        return signal;
    }
}