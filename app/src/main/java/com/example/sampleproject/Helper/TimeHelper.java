package com.example.sampleproject.Helper;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeHelper {

    public static Calendar setDateTimeToZero(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    public static Calendar setDateTimeOneDown(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,-1);
        return cal;
    }

    public static long calcDaysBetween(Date date1, Date date2) {
        long daysBetween = TimeUnit.DAYS.convert(date1.getTime()- date2.getTime(), TimeUnit.MILLISECONDS);
        return daysBetween;
    }
}
