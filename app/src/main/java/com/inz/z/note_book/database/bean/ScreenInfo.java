package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 屏幕显示信息
 * ====================================================
 * Create by 11654 in 2021/4/3 20:37
 */
@Entity(nameInDb = "screen_info")
public class ScreenInfo {

    @Index
    @Id(autoincrement = true)
    private Long infoId;
    /**
     * 开始时间
     */
    @Nullable
    private Date startTime;
    /**
     * 结束时间
     */
    @Nullable
    private Date endTime;
    /**
     * 解锁时间
     */
    @Nullable
    private Date unlockTime;

    @Generated(hash = 1356902941)
    public ScreenInfo(Long infoId, Date startTime, Date endTime, Date unlockTime) {
        this.infoId = infoId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.unlockTime = unlockTime;
    }

    @Generated(hash = 1053037331)
    public ScreenInfo() {
    }

    public Long getInfoId() {
        return this.infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    @Nullable
    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(@Nullable Date startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(@Nullable Date endTime) {
        this.endTime = endTime;
    }

    @Nullable
    public Date getUnlockTime() {
        return this.unlockTime;
    }

    public void setUnlockTime(@Nullable Date unlockTime) {
        this.unlockTime = unlockTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "ScreenInfo{infoId: " + infoId
                + ", startTime: " + startTime
                + ", endTime: " + endTime
                + ", unlockTime: " + unlockTime
                + "}";
    }
}
