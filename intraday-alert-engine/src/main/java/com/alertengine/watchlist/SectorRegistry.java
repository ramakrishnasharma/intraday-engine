package com.alertengine.watchlist;

import java.io.*;
import java.util.*;

public class SectorRegistry {

    private static Map<String,String> symbolSectorMap =
            new HashMap<>();

    public static void load(String filePath) {

        try(BufferedReader br =
                    new BufferedReader(
                            new FileReader(filePath))) {

            String line;

            br.readLine(); // skip header

            while((line = br.readLine()) != null){

                String[] parts = line.split(",", -1);

                if(parts.length < 2)
                    continue;

                String symbol = parts[0].trim();

                if(symbol.endsWith(".NS"))
                    symbol = symbol.replace(".NS","");
                String sector = parts[1].trim();

                symbolSectorMap.put(symbol, sector);
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getSector(String symbol){

        if(symbol == null)
            return null;

        return symbolSectorMap.get(symbol);
    }
}