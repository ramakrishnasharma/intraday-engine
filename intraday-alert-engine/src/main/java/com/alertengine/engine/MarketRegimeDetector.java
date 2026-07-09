package com.alertengine.engine;

import com.alertengine.model.Candle;

public class MarketRegimeDetector {

    public String detect(Candle c5,
                         Candle c15,
                         double atr){

        if(c5 == null || c15 == null)
            return "UNKNOWN";

        double price =
                c15.close;

        double range =
                (c15.high - c15.low) / price;

        double volatility =
                atr / price;

        /*
         VOLATILE MARKET
        */
        if(range > 0.02 || volatility > 0.01)
            return "VOLATILE";

        /*
         TRENDING MARKET
        */
        boolean uptrend =
                c5.close > c5.open &&
                c15.close > c15.open;

        boolean downtrend =
                c5.close < c5.open &&
                c15.close < c15.open;

        if(uptrend || downtrend)
            return "TRENDING";

        /*
         OTHERWISE RANGE
        */
        return "RANGING";
    }
}