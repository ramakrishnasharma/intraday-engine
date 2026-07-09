package com.alertengine.engine;

import com.alertengine.model.*;

import java.util.*;

public class PositionManager {

    private Map<String, Position> positions =
            new HashMap<>();

    /*
     Add new position
    */
    public void addPosition(Signal signal){

        if(positions.containsKey(signal.symbol))
            return;

        Position p = new Position();

        p.symbol = signal.symbol;
        p.side = signal.side;

        p.entry = signal.entry;
        p.stopLoss = signal.stopLoss;
        p.target = signal.target;

        p.highestPrice = signal.entry;
        p.lowestPrice = signal.entry;

        p.atr = signal.atr;

        positions.put(p.symbol, p);
    }

    /*
     Check if position exists
    */
    public boolean exists(String symbol){

        return positions.containsKey(symbol);
    }

    /*
     Get position
    */
    public Position get(String symbol){

        return positions.get(symbol);
    }

    /*
     Get all open positions
    */
    public Collection<Position> getAll(){

        return positions.values();
    }

    /*
     Update price extremes for trailing logic
    */
    public void updatePrice(String symbol,
                            double price){

        Position p = positions.get(symbol);

        if(p == null)
            return;

        if(price > p.highestPrice)
            p.highestPrice = price;

        if(price < p.lowestPrice)
            p.lowestPrice = price;
    }

    /*
     Close position
    */
    public void remove(String symbol){

        positions.remove(symbol);
    }
}