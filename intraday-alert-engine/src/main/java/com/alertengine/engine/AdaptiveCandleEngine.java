package com.alertengine.engine;

import com.alertengine.model.*;
import com.alertengine.indicator.*;

import java.util.*;

public class AdaptiveCandleEngine {

    private Map<Long,Candle> candles =
            new HashMap<>();

    private VolatilityDetector volatility =
            new VolatilityDetector();

    public Candle onTick(TickData tick){

        int timeframe =
                volatility.getOptimalTimeframe(
                        tick.symbol, tick.price
                );

        long bucket =
                System.currentTimeMillis() /
                (60000 * timeframe);

        Candle c = candles.get(tick.token);

        if(c == null){

            c = new Candle();

            c.token = tick.token;
            c.symbol = tick.symbol;

            c.open = tick.price;
            c.high = tick.price;
            c.low = tick.price;
            c.close = tick.price;

            c.volume = tick.volume;

            c.startTime = bucket;

            candles.put(tick.token,c);

            return null;
        }

        if(c.startTime != bucket){

            Candle finished = c;

            Candle newC = new Candle();

            newC.token = tick.token;
            newC.symbol = tick.symbol;

            newC.open = tick.price;
            newC.high = tick.price;
            newC.low = tick.price;
            newC.close = tick.price;

            newC.volume = tick.volume;

            newC.startTime = bucket;

            candles.put(tick.token,newC);

            return finished;
        }

        c.close = tick.price;

        c.high = Math.max(c.high,tick.price);
        c.low = Math.min(c.low,tick.price);

        c.volume += tick.volume;

        return null;
    }
}