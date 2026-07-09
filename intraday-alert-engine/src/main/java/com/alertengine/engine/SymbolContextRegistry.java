package com.alertengine.engine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SymbolContextRegistry {

    private Map<String, SymbolContext> contexts =
            new ConcurrentHashMap<>();

    public SymbolContext get(String symbol){

        return contexts.computeIfAbsent(
                symbol,
                s -> {

                    SymbolContext ctx =
                            new SymbolContext();

                    ctx.symbol = symbol;

                    return ctx;
                }
        );
    }

    public Collection<SymbolContext> all(){

        return contexts.values();
    }
}