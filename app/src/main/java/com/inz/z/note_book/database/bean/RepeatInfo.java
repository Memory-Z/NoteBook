package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

/**
 * 重复信息
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/27 11:03.
 */
@Entity(nameInDb = "repeat_info")
public class RepeatInfo {

    @Id
    @Index
    private String repeatId = "";

    /**
     * 重复日期ID  ${@link TaskSchedule}
     */
    private String taskScheduleId = "";
    /**
     * 重复日
     */
    private int repeatDate = -1;
    /**
     * 重复周
     */
    private int repeatWeek = -1;
    /**
     * 重复月
     */
    private int repeatMonth = -1;
    /**
     * 重复年
     */
    private int repeatYear = -1;
    /**
     * 是否启用 0: 不启用； 1： 启用
     */
    private int enable = 0;

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;


    @Generated(hash = 1785270682)
    public RepeatInfo() {
    }

    @Generated(hash = 716709692)
    public RepeatInfo(String repeatId, String taskScheduleId, int repeatDate,
                      int repeatWeek, int repeatMonth, int repeatYear, int enable,
                      Date createDate, Date updateDate) {
        this.repeatId = repeatId;
        this.taskScheduleId = taskScheduleId;
        this.repeatDate = repeatDate;
        this.repeatWeek = repeatWeek;
        this.repeatMonth = repeatMonth;
        this.repeatYear = repeatYear;
        this.enable = enable;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public String getRepeatId() {
        return this.repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public String getTaskScheduleId() {
        return this.taskScheduleId;
    }

    public void setTaskScheduleId(String taskScheduleId) {
        this.taskScheduleId = taskScheduleId;
    }

    public int getRepeatDate() {
        return this.repeatDate;
    }

    public void setRepeatDate(int repeatDate) {
        this.repeatDate = repeatDate;
    }

    public int getRepeatWeek() {
        return this.repeatWeek;
    }

    public void setRepeatWeek(int repeatWeek) {
        this.repeatWeek = repeatWeek;
    }

    public int getRepeatMonth() {
        return this.repeatMonth;
    }

    public void setRepeatMonth(int repeatMonth) {
        this.repeatMonth = repeatMonth;
    }

    public int getRepeatYear() {
        return this.repeatYear;
    }

    public void setRepeatYear(int repeatYear) {
        this.repeatYear = repeatYear;
    }

    public int getEnable() {
        return this.enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "RepeatInfo{" +
                "repeatId='" + repeatId + '\'' +
                ", taskScheduleId='" + taskScheduleId + '\'' +
                ", repeatDate=" + repeatDate +
                ", repeatWeek=" + repeatWeek +
                ", repeatMonth=" + repeatMonth +
                ", repeatYear=" + repeatYear +
                ", enable=" + enable +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }


}
