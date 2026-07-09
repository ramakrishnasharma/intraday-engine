package com.alertengine.engine;

public class ScoreEngine {

    public int calculate(boolean momentum,
                         boolean volumeSpike,
                         boolean vwapAligned,
                         boolean trendAligned){

        int score = 0;

        if(momentum) score += 3;
        if(volumeSpike) score += 3;
        if(vwapAligned) score += 2;
        if(trendAligned) score += 2;

        return score;
    }
}