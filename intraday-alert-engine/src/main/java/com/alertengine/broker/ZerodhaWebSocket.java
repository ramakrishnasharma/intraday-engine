package com.alertengine.broker;

import com.alertengine.engine.TickQueueEngine;
import com.alertengine.watchlist.SectorIndexRegistry;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.User;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

import com.alertengine.router.SignalRouter;
import com.alertengine.model.TickData;
import com.alertengine.watchlist.TokenRegistry;

import java.util.ArrayList;

public class ZerodhaWebSocket {

    private KiteTicker ticker;
    private SignalRouter router;

    private String apiKey;
    private String apiSecret;
    private String requestToken;

    private ArrayList<Long> tokens;

    private TickQueueEngine tickQueue;


    public ZerodhaWebSocket(String apiKey,
                            String apiSecret,
                            String requestToken,
                            ArrayList<Long> tokens,
                            SignalRouter router,
                            TickQueueEngine tickQueue){

        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.requestToken = requestToken;
        this.tokens = tokens;
        this.router = router;
        this.tickQueue = tickQueue;
    }

    public void connect() throws KiteException {

        try{

            KiteConnect kite = new KiteConnect(apiKey);

            User user =
                    kite.generateSession(
                            requestToken,
                            apiSecret
                    );

            kite.setAccessToken(user.accessToken);
            kite.setPublicToken(user.publicToken);

            System.out.println("Logged in as: " + user.userName);

            ticker = new KiteTicker(kite.getAccessToken(), kite.getApiKey());

            ticker.setTryReconnection(true);
            ticker.setMaximumRetries(10);
            ticker.setMaximumRetryInterval(30);

            ticker.setOnConnectedListener(() -> {

                System.out.println("Connected to Zerodha WebSocket");

                ticker.subscribe(tokens);
                ticker.setMode(tokens, KiteTicker.modeFull);

                System.out.println("Subscribed to "+tokens.size()+" stocks");
            });

            ticker.setOnTickerArrivalListener(ticks -> {

                for(Tick t : ticks){

                    try{

                        System.out.println(
                                "Tick: token=" + t.getInstrumentToken() +
                                        " price=" + t.getLastTradedPrice()
                        );
                        if(t.getLastTradedPrice() == 0)
                            continue;
                        TickData tick = new TickData();

                        tick.token = t.getInstrumentToken();
                        tick.price = t.getLastTradedPrice();
                        tick.volume = t.getVolumeTradedToday();
                        tick.indexPrice = tick.price;

                        tick.symbol =
                                TokenRegistry.getSymbol(tick.token);

                        if(tick.symbol == null)
                            continue;

                        // detect sector index ticks

                        if(tick.symbol.equals("NIFTY")){
                            router.updateIndex(tick.price);
                            continue;
                        }

                        if(tick.symbol.startsWith("NIFTY ")){
                            router.updateSector(tick.symbol,tick.price);
                            continue;
                        }


                        if(t.getLastTradedTime()!=null){
                            tick.timestamp =
                                    t.getLastTradedTime().getTime();
                        }else{
                            tick.timestamp =
                                    System.currentTimeMillis();
                        }

                        tickQueue.push(tick);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            ticker.connect();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}