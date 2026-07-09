package com.alertengine.model;

public class Signal {

    public String symbol;
    public String strategy;
    public String side;

    public double entry;
    public double stopLoss;
    public double target;

    public int score;

    public double atr;

    public int quantity;

    public Signal(String symbol,
                  String strategy,
                  String side,
                  double entry,
                  double stopLoss,
                  double target,
                  int score,
                  double atr){

        this.symbol = symbol;
        this.strategy = strategy;
        this.side = side;
        this.entry = entry;
        this.stopLoss = stopLoss;
        this.target = target;
        this.score = score;
        this.atr = atr;
    }

    @Override
    public String toString() {
        return "Signal{" +
                "symbol='" + symbol + '\'' +
                ", side='" + side + '\'' +
                ", strategy='" + strategy + '\'' +
                ", entry=" + entry +
                ", stopLoss=" + stopLoss +
                ", target=" + target +
                ", score=" + score +
                '}';
    }
}