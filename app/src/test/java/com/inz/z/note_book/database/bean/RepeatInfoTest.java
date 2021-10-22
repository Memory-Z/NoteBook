package com.inz.z.note_book.database.bean;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * ====================================================
 * Create by 11654 in 2021/10/22 22:35
 */
public class RepeatInfoTest extends TestCase {

    public void testGetCustomRepeatDataArray() {
        RepeatInfo repeatInfo = new RepeatInfo();
        repeatInfo.setDuration(10000);
        int[] array = repeatInfo.getCustomRepeatDataArray();
        System.out.println("------------>> " + Arrays.toString(array));
        array = new int[] {1, 0, 0, 1};
        repeatInfo.setCustomRepeatDate(array);
        System.out.println("------------>> " + repeatInfo.getDuration());
    }

}