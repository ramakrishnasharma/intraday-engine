package com.alertengine.engine;

import java.time.*;
import java.util.*;

public class SectorTracker {

    private Map<String,Double> openPrice =
            new HashMap<>();

    private Map<String,Double> move =
            new HashMap<>();

    private Map<String,LocalDate> tradingDay =
            new HashMap<>();

    public void update(String sectorIndex,double price){

        if(price <= 0)
            return;

        LocalDate today =
                LocalDate.now(ZoneId.of("Asia/Kolkata"));

        LocalDate storedDay =
                tradingDay.get(sectorIndex);

        /*
         Reset open price on new trading day
        */

        if(storedDay == null || !storedDay.equals(today)){

            tradingDay.put(sectorIndex,today);
            openPrice.put(sectorIndex,price);
        }

        openPrice.putIfAbsent(sectorIndex,price);

        double open =
                openPrice.get(sectorIndex);

        if(open == 0)
            return;

        double sectorMove =
                (price - open) / open;

        move.put(sectorIndex,sectorMove);
    }

    public double getMove(String sectorIndex){

        return move.getOrDefault(sectorIndex,0.0);
    }
}