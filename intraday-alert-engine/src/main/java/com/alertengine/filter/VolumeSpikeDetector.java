package com.alertengine.filter;

import java.util.LinkedList;

public class VolumeSpikeDetector {

    private LinkedList<Double> history =
            new LinkedList<>();

    private static final int PERIOD = 20;

    public boolean isSpike(double volume){

        history.add(volume);

        if(history.size() > PERIOD)
            history.removeFirst();

        if(history.size() < PERIOD)
            return false;

        double sum = 0;

        for(double v : history)
            sum += v;

        double avg = sum / PERIOD;

        if(avg == 0)
            return false;

        return volume > (avg);
    }
}