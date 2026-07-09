package com.alertengine.indicator;

import java.util.*;

public class VolumeAverageCalculator {

    private Map<String, LinkedList<Double>> volumeHistory =
            new HashMap<>();

    private int period = 20; // 20 candles average

    public double update(String symbol,
                         double volume){

        LinkedList<Double> list =
                volumeHistory.computeIfAbsent(
                        symbol,
                        s -> new LinkedList<>()
                );

        list.add(volume);

        if(list.size() > period)
            list.removeFirst();

        double sum = 0;

        for(double v : list)
            sum += v;

        return sum / list.size();
    }
}