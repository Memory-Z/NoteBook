package com.inz.z.note_book.database.bean.local;

import androidx.annotation.NonNull;

/**
 * 任务 重复信息
 * ====================================================
 * Create by 11654 in 2021/10/14 21:44
 */
public class TaskRepeatInfo {
    /**
     * 重复日期
     */
    private int repeatDate = 0;
    /**
     * 重复周期
     */
    private int repeatDuration = 0;

    public int getRepeatDate() {
        return repeatDate;
    }

    public void setRepeatDate(int repeatDate) {
        this.repeatDate = repeatDate;
    }

    public int getRepeatDuration() {
        return repeatDuration;
    }

    public void setRepeatDuration(int repeatDuration) {
        this.repeatDuration = repeatDuration;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskRepeatInfo{" +
                "repeatDate=" + repeatDate +
                ", repeatDuration=" + repeatDuration +
                '}';
    }
}
