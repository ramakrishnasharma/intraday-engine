package com.alertengine.risk;

import com.alertengine.model.Signal;

public class PositionSizer {

    private double capital;

    // risk per trade (1%)
    private double riskPercent = 0.01;

    // max capital allocation per trade (20%)
    private double maxCapitalPercent = 0.20;

    // minimum SL distance protection
    private static final double MIN_SL_RATIO = 0.002;

    public PositionSizer(double capital){
        this.capital = capital;
    }

    public int calculateQuantity(Signal signal){

        if(signal == null)
            return 0;

        double entry = signal.entry;
        double stop = signal.stopLoss;

        double riskPerShare =
                Math.abs(entry - stop);

        if(riskPerShare <= 0)
            return 0;

        /*
         SL safety check
        */

        double slRatio =
                riskPerShare / entry;

        if(slRatio < MIN_SL_RATIO)
            return 0;

        /*
         Risk-based sizing
        */

        double riskPerTrade =
                capital * riskPercent;

        int qtyByRisk =
                (int)(riskPerTrade / riskPerShare);

        /*
         Capital limit
        */

        double maxCapital =
                capital * maxCapitalPercent;

        int qtyByCapital =
                (int)(maxCapital / entry);

        int qty =
                Math.min(qtyByRisk, qtyByCapital);

        if(qty < 1)
            return 0;

        return qty;
    }
}