package com.alertengine.test;

import com.alertengine.model.TickData;
import com.alertengine.router.SignalRouter;

import java.util.*;
import java.util.concurrent.*;

public class MockTickGenerator {

    private SignalRouter router;

    private Map<String, Double> priceMap = new HashMap<>();
    private Map<String, Double> volumeMap = new HashMap<>();

    private List<String> symbols = Arrays.asList(
            "RELIANCE",
            "INFY",
            "HDFCBANK",
            "ICICIBANK",
            "SBIN",
            "TCS",
            "LT",
            "ITC",
            "AXISBANK",
            "MARUTI"
    );

    public MockTickGenerator(SignalRouter router) {

        this.router = router;

        Random r = new Random();

        for(String s : symbols){

            priceMap.put(
                    s,
                    100 + r.nextDouble()*500
            );

            volumeMap.put(
                    s,
                    100000.0
            );
        }
    }

    public void start(){

        ScheduledExecutorService exec =
                Executors.newScheduledThreadPool(1);

        exec.scheduleAtFixedRate(
                this::generateTicks,
                0,
                50,
                TimeUnit.MILLISECONDS
        );
    }

    private void generateTicks(){

        Random r = new Random();

        for(String symbol : symbols){
            System.out.println("Mock tick generated for " + symbol);

            double price =
                    priceMap.get(symbol);

            double move =
                    (r.nextDouble() - 0.5) * 0.5;

            price += move;

            priceMap.put(symbol, price);

            double volume =
                    volumeMap.get(symbol);

            volume += r.nextInt(500);

            volumeMap.put(symbol, volume);

            TickData tick = new TickData();

            tick.symbol = symbol;
            tick.token = symbol.hashCode();
            tick.price = price;
            tick.volume = volume;
            tick.timestamp = System.currentTimeMillis();

            router.onTick(tick);
        }
    }
}