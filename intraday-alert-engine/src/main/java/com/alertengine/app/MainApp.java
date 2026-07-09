package com.alertengine.app;

import com.alertengine.config.AppConfig;
import com.alertengine.engine.*;
import com.alertengine.router.*;
import com.alertengine.candle.*;
import com.alertengine.notification.*;
import com.alertengine.risk.*;
import com.alertengine.broker.*;
import com.alertengine.watchlist.SectorRegistry;
import com.alertengine.watchlist.WatchlistLoader;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

import java.util.*;

public class MainApp {

    public static void main(String[] args) throws KiteException, InterruptedException {

        SectorRegistry.load("src/main/resources/sector_map.csv");

        String apiKey = AppConfig.get("api.key");
        String apiSecret = AppConfig.get("api.secret");
        String accessToken = AppConfig.get("access.token");

        /*
         Load watchlist tokens
        */
        WatchlistLoader loader = new WatchlistLoader();
        ArrayList<Long> tokens = loader.load();

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

        TickQueueEngine tickQueue =
                new TickQueueEngine(processor, router);

        notifier.send("Scanner Started");

        ZerodhaWebSocket ws =
                new ZerodhaWebSocket(
                        apiKey,
                        apiSecret,
                        accessToken,
                        tokens,
                        router,
                        tickQueue
                );

        ws.connect();
        System.out.println("Trading engine started");
    }
}