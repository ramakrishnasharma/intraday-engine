package com.alertengine.candle;

import com.alertengine.model.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTimeframeEngine {

    private Map<Long, Candle> candle1m = new ConcurrentHashMap<>();
    private Map<Long, Candle> candle5m = new ConcurrentHashMap<>();
    private Map<Long, Candle> candle15m = new ConcurrentHashMap<>();

    private Map<Long, Long> bucket1m = new ConcurrentHashMap<>();
    private Map<Long, Long> bucket5m = new ConcurrentHashMap<>();
    private Map<Long, Long> bucket15m = new ConcurrentHashMap<>();

    /* track previous cumulative volume per token */
    private Map<Long, Double> lastTickVolume = new ConcurrentHashMap<>();

    public CandleBundle onTick(TickData tick) {
        if(tick == null)
            return null;

        if(tick.token == 0) {
            System.out.println("INVALID TICK: token=0 symbol=" + tick.symbol);
            return null;
        }

        if(tick.price <= 0) {
            System.out.println("INVALID PRICE: " + tick.symbol + " price=" + tick.price);
            return null;
        }

        if (tick.price > 100000 || tick.price < 1) {
            System.out.println("BAD PRICE TICK: " + tick.symbol + " price=" + tick.price);
            return null;
        }

        System.out.println(
                "Tick -> "
                        + tick.symbol
                        + " token=" + tick.token
                        + " price=" + tick.price
                        + " volume=" + tick.volume
                        + " ts=" + tick.timestamp
        );

        CandleBundle bundle = new CandleBundle();

        long minute = tick.timestamp / 60000;

        /* -------- CALCULATE INCREMENTAL VOLUME -------- */

        Double prevVol = lastTickVolume.get(tick.token);

        double delta;

        if (prevVol == null) {
            delta = tick.volume;
        } else {
            delta = tick.volume - prevVol;
            if (delta < 0) {
                delta = 0;
            }
        }

        lastTickVolume.put(tick.token, tick.volume);

        /* -------- 1 MINUTE CANDLE -------- */

        Long prevBucket = bucket1m.get(tick.token);
        Candle c1 = candle1m.get(tick.token);

        boolean newCandle = false;

        if(prevBucket != null && minute - prevBucket > 1) {

            System.out.println("GAP DETECTED " + tick.symbol);

        }

        if (prevBucket == null || prevBucket != minute || c1 == null) {

            Candle prev = c1;
            newCandle = true;

            bucket1m.put(tick.token, minute);

            c1 = new Candle();

            c1.symbol = tick.symbol;
            c1.token = tick.token;

            c1.open = tick.price;
            c1.high = tick.price;
            c1.low = tick.price;
            c1.close = tick.price;

            c1.volume = delta;

            c1.startTime = minute * 60000;
            c1.endTime = c1.startTime + 60000;

            candle1m.put(tick.token, c1);

            bundle.prev1m = prev;

        } else {

            c1.high = Math.max(c1.high, tick.price);
            c1.low = Math.min(c1.low, tick.price);
            c1.close = tick.price;

            c1.volume += delta;
        }

        bundle.c1m = c1;
        bundle.newCandle = newCandle;

        /* -------- 5 MINUTE CANDLE -------- */

        long bucket5 = minute / 5;

        Long prev5 = bucket5m.get(tick.token);
        Candle c5 = candle5m.get(tick.token);

        if (prev5 == null || prev5 != bucket5 || c5 == null) {

            bucket5m.put(tick.token, bucket5);

            c5 = new Candle();

            c5.symbol = tick.symbol;
            c5.token = tick.token;

            c5.open = tick.price;
            c5.high = tick.price;
            c5.low = tick.price;
            c5.close = tick.price;

            c5.volume = delta;

            c5.startTime = bucket5 * 5 * 60000;
            c5.endTime = c5.startTime + 5 * 60000;

            candle5m.put(tick.token, c5);

        } else {

            c5.high = Math.max(c5.high, tick.price);
            c5.low = Math.min(c5.low, tick.price);
            c5.close = tick.price;

            c5.volume += delta;
        }

        bundle.c5m = c5;

        /* -------- 15 MINUTE CANDLE -------- */

        long bucket15 = minute / 15;

        Long prev15 = bucket15m.get(tick.token);
        Candle c15 = candle15m.get(tick.token);

        if (prev15 == null || prev15 != bucket15 || c15 == null) {

            bucket15m.put(tick.token, bucket15);

            c15 = new Candle();

            c15.symbol = tick.symbol;
            c15.token = tick.token;

            c15.open = tick.price;
            c15.high = tick.price;
            c15.low = tick.price;
            c15.close = tick.price;

            c15.volume = delta;

            c15.startTime = bucket15 * 15 * 60000;
            c15.endTime = c15.startTime + 15 * 60000;

            candle15m.put(tick.token, c15);

        } else {

            c15.high = Math.max(c15.high, tick.price);
            c15.low = Math.min(c15.low, tick.price);
            c15.close = tick.price;

            c15.volume += delta;
        }

        bundle.c15m = c15;

        /* -------- DEBUG LOG -------- */

        if (bundle.newCandle) {

            Candle c = bundle.c1m;

            System.out.println(
                    "New 1m Candle: " +
                            c.symbol +
                            " O:" + c.open +
                            " H:" + c.high +
                            " L:" + c.low +
                            " C:" + c.close +
                            " V:" + c.volume
            );
        }

        return bundle;
    }
}