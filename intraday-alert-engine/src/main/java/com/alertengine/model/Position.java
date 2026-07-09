package com.alertengine.model;

public class Position {

    public String symbol;

    public String side;   // LONG or SHORT

    public double entry;

    public double stopLoss;

    public double target;

    public double highestPrice;
    public double lowestPrice;

    public double atr;

    public boolean active = true;

}