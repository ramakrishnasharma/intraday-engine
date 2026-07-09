package com.alertengine.filter;

import com.alertengine.model.Candle;

public class TrendFilter {

    public boolean isUptrend(Candle c5,
                             Candle c15){

        if(c5 == null || c15 == null)
            return false;

        return c5.close > c5.open &&
               c15.close > c15.open;
    }

    public boolean isDowntrend(Candle c5,
                               Candle c15){

        if(c5 == null || c15 == null)
            return false;

        return c5.close < c5.open &&
               c15.close < c15.open;
    }
}