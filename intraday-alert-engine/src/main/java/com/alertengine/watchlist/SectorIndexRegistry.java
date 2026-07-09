package com.alertengine.watchlist;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class SectorIndexRegistry {

    static Map<String, String> sectorToIndexMap = Map.ofEntries(
            // --- Sectoral Mappings ---
            entry("Information Technology Services", "NIFTY IT"),
            entry("Banks—Regional", "NIFTY BANK"),
            entry("Credit Services", "NIFTY FIN SERVICE"),
            entry("Insurance—Life", "NIFTY FIN SERVICE"),
            entry("Automobile and Auto Components", "NIFTY AUTO"), // Includes Auto Parts
            entry("Auto Parts", "NIFTY AUTO"),
            entry("Drug Manufacturers—Specialty & Generic", "NIFTY PHARMA"),
            entry("Diagnostics & Research", "NIFTY PHARMA"),
            entry("Steel", "NIFTY METAL"),
            entry("Copper", "NIFTY METAL"),
            entry("Real Estate—Development", "NIFTY REALTY"),
            entry("Real Estate—Diversified", "NIFTY REALTY"),
            entry("Broadcasting", "NIFTY MEDIA"),
            entry("Entertainment", "NIFTY MEDIA"),
            entry("Oil & Gas Refining & Marketing", "NIFTY OIL & GAS"),
            entry("Utilities—Regulated Gas", "NIFTY OIL & GAS"),

            // --- Infrastructure & Energy Mappings ---
            entry("Engineering & Construction", "NIFTY INFRA"),
            entry("Infrastructure Operations", "NIFTY INFRA"),
            entry("Utilities—Regulated Electric", "NIFTY ENERGY"),
            entry("Utilities—Independent Power Producers", "NIFTY ENERGY"),
            entry("Utilities—Renewable", "NIFTY ENERGY"),
            entry("Solar", "NIFTY ENERGY"),

            // --- Thematic & Broad Market Mappings ---
            entry("Specialty Chemicals", "NIFTY CHEMICALS"),
            entry("Agricultural Inputs", "NIFTY COMMODITIES"),
            entry("Airlines", "NIFTY TOURISM"),
            entry("Specialty Industrial Machinery", "NIFTY IND MANUFACTURING"),
            entry("Building Materials", "NIFTY HOUSING"),
            entry("Security & Protection Services", "NIFTY SERVICES SECTOR"),
            entry("Paper & Paper Products", "NIFTY 500") // No specific index, falls under Broad Market

    );


    public static String getSectorIndex(String symbol){

        return sectorToIndexMap.get(symbol);
    }
}