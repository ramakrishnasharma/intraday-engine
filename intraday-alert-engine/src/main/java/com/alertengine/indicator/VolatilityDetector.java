package com.alertengine.indicator;

import java.util.*;

public class VolatilityDetector {

    private Map<String, LinkedList<Double>> prices =
            new HashMap<>();

    public int getOptimalTimeframe(String symbol,
                                   double price){

        LinkedList<Double> list =
                prices.computeIfAbsent(
                        symbol,
                        s -> new LinkedList<>());

        list.add(price);

        if(list.size() > 20)
            list.removeFirst();

        if(list.size() < 10)
            return 1;

        double min = Collections.min(list);
        double max = Collections.max(list);

        double range =
                (max - min) / price;

        if(range > 0.01)
            return 1;

        if(range > 0.005)
            return 3;

        return 5;
    }
}