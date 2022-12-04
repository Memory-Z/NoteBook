package com.inz.z.base.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/09/16 11:52.
 */
public class BaseToolsTest {
    @Test
    public  void tesssst() {
        System.out.println("1111");
    }

    @Test
    public void testDifDay() {
        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
        calendar2.set(2023, 6, 1);

        System.out.println("---------->> " + BaseTools.getDiffDay(calendar2 , calendar1));
    }

    @Test
    public void testZeroCalendar() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        date.setMonth(11);
        date.setDate(32);
        Calendar calendar = BaseTools.getDateZeroCalendar(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);

        DateFormat dateFormat = BaseTools.getBaseDateFormat();
        String dateStr = dateFormat.format(calendar.getTime());
        System.out.println("--->> dateStr == " + dateStr);
    }
}