package com.alertengine.test;

import com.alertengine.candle.MultiTimeframeEngine;
import com.alertengine.config.AppConfig;
import com.alertengine.engine.AlertDispatcher;
import com.alertengine.engine.CooldownEngine;
import com.alertengine.engine.IndexTracker;
import com.alertengine.engine.PositionManager;
import com.alertengine.engine.StrategyPipeline;
import com.alertengine.engine.SymbolContextRegistry;
import com.alertengine.engine.TickProcessor;
import com.alertengine.engine.TrailingStopManager;
import com.alertengine.notification.AsyncTelegramNotifier;
import com.alertengine.notification.TelegramNotifier;
import com.alertengine.risk.PositionSizer;
import com.alertengine.router.SignalRouter;
import com.alertengine.engine.TickProcessingEngine;
import com.alertengine.engine.StrategyEngine;
import com.alertengine.engine.ScoreEngine;
import com.alertengine.engine.RankingEngine;
import com.alertengine.test.NSEMarketSimulator;

public class MarketSimulationRunner {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting Intraday Engine Simulator");

        // --- Core engines

        StrategyEngine strategyEngine =
                new StrategyEngine();

        StrategyPipeline pipeline =
                new StrategyPipeline(strategyEngine);

        RankingEngine ranking =
                new RankingEngine();

        TelegramNotifier notifier =
                new TelegramNotifier(
                        AppConfig.get("telegram.token"),
                        AppConfig.get("telegram.chatId")
                );

        AsyncTelegramNotifier asyncNotifier =
                new AsyncTelegramNotifier(notifier);

        AlertDispatcher dispatcher =
                new AlertDispatcher(asyncNotifier);

        dispatcher.startWorker();

        PositionManager positionManager =
                new PositionManager();

        TrailingStopManager trailing =
                new TrailingStopManager();

        CooldownEngine cooldown =
                new CooldownEngine();

        IndexTracker indexTracker =
                new IndexTracker();

        MultiTimeframeEngine candleEngine = new MultiTimeframeEngine();

        TickProcessor processor =
                new TickProcessor(candleEngine);

        SymbolContextRegistry registry =
                new SymbolContextRegistry();



        PositionSizer sizer =
                new PositionSizer(60000);

        SignalRouter router =
                new SignalRouter(
                        processor,
                        pipeline,
                        ranking,
                        dispatcher,
                        positionManager,
                        trailing,
                        cooldown,
                        indexTracker,
                        registry,
                        sizer
                );

        // --- Start simulator

        NSEMarketSimulator simulator =
                new NSEMarketSimulator(router);

        simulator.start();

        System.out.println("Simulation Completed");
    }
}