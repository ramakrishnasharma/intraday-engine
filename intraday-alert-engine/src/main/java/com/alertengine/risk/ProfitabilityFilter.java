package com.alertengine.risk;

import com.alertengine.model.Signal;

public class ProfitabilityFilter {

    private TradeCostModel costModel =
            new TradeCostModel();

    // minimum target move
    private static final double MIN_MOVE = 0.0015; // 0.15% // previous 0.003

    // minimum reward/risk
    private static final double MIN_RR = 1.5;

    public boolean allow(Signal signal){

        if(signal == null)
            return false;

        double entry = signal.entry;
        double target = signal.target;
        double stop = signal.stopLoss;

        double move =
                Math.abs(target - entry) / entry;

        if(move < MIN_MOVE)
            return false;

        double risk =
                Math.abs(entry - stop);

        double reward =
                Math.abs(target - entry);

        if(risk == 0)
            return false;

        double rr =
                reward / risk;

        if(rr < MIN_RR)
            return false;

        double cost =
                costModel.estimateCost(entry);

        if(reward <= cost)
            return false;

        return true;
    }
}