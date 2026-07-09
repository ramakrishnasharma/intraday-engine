package com.alertengine.engine;

import com.alertengine.model.*;
import com.alertengine.router.*;

import java.util.concurrent.*;

public class TickProcessingEngine {

    private ExecutorService pool =
            Executors.newFixedThreadPool(
                    Runtime.getRuntime()
                            .availableProcessors()
            );

    private SignalRouter router;

    public TickProcessingEngine(SignalRouter router){

        this.router = router;
    }

    public void onTick(TickData tick){

        pool.submit(() -> {

            router.onTick(tick);

        });
    }
}