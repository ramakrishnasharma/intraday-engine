package com.alertengine.test;


import com.alertengine.router.SignalRouter;

public class NSEMarketSimulator {

    private final MarketState state;

    private final IndexSimulator indexSimulator;

    private final SectorSimulator sectorSimulator;

    private final StockSimulator stockSimulator;

    public NSEMarketSimulator(SignalRouter router) {

        state = new MarketState();

        indexSimulator = new IndexSimulator(state, router);

        sectorSimulator = new SectorSimulator(state, router);

        stockSimulator = new StockSimulator(state, router);
    }

    public void start() throws Exception {

        System.out.println("Starting NSE Market Simulation");

        long baseTime = System.currentTimeMillis();

        for (int tick = 0; tick < 20000; tick++) {

            long time = baseTime + tick * 200;

            indexSimulator.generate(time);

            sectorSimulator.generate(time);

            stockSimulator.generate(time);

            Thread.sleep(100);
        }

        System.out.println("Simulation Finished");
    }
}
