package com.alertengine.engine;

import java.util.*;

public class CooldownEngine {

    private Map<String,Long> lastSignal = new HashMap<>();

    public synchronized boolean allow(String symbol){

        long now = System.currentTimeMillis();

        Long last = lastSignal.get(symbol);

        if(last == null){

            lastSignal.put(symbol, now);
            return true;
        }

        long diff = now - last;

        // 10 minutes cooldown
        if(diff > 300000){

            lastSignal.put(symbol, now);
            return true;
        }

        return false;
    }

    public synchronized void reset(String symbol){
        lastSignal.remove(symbol);
    }
}