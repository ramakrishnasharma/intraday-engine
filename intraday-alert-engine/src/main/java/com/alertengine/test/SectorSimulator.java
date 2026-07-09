package com.alertengine.test;

import com.alertengine.model.TickData;
import com.alertengine.router.SignalRouter;

import java.util.Random;

public class SectorSimulator {

    private final MarketState state;
    private final SignalRouter router;
    private final Random random = new Random();

    public SectorSimulator(MarketState state, SignalRouter router) {
        this.state = state;
        this.router = router;
    }

    public void generate(long time) {

        for (String sector : state.sectorPrices.keySet()) {

            double price = state.sectorPrices.get(sector);

            double indexInfluence = (random.nextDouble() - 0.5) * 10;

            price += indexInfluence;

            state.sectorPrices.put(sector, price);

            TickData tick = new TickData();
            tick.symbol=(sector);
            tick.price=(price);
            tick.volume=(80000);
            tick.timestamp=(time);

            router.onTick(tick);
        }
    }
}