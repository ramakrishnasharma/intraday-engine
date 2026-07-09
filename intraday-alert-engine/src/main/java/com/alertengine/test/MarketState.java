package com.alertengine.test;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MarketState {

    public double niftyPrice = 20000;

    public Map<String, Double> sectorPrices = new HashMap<>();

    public Map<String, Double> stockPrices = new HashMap<>();

    public MarketState() {

        Random r = new Random();

        for (String sector : MarketUniverse.SECTOR_STOCKS.keySet()) {

            sectorPrices.put(sector, (double) (20000 + r.nextInt(5000)));

            for (String stock : MarketUniverse.SECTOR_STOCKS.get(sector)) {

                stockPrices.put(stock, (double) (500 + r.nextInt(2000)));
            }
        }
    }
}
