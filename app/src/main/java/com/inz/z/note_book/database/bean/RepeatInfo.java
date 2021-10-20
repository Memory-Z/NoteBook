package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import com.inz.z.note_book.base.RepeatType;
import com.inz.z.note_book.base.TaskValue;

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
 * -----------------------------------------
 * 更新: laptop 3 ,  2021/10/19 23:18.
 * 修改重复类型：
 * <hr/>
 * 如果 重复日：根据 {@link #duration}
 * 如果 重复周：根据 {@link #repeatWeek}
 * 如果 重复月/农历月：根据 {@link #repeatDate}
 * 如果 重复年/农历年：根据 {@link #repeatMonth} {@link #repeatDate} 或 {@link #repeatYear}
 * 如果 自定义重复： 根据 {@link #duration} 或  {@link #repeatMonth} {@link #repeatDate}
 * @see com.inz.z.note_book.base.RepeatType
 */
@Entity(nameInDb = "repeat_info")
public class RepeatInfo {

    @Id
    @Index
    private String repeatId = "";

    /**
     * 重复日期ID  ${@link TaskSchedule}
     */
    @Index
    private String taskScheduleId = "";

    /**
     * 重复类型
     */
    @RepeatType
    private int repeatType = TaskValue.TASK_REPEAT_TYPE_NONE;
    /**
     * 重复分钟 具体分钟 0-59
     */
    private int repeatMin = -1;
    /**
     * 重复小时 具体 小时 0—23
     */
    private int repeatHour = -1;
    /**
     * 重复日 具体 日期
     */
    private int repeatDate = -1;
    /**
     * 重复周， 具体 星期
     */
    private int repeatWeek = -1;
    /**
     * 重复月 具体 月份
     */
    private int repeatMonth = -1;
    /**
     * 重复年， 间隔年
     */
    private int repeatYear = -1;
    /**
     * 是否启用 0: 不启用； 1： 启用
     */
    private int enable = 0;
    /**
     * 重复间隔 默认 单位: 分钟
     */
    private long duration = -1;

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;


    @Generated(hash = 367818410)
    public RepeatInfo(String repeatId, String taskScheduleId, int repeatType,
                      int repeatMin, int repeatHour, int repeatDate, int repeatWeek,
                      int repeatMonth, int repeatYear, int enable, long duration,
                      Date createDate, Date updateDate) {
        this.repeatId = repeatId;
        this.taskScheduleId = taskScheduleId;
        this.repeatType = repeatType;
        this.repeatMin = repeatMin;
        this.repeatHour = repeatHour;
        this.repeatDate = repeatDate;
        this.repeatWeek = repeatWeek;
        this.repeatMonth = repeatMonth;
        this.repeatYear = repeatYear;
        this.enable = enable;
        this.duration = duration;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1785270682)
    public RepeatInfo() {
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

    public int getRepeatMin() {
        return this.repeatMin;
    }

    public void setRepeatMin(int repeatMin) {
        this.repeatMin = repeatMin;
    }

    public int getRepeatHour() {
        return this.repeatHour;
    }

    public void setRepeatHour(int repeatHour) {
        this.repeatHour = repeatHour;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @RepeatType
    public int getRepeatType() {
        return this.repeatType;
    }

    public void setRepeatType(@RepeatType int repeatType) {
        this.repeatType = repeatType;
    }

    @NonNull
    @Override
    public String toString() {
        return "RepeatInfo{" +
                "repeatId='" + repeatId + '\'' +
                ", taskScheduleId='" + taskScheduleId + '\'' +
                ", repeatType=" + repeatType +
                ", repeatMin=" + repeatMin +
                ", repeatHour=" + repeatHour +
                ", repeatDate=" + repeatDate +
                ", repeatWeek=" + repeatWeek +
                ", repeatMonth=" + repeatMonth +
                ", repeatYear=" + repeatYear +
                ", enable=" + enable +
                ", duration=" + duration +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
