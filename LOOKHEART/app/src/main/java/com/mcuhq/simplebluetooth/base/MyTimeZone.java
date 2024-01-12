package com.mcuhq.simplebluetooth.base;

import android.app.Activity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class MyTimeZone {
    private static MyTimeZone instance;

    public static MyTimeZone getInstance() {
        if ( instance == null )
            instance = new MyTimeZone();
        return instance;
    }

    public String getTimeZone(Activity activity){
        // 국가 코드
        Locale current = activity.getResources().getConfiguration().getLocales().get(0);
        String currentCountry = current.getCountry();
        String utcOffset;

        // 현재 시스템의 기본 타임 존
        java.util.TimeZone currentTimeZone = java.util.TimeZone.getDefault();

        // 타임 존의 아이디
        String timeZoneId = currentTimeZone.getID();

        ZoneId zoneId = ZoneId.of(timeZoneId);
        ZoneOffset offset = LocalDateTime.now().atZone(zoneId).getOffset();

        String utcTime = String.valueOf(offset);

        String firstChar = String.valueOf(utcTime.charAt(0));

        if (firstChar.equals("+") || firstChar.equals("-"))
            utcOffset = utcTime;
        else
            utcOffset = "+" + utcTime;

        return utcOffset + "/" + timeZoneId +"/" + currentCountry;
    }

    public String getCurrentUtcTime(){
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        TimeZone currentTimeZone = TimeZone.getDefault();
        String timeZoneId = currentTimeZone.getID();
        ZonedDateTime currentTimezone = now.withZoneSameInstant(ZoneId.of(timeZoneId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        return formatter.format(currentTimezone);
    }

}
