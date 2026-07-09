package com.alertengine.risk;

public class TradeCostModel {

    // estimated round-trip trading cost
    // brokerage + STT + exchange + GST etc

    private static final double COST_RATIO = 0.0007; // ~0.07%

    public double estimateCost(double price){

        return price * COST_RATIO;
    }

}