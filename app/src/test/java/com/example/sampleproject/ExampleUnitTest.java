package com.example.sampleproject;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.Helper.TimeHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    // Rmb that Calendar.Month starts from 0 lmao
    // And Cal.Day starts from 0


    @Test
    public void testSetDateTimeToZero() {
        Calendar calOne = Calendar.getInstance();
        calOne.set(Calendar.YEAR, 2023);
        calOne.set(Calendar.MONTH, 2);
        calOne.set(Calendar.DAY_OF_MONTH, 11);
        calOne.set(Calendar.HOUR, 23);
        calOne.set(Calendar.MINUTE, 18);
        calOne.set(Calendar.SECOND, 12);
        Date dateOne = TimeHelper.setDateTimeToZero(calOne.getTime()).getTime();

        assertEquals(0, dateOne.getHours());
        assertEquals(0, dateOne.getMinutes());
        assertEquals(0, dateOne.getSeconds());
    }

    @Test
    public void testSetDateTimeDownOne() {
        Calendar calOne = Calendar.getInstance();
        calOne.set(Calendar.YEAR, 2023);
        calOne.set(Calendar.MONTH, 2);
        calOne.set(Calendar.DAY_OF_MONTH, 11);
        calOne.set(Calendar.HOUR, 23);
        calOne.set(Calendar.MINUTE, 18);
        calOne.set(Calendar.SECOND, 12);
        Date dateOne = TimeHelper.setDateTimeOneDown(calOne.getTime()).getTime();

        assertEquals(23, dateOne.getHours());
        assertEquals(59, dateOne.getMinutes());
        assertEquals(59, dateOne.getSeconds());
    }


    @Test
    public void testPositiveDaysBetween() {

        // Init First Date //
        Calendar calOne = Calendar.getInstance();
        calOne.set(Calendar.YEAR, 2023);
        calOne.set(Calendar.MONTH, 2);
        calOne.set(Calendar.DAY_OF_MONTH, 11);
        Date dateOne = calOne.getTime();

        // Init Second Date //
        Calendar calTwo = Calendar.getInstance();
        calTwo.set(Calendar.YEAR, 2022);
        calTwo.set(Calendar.MONTH, 3);
        calTwo.set(Calendar.DAY_OF_MONTH, 4);
        Date dateTwo = calTwo.getTime();

        assertTrue(TimeHelper.calcDaysBetween(dateOne, dateTwo) > 0);
        assertEquals(341, TimeHelper.calcDaysBetween(dateOne, dateTwo));
    }

    @Test
    public void testNegativeDaysBetween() {
        // Rmb that Calendar.Month starts from 0 lmao

        // Init First Date //
        Calendar calOne = Calendar.getInstance();
        calOne.set(Calendar.YEAR, 2022);
        calOne.set(Calendar.MONTH, 2);
        calOne.set(Calendar.DAY_OF_MONTH, 4);
        Date dateOne = calOne.getTime();

        // Init Second Date //
        Calendar calTwo = Calendar.getInstance();
        calTwo.set(Calendar.YEAR, 2022);
        calTwo.set(Calendar.MONTH, 2);
        calTwo.set(Calendar.DAY_OF_MONTH, 11);
        Date dateTwo = calTwo.getTime();

        assertTrue(TimeHelper.calcDaysBetween(dateOne, dateTwo) < 0);
        assertEquals(-7, TimeHelper.calcDaysBetween(dateOne, dateTwo));
    }

    @Test
    public void testMakeStringDate() {
        Calendar calOne = Calendar.getInstance();
        calOne.set(Calendar.YEAR, 2023);
        calOne.set(Calendar.MONTH, 2);
        calOne.set(Calendar.DAY_OF_MONTH, 11);
        Date dateOne = calOne.getTime();

        int day = dateOne.getDate();
        int month = dateOne.getMonth();
        int year = dateOne.getYear();

        assertEquals("12 MAR 2023", CalendarPickerDialog.makeDateString(day+1, month+1, year+1900));
    }

//    @Test
//    public void testGetMonthFormat() {
//        int month = 6;
//        assertEquals("JUN", CalendarPickerDialog.getMonthFormat);
//    }

}