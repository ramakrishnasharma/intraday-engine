package com.alertengine.candle;

import com.alertengine.model.*;

import java.util.*;

public class CandleBuilder {

    private Map<Long,Candle> candles = new HashMap<>();

    public Candle onTick(TickData tick){

        long minute = tick.timestamp / 60000;

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

            c.startTime = minute;

            candles.put(tick.token,c);

            return null;
        }

        if(c.startTime != minute){

            Candle finished = c;

            Candle newC = new Candle();

            newC.token = tick.token;
            newC.symbol = tick.symbol;

            newC.open = tick.price;
            newC.high = tick.price;
            newC.low = tick.price;
            newC.close = tick.price;

            newC.volume = tick.volume;

            newC.startTime = minute;

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