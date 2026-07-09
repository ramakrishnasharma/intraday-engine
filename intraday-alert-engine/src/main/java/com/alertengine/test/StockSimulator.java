package com.alertengine.test;


import com.alertengine.model.TickData;
import com.alertengine.router.SignalRouter;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class StockSimulator {

    private final MarketState state;
    private final SignalRouter router;

    private final Random random = new Random();

    public StockSimulator(MarketState state, SignalRouter router) {
        this.state = state;
        this.router = router;
    }

    public void generate(long time) {

        for (Map.Entry<String, List<String>> entry :
                MarketUniverse.SECTOR_STOCKS.entrySet()) {

            String sector = entry.getKey();

            double sectorPrice = state.sectorPrices.get(sector);

            for (String stock : entry.getValue()) {

                double price = state.stockPrices.get(stock);

                double sectorMove = (random.nextDouble() - 0.5) * 2;

                double noise = (random.nextDouble() - 0.5) * 3;

                price += sectorMove + noise;

                if (price < 1)
                    price = 1;

                state.stockPrices.put(stock, price);

                TickData tick = new TickData();

                tick.symbol=(stock);
                tick.price=(price);

                int volume;

                if (random.nextDouble() < 0.1)
                    volume = 200000;
                else
                    volume = 20000 + random.nextInt(40000);

                tick.volume=(volume);

                tick.timestamp=(time);

                router.onTick(tick);
            }
        }
    }
}