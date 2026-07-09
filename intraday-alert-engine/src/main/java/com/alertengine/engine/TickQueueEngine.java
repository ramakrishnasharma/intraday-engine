package com.alertengine.engine;

import com.alertengine.model.*;
import com.alertengine.router.SignalRouter;

import java.util.concurrent.*;

public class TickQueueEngine {

    private BlockingQueue<TickData> queue =
            new LinkedBlockingQueue<>();

    private static int nWorkers = Runtime.getRuntime().availableProcessors()*2;

    private ExecutorService workers =
            Executors.newFixedThreadPool(
                    nWorkers
            );

    private TickProcessor processor;
    private SignalRouter router;

    public TickQueueEngine(TickProcessor processor,
                           SignalRouter router){

        this.processor = processor;
        this.router = router;

        start();
    }

    public void push(TickData tick){

        queue.offer(tick);
    }

    private void start(){

        for(int i=0;i<nWorkers;i++){

            workers.submit(() -> {

                while(!Thread.currentThread().isInterrupted()){

                    try{

                        TickData tick =
                                queue.take();

                        /*
                         pass tick to router
                        */

                        router.onTick(tick);

                    }catch(Exception e){

                        e.printStackTrace();
                    }
                }
            });
        }
    }
}