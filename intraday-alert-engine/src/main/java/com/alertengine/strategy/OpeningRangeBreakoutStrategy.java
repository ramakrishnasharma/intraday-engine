package com.alertengine.strategy;

import com.alertengine.model.*;
import com.alertengine.indicator.*;
import com.alertengine.engine.ScoreEngine;
import com.alertengine.filter.*;

import java.time.*;
import java.util.*;

public class OpeningRangeBreakoutStrategy {

    private Map<String, Double> rangeHigh =
            new HashMap<>();

    private Map<String, Double> rangeLow =
            new HashMap<>();

    private Map<String, Boolean> rangeLocked =
            new HashMap<>();

    private Map<String, ATRCalculator> atrMap =
            new HashMap<>();

    private Map<String, VolumeSpikeDetector> volumeMap =
            new HashMap<>();

    private TrendFilter trend =
            new TrendFilter();

    private ScoreEngine scorer =
            new ScoreEngine();

    public void updateRange(Candle candle){

        if(candle == null)
            return;

        String symbol = candle.symbol;

        long ts = candle.startTime;

        LocalDateTime time =
                Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.of("Asia/Kolkata"))
                        .toLocalDateTime();

        int minuteOfDay =
                time.getHour()*60 + time.getMinute();

        int open = 9*60 + 15;
        int rangeEnd = 9*60 + 30;

        if(minuteOfDay >= open && minuteOfDay < rangeEnd){

            rangeHigh.put(
                    symbol,
                    Math.max(
                            rangeHigh.getOrDefault(symbol,0.0),
                            candle.high
                    )
            );

            rangeLow.put(
                    symbol,
                    Math.min(
                            rangeLow.getOrDefault(symbol,
                                    Double.MAX_VALUE),
                            candle.low
                    )
            );

        } else if(minuteOfDay >= rangeEnd){

            rangeLocked.put(symbol,true);
        }
    }

    public Signal evaluate(Candle c1,
                           Candle c5,
                           Candle c15,
                           double vwap){

        if(c1 == null)
            return null;

        String symbol = c1.symbol;

        if(!rangeLocked.getOrDefault(symbol,false))
            return null;

        ATRCalculator atr =
                atrMap.computeIfAbsent(
                        symbol,
                        s -> new ATRCalculator()
                );

        VolumeSpikeDetector volume =
                volumeMap.computeIfAbsent(
                        symbol,
                        s -> new VolumeSpikeDetector()
                );

        double atrValue =
                atr.update(c1);

        double high =
                rangeHigh.getOrDefault(symbol,0.0);

        double low =
                rangeLow.getOrDefault(symbol,0.0);

        double range =
                high - low;

        // Filter 1: range must have volatility
        boolean volatility =
                range > (atrValue * 0.4);

        // Filter 2: volume confirmation
        boolean volumeSpike =
                volume.isSpike(c1.volume);

        // Filter 3: VWAP alignment
        boolean vwapLong =
                c1.close > vwap;

        boolean vwapShort =
                c1.close < vwap;

        // Filter 4: trend alignment
        boolean trendUp =
                trend.isUptrend(c5,c15);

        boolean trendDown =
                trend.isDowntrend(c5,c15);

        /*
         LONG BREAKOUT
        */

        if(c1.close > high){

            int score =
                    scorer.calculate(
                            volatility,
                            volumeSpike,
                            vwapLong,
                            trendUp
                    );

            if(score < 4)
                return null;

            double entry =
                    high * 1.001;

            double stopLoss =
                    entry - (1.2 * atrValue);

            double target =
                    entry + (2 * atrValue);

            return new Signal(
                    symbol,
                    "ORB Breakout",
                    "LONG",
                    entry,
                    stopLoss,
                    target,
                    score,
                    atrValue
            );
        }

        /*
         SHORT BREAKDOWN
        */

        if(c1.close < low){

            int score =
                    scorer.calculate(
                            volatility,
                            volumeSpike,
                            vwapShort,
                            trendDown
                    );

            if(score < 4)
                return null;

            double entry =
                    low * 0.999;

            double stopLoss =
                    entry + (1.2 * atrValue);

            double target =
                    entry - (2 * atrValue);

            return new Signal(
                    symbol,
                    "ORB Breakdown",
                    "SHORT",
                    entry,
                    stopLoss,
                    target,
                    score,
                    atrValue
            );
        }

        return null;
    }
}