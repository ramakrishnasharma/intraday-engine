package com.alertengine.model;

public class CandleBundle {


    public Candle c1m;
    public Candle c5m;
    public Candle c15m;

    public Candle prev1m;

    // true when a new 1m candle has closed
    public boolean newCandle;

}