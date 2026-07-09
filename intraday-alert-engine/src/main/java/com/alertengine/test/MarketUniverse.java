package com.alertengine.test;


import java.util.*;

public class MarketUniverse {

    public static final Map<String, List<String>> SECTOR_STOCKS = Map.of(

            "NIFTY IT", List.of(
                    "INFY",
                    "TCS",
                    "HCLTECH",
                    "WIPRO"
            ),

            "NIFTY BANK", List.of(
                    "HDFCBANK",
                    "ICICIBANK",
                    "AXISBANK",
                    "KOTAKBANK",
                    "SBIN"
            ),

            "NIFTY FMCG", List.of(
                    "ITC",
                    "HINDUNILVR",
                    "NESTLEIND"
            ),

            "NIFTY ENERGY", List.of(
                    "RELIANCE",
                    "ONGC"
            )
    );

}