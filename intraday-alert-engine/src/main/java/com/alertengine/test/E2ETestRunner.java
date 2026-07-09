package com.alertengine.test;

import com.alertengine.candle.MultiTimeframeEngine;
import com.alertengine.config.AppConfig;
import com.alertengine.engine.*;
import com.alertengine.notification.AsyncTelegramNotifier;
import com.alertengine.notification.TelegramNotifier;
import com.alertengine.router.SignalRouter;
import com.alertengine.strategy.*;
import com.alertengine.model.*;
import com.alertengine.risk.*;
import com.alertengine.filter.*;
import com.alertengine.util.*;
import com.zerodhatech.models.Tick;

import java.util.Random;

public class E2ETestRunner {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting E2E Test");

        /*
         ENGINE SETUP
        */

        MultiTimeframeEngine candleEngine = new MultiTimeframeEngine();

        TickProcessor processor =
                new TickProcessor(candleEngine);

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

        PositionManager positionManager =
                new PositionManager();

        TrailingStopManager trailing =
                new TrailingStopManager();

        CooldownEngine cooldown =
                new CooldownEngine();

        IndexTracker indexTracker =
                new IndexTracker();

        SymbolContextRegistry contextRegistry =
                new SymbolContextRegistry();

        PositionSizer sizer =
                new PositionSizer(100000);

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
                        contextRegistry,
                        sizer
                );

        /*
         Simulate market index
        */

        router.updateIndex(22000);

        /*
         Simulate sector index
        */

        router.updateSector("NIFTY IT", 38000);

        /*
         Generate fake ticks
        */

        long ts = System.currentTimeMillis();


        Random random = new Random();

        double price = 1500;
        double drift = 0.02; // upward trend

        for(int i = 0; i < 5000; i++) {

            double noise = (random.nextDouble() - 0.5) * 0.8;
            price = price + drift + noise;

            TickData tick = new TickData();
            tick.symbol=("INFY");
            tick.price=(price);
            tick.volume=(10000 + random.nextInt(50000));
            tick.timestamp=(System.currentTimeMillis());

            router.onTick(tick);

            Thread.sleep(200); // simulate live feed

        }

        System.out.println("Test Completed");

    }
}