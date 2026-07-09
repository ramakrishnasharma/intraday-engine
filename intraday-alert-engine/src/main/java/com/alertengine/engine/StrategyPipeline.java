package com.alertengine.engine;

import com.alertengine.model.*;

import java.util.concurrent.*;

public class StrategyPipeline {

    private StrategyEngine engine;

    public StrategyPipeline(StrategyEngine engine){

        this.engine = engine;
    }

    public Signal evaluate(CandleBundle bundle,
                           double vwap,
                           double avgVolume){

        Signal signal =
                engine.evaluate(
                        bundle,
                        vwap,
                        avgVolume
                );

//        if(signal != null){
//
//            System.out.println(
//                    "Signal: " + signal.symbol
//            );
//        }

        return signal;
    }
}