package com.alertengine.engine;

import com.alertengine.model.Signal;

import java.util.*;

public class RankingEngine {

    private List<Signal> signals = new ArrayList<>();

    public void add(Signal signal){

        signals.add(signal);
    }

    public List<Signal> top(int count){

        signals.sort((a,b) -> b.score - a.score);

        return signals.subList(
                0,
                Math.min(count,signals.size())
        );
    }

    public void clear(){

        signals.clear();
    }
}