package com.alertengine.watchlist;

import java.io.*;
import java.net.*;
import java.util.*;

public class WatchlistLoader {

    public ArrayList<Long> load(){

    	ArrayList<Long> tokens = new ArrayList<>();

        try{

            URL url =
                    new URL("https://api.kite.trade/instruments");

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    url.openStream()));

            String line;

            reader.readLine(); // skip header

            while((line = reader.readLine()) != null){

                String[] cols = line.split(",");

                long token =
                        Long.parseLong(cols[0]);

                String symbol = cols[2];
                String exchange = cols[11];

                if(exchange.equals("NSE") &&
                        cols[9].equals("EQ") &&
                        !Character.isDigit(symbol.charAt(0))) {

                    tokens.add(token);

                    TokenRegistry.register(
                            token,
                            symbol
                    );

                    if(tokens.size() >= 2500)
                        break;
                }
            }

            reader.close();

        }catch(Exception e){

            e.printStackTrace();
        }

        System.out.println("Loaded tokens: "+tokens.size());

        return tokens;
    }
}