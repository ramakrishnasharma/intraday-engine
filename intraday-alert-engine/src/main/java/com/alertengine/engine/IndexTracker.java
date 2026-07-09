package com.alertengine.engine;

import java.time.*;

public class IndexTracker {

    private double open = 0;
    private LocalDate tradingDay;

    public double update(double price){

        LocalDate today =
                LocalDate.now(ZoneId.of("Asia/Kolkata"));

        /*
         Reset at start of new trading day
        */
        if(tradingDay == null || !tradingDay.equals(today)){

            tradingDay = today;
            open = price;
        }

        if(open == 0)
            open = price;

        return (price - open) / open;
    }
}