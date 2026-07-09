package com.alertengine.test;


import com.alertengine.model.TickData;
import com.alertengine.router.SignalRouter;

import java.util.Random;

public class IndexSimulator {

    private final MarketState state;
    private final SignalRouter router;
    private final Random random = new Random();

    public IndexSimulator(MarketState state, SignalRouter router) {
        this.state = state;
        this.router = router;
    }

    public void generate(long time) {

        double move = (random.nextDouble() - 0.5) * 15;

        state.niftyPrice += move;

        TickData tick = new TickData();
        tick.symbol=("NIFTY 50");
        tick.price=(state.niftyPrice);
        tick.volume=(100000);
        tick.timestamp=(time);

        router.onTick(tick);
    }
}
