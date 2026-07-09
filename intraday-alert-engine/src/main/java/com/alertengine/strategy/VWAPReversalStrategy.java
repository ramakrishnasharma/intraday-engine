package com.alertengine.strategy;

import com.alertengine.model.*;
import com.alertengine.indicator.*;
import com.alertengine.engine.*;

import java.util.*;

public class VWAPReversalStrategy {

    private Map<String, ATRCalculator> atrMap =
            new HashMap<>();

    private ScoreEngine scorer =
            new ScoreEngine();

    public Signal evaluate(Candle candle,
                           double vwap){

        if(candle == null)
            return null;

        String symbol = candle.symbol;

        ATRCalculator atr =
                atrMap.computeIfAbsent(
                        symbol,
                        s -> new ATRCalculator()
                );

        double atrVal =
                atr.update(candle);

        boolean longReversal =
                candle.low < vwap &&
                        candle.close > vwap;

        boolean shortReversal =
                candle.high > vwap &&
                        candle.close < vwap;

        double body =
                Math.abs(candle.close - candle.open);

        boolean momentum =
                body / candle.close > 0.002;

        boolean vwapDistance =
                Math.abs(candle.close - vwap) / vwap > 0.001;

        boolean volumeSpike =
                candle.volume > 0;

        int score =
                scorer.calculate(
                        momentum,
                        volumeSpike,
                        vwapDistance,
                        true
                );

        if(score < 4)
            return null;

        if(longReversal){

            double entry = candle.close;
            double sl = entry - (1.2 * atrVal);
            double target = entry + (2 * atrVal);

            return new Signal(
                    symbol,
                    "VWAP Reversal",
                    "LONG",
                    entry,
                    sl,
                    target,
                    score,
                    atrVal
            );
        }

        if(shortReversal){

            double entry = candle.close;
            double sl = entry + (1.2 * atrVal);
            double target = entry - (2 * atrVal);

            return new Signal(
                    symbol,
                    "VWAP Reversal",
                    "SHORT",
                    entry,
                    sl,
                    target,
                    score,
                    atrVal
            );
        }

        return null;
    }
}