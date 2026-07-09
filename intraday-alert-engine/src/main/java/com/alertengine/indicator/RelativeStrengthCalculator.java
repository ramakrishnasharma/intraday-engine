package com.alertengine.indicator;

import java.util.*;

public class RelativeStrengthCalculator {

    private Map<String, Double> openPrice =
            new HashMap<>();

    public double update(String symbol,
                         double price){

        openPrice.putIfAbsent(symbol, price);

        double open = openPrice.get(symbol);

        return (price - open) / open;
    }
}