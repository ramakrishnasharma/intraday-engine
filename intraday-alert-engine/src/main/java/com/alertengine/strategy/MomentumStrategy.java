package com.alertengine.strategy;

import com.alertengine.model.*;
import com.alertengine.indicator.*;
import com.alertengine.engine.*;
import com.alertengine.filter.*;

import java.util.*;

public class MomentumStrategy {

    private Map<String, ATRCalculator> atrMap =
            new HashMap<>();

    private Map<String, VolumeSpikeDetector> volumeMap =
            new HashMap<>();

    private Map<String, VolumeAverageCalculator> avgVolumeMap =
            new HashMap<>();

    private ScoreEngine scorer =
            new ScoreEngine();

    private TrendFilter trend =
            new TrendFilter();

    public Signal evaluate(Candle c1,
                           Candle c5,
                           Candle c15,
                           double vwap){

        if(c1 == null)
            return null;

        String symbol = c1.symbol;

        ATRCalculator atr =
                atrMap.computeIfAbsent(
                        symbol,
                        s -> new ATRCalculator()
                );

        VolumeAverageCalculator avgCalc =
                avgVolumeMap.computeIfAbsent(
                        symbol,
                        s -> new VolumeAverageCalculator()
                );

        double avgVolume =
                avgCalc.update(symbol, c1.volume);

        boolean volumeSpike =
                c1.volume > avgVolume * 1.5;

        double atrVal =
                atr.update(c1);

        if(atrVal == 0)
            atrVal = c1.close * 0.002;

//        if(atrVal < c1.close * 0.001)
//            return null;

        boolean trendUp =
                trend.isUptrend(c5,c15);

        boolean trendDown =
                trend.isDowntrend(c5,c15);

        boolean bullish =
                c1.close > c1.open;

        boolean bearish =
                c1.close < c1.open;

        double body =
                Math.abs(c1.close - c1.open);

        boolean momentum =
                body > (atrVal * 0.25);

        boolean vwapAligned =
                Math.abs(c1.close - vwap) / vwap > 0.0005;

        double range =
                c1.high - c1.low;

        boolean rangeExpansion =
                range > atrVal * 0.8;

        if(!momentum)
            return null;

        int score =
                scorer.calculate(
                        momentum,
                        volumeSpike,
                        vwapAligned,
                        trendUp || trendDown
                );

/*
DEBUG BLOCK
*/

//        System.out.println("Momentum Debug: " + c1.symbol);
//        System.out.println("Momentum: " + momentum);
//        System.out.println("VolumeSpike: " + volumeSpike);
//        System.out.println("VWAPAligned: " + vwapAligned);
//        System.out.println("TrendAligned: " + (trendUp || trendDown));
//        System.out.println("RangeExpansion: " + rangeExpansion);
//        System.out.println("VWAP: " + vwap);
//        System.out.println("ATR: " + atrVal);
//        System.out.println("AvgVolume: " + avgVolume);
//        System.out.println("Body: " + body);
//        System.out.println("Range: " + range);
//        System.out.println("Bullish: " + bullish);
//        System.out.println("Bearish: " + bearish);
//        System.out.println("Score: " + score);

        if(score < 4)
            return null;

        if(rangeExpansion)
            score += 1;

        if(score < 4)
            return null;

        // LONG breakout
        if(bullish){

            double entry = c1.high * 1.001;

            double stopLoss =
                    entry - (1.5 * atrVal);

            double target =
                    entry + (2 * atrVal);

            return new Signal(
                    symbol,
                    "Momentum Breakout",
                    "LONG",
                    entry,
                    stopLoss,
                    target,
                    score,
                    atrVal
            );
        }

        // SHORT breakdown
        if(bearish){

            double entry = c1.low * 0.999;

            double stopLoss =
                    entry + (1.5 * atrVal);

            double target =
                    entry - (2 * atrVal);

            return new Signal(
                    symbol,
                    "Momentum Breakdown",
                    "SHORT",
                    entry,
                    stopLoss,
                    target,
                    score,
                    atrVal
            );
        }

        return null;
    }
}