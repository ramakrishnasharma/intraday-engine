package com.alertengine.engine;

import com.alertengine.indicator.*;
import com.alertengine.model.*;

public class SymbolContext {

    public String symbol;

    public VWAPCalculator vwap =
            new VWAPCalculator();

    public VolumeAverageCalculator avgVolume =
            new VolumeAverageCalculator();

    public VolatilityDetector volatility =
            new VolatilityDetector();

    public ATRCalculator atr =
            new ATRCalculator();

    public RelativeStrengthCalculator rs =
            new RelativeStrengthCalculator();

    public Position position;

}