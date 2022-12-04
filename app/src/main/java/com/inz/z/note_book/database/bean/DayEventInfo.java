package com.inz.z.note_book.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 日 事件信息
 * ====================================================
 * Create by 11654 in 2022/6/9 10:29
 */
@Entity(nameInDb = "day_event_info")
public class DayEventInfo {

    @Index
    @Id(autoincrement = true)
    private Long dayEventId = 0L;

    /**
     * 事件内容
     */
    private String eventContent = "";

    /**
     * 事件日期
     */
    private Date eventDate;

    /**
     * 是否显示周期
     */
    private boolean eventShowCycle = false;

    /**
     * 事件周期 时间 day，默认：-1 无周期
     */
    private int eventShowDayCycle = -1;

    /**
     * 事件周期 时间 month，默认：-1 无周期
     */
    private int eventShowMonthCycle = -1;

    /**
     * 事件周期 时间 year，默认：-1 无周期
     */
    private int eventShowYearCycle = -1;

    /**
     * 创建日期
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;
    @Generated(hash = 1593616961)
    public DayEventInfo(Long dayEventId, String eventContent, Date eventDate,
            boolean eventShowCycle, int eventShowDayCycle, int eventShowMonthCycle,
            int eventShowYearCycle, Date createDate, Date updateDate) {
        this.dayEventId = dayEventId;
        this.eventContent = eventContent;
        this.eventDate = eventDate;
        this.eventShowCycle = eventShowCycle;
        this.eventShowDayCycle = eventShowDayCycle;
        this.eventShowMonthCycle = eventShowMonthCycle;
        this.eventShowYearCycle = eventShowYearCycle;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
    @Generated(hash = 1593331591)
    public DayEventInfo() {
    }
    public Long getDayEventId() {
        return this.dayEventId;
    }
    public void setDayEventId(Long dayEventId) {
        this.dayEventId = dayEventId;
    }
    public String getEventContent() {
        return this.eventContent;
    }
    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }
    public Date getEventDate() {
        return this.eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    public boolean getEventShowCycle() {
        return this.eventShowCycle;
    }
    public void setEventShowCycle(boolean eventShowCycle) {
        this.eventShowCycle = eventShowCycle;
    }
    public int getEventShowDayCycle() {
        return this.eventShowDayCycle;
    }
    public void setEventShowDayCycle(int eventShowDayCycle) {
        this.eventShowDayCycle = eventShowDayCycle;
    }
    public int getEventShowMonthCycle() {
        return this.eventShowMonthCycle;
    }
    public void setEventShowMonthCycle(int eventShowMonthCycle) {
        this.eventShowMonthCycle = eventShowMonthCycle;
    }
    public int getEventShowYearCycle() {
        return this.eventShowYearCycle;
    }
    public void setEventShowYearCycle(int eventShowYearCycle) {
        this.eventShowYearCycle = eventShowYearCycle;
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
}
