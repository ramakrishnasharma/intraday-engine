package com.alertengine.engine;

import com.alertengine.model.*;

public class MomentumMonitor {

    public String check(Position pos,
                        double price){

        if(pos == null || !pos.active)
            return null;

        if(pos.side.equals("LONG")){

            if(price >= pos.target){

                pos.active = false;

                return "🎯 TARGET HIT";
            }

            if(price <= pos.stopLoss){

                pos.active = false;

                return "⛔ STOP LOSS HIT";
            }
        }

        if(pos.side.equals("SHORT")){

            if(price <= pos.target){

                pos.active = false;

                return "🎯 TARGET HIT";
            }

            if(price >= pos.stopLoss){

                pos.active = false;

                return "⛔ STOP LOSS HIT";
            }
        }

        return null;
    }
}