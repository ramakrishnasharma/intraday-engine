package com.alertengine.filter;

public class LiquidityFilter {

    public boolean allow(double avgVolume,
                         double price){

        if(avgVolume < 50000)
            return false;

//        if(price < 100)
//            return false;

        return true;
    }
}