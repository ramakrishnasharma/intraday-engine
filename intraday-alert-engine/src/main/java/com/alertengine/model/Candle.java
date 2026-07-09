package com.alertengine.model;

public class Candle {

    public long token;

    public String symbol;

    public double open;
    public double high;
    public double low;
    public double close;

    public double volume;

    // start time of candle bucket
    public long startTime;

    public long endTime;

}