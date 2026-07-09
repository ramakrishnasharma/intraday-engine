package com.alertengine.util;

import java.time.*;

public class MarketTimeUtil {

    private static final ZoneId ZONE =
            ZoneId.of("Asia/Kolkata");

    /*
     Convert timestamp → minute of day
    */
    public static int minuteOfDay(long timestamp){

        LocalDateTime time =
                Instant.ofEpochMilli(timestamp)
                        .atZone(ZONE)
                        .toLocalDateTime();

        return time.getHour()*60 + time.getMinute();
    }

    /*
     Market open check
    */
    public static boolean isMarketOpen(long timestamp){

        int minute = minuteOfDay(timestamp);

        int open = 9*60 + 15;
        int close = 15*60 + 30;

        return minute >= open && minute <= close;
    }

    /*
     Opening range (9:15 → 9:30)
    */
    public static boolean isOpeningRange(int minute){

        int open = 9*60 + 15;
        int rangeEnd = 9*60 + 30;

        return minute >= open && minute < rangeEnd;
    }

    /*
     After opening range
    */
    public static boolean afterOpeningRange(int minute){

        int rangeEnd = 9*60 + 30;

        return minute >= rangeEnd;
    }

    /*
     Avoid trades near market close
    */
    public static boolean nearMarketClose(int minute){

        int cutoff = 15*60 + 15;

        return minute >= cutoff;
    }
}