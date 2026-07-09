package com.alertengine.engine;

import com.alertengine.model.*;
import com.alertengine.candle.*;

public class TickProcessor {

    private MultiTimeframeEngine candleEngine;

    public TickProcessor(MultiTimeframeEngine candleEngine){
        this.candleEngine = candleEngine;
    }

    public CandleBundle process(TickData tick){

        if(tick == null)
            return null;

        return candleEngine.onTick(tick);
    }
}