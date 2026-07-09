package com.alertengine.indicator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class VWAPCalculator {

    private Map<String, Double> cumulativePV =
            new HashMap<>();

    private Map<String, Double> cumulativeVolume =
            new HashMap<>();

    private LocalDate tradingDay;

    public double update(String symbol,
                         double price,
                         double volume){

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Kolkata"));

        if(tradingDay == null ||
                !tradingDay.equals(today)){

            cumulativePV.clear();
            cumulativeVolume.clear();

            tradingDay = today;
        }

        double pv =
                cumulativePV.getOrDefault(symbol,0.0);

        double vol =
                cumulativeVolume.getOrDefault(symbol,0.0);

        pv += price * volume;
        vol += volume;

        cumulativePV.put(symbol,pv);
        cumulativeVolume.put(symbol,vol);

        if(vol == 0)
            return price;

        return pv / vol;
    }
}