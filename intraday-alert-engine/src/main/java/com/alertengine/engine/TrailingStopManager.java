package com.alertengine.engine;

import com.alertengine.model.*;

public class TrailingStopManager {

    private double atrMultiplier = 1.2;

    public String update(Position pos,
                         double price){

        if(pos == null || !pos.active)
            return null;

        if(pos.side.equals("LONG")){

            if(price > pos.highestPrice)
                pos.highestPrice = price;

            double newSL =
                    pos.highestPrice -
                    (pos.atr * atrMultiplier);

            if(newSL > pos.stopLoss){

                pos.stopLoss = newSL;

                return "🔁 TRAILING SL → " + round(newSL);
            }

            if(price <= pos.stopLoss){

                pos.active = false;

                return "⛔ STOP LOSS HIT";
            }

            if(price >= pos.target){

                pos.active = false;

                return "🎯 TARGET HIT";
            }
        }

        if(pos.side.equals("SHORT")){

            if(price < pos.lowestPrice)
                pos.lowestPrice = price;

            double newSL =
                    pos.lowestPrice +
                    (pos.atr * atrMultiplier);

            if(newSL < pos.stopLoss){

                pos.stopLoss = newSL;

                return "🔁 TRAILING SL → " + round(newSL);
            }

            if(price >= pos.stopLoss){

                pos.active = false;

                return "⛔ STOP LOSS HIT";
            }

            if(price <= pos.target){

                pos.active = false;

                return "🎯 TARGET HIT";
            }
        }

        return null;
    }

    private double round(double v){

        return Math.round(v * 100.0) / 100.0;
    }
}