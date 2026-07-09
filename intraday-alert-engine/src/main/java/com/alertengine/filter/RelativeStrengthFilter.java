package com.alertengine.filter;

public class RelativeStrengthFilter {

    public boolean allow(String side,
                         double stockMove,
                         double indexMove){

        if(side.equals("LONG")){

            return stockMove > indexMove;
        }

        if(side.equals("SHORT")){

            return stockMove < indexMove;
        }

        return false;
    }
}