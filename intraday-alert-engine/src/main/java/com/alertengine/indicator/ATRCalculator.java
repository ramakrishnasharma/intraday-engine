package com.alertengine.indicator;

import com.alertengine.model.Candle;

import java.util.*;

public class ATRCalculator {

    private LinkedList<Double> trList =
            new LinkedList<>();

    private double prevClose = 0;

    public double update(Candle candle){

        double tr;

        if(prevClose == 0){

            tr = candle.high - candle.low;

        } else {

            double hl = candle.high - candle.low;
            double hc = Math.abs(candle.high - prevClose);
            double lc = Math.abs(candle.low - prevClose);

            tr = Math.max(
                    hl,
                    Math.max(hc, lc)
            );
        }

        prevClose = candle.close;

        trList.add(tr);

        if(trList.size() > 14)
            trList.removeFirst();

        double sum = 0;

        for(double v : trList)
            sum += v;

        return sum / trList.size();
    }
}