package com.alertengine.watchlist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TokenRegistry {

    private static Map<Long,String> tokenSymbol =
            new ConcurrentHashMap<>();

    public static void register(long token, String symbol){

        tokenSymbol.put(token, symbol);
    }

    public static String getSymbol(long token){

        return tokenSymbol.get(token);
    }
}