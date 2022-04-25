package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * 设置 桌面壁纸信息
 * <p>
 * ====================================================
 * Create by 11654 in 2021/11/7 20:36
 */
@Entity(nameInDb = "desktop_wallpaper_info")
public class DesktopWallpaperInfo {

    @Index
    @Id(autoincrement = true)
    private Long wallpaperId;
    /**
     * 壁纸地址
     */
    private String wallpaperPath = "";

    /**
     * 壁纸描述
     */
    private String wallpaperDescription = "";

    /**
     * 壁纸大小
     */
    private Long wallpaperSize = 0L;
    /**
     * 是否启用
     */
    private boolean enable = true;

    /**
     * 是否重复
     */
    private boolean isRepeat = false;

    /**
     * 是否为当前设置壁纸
     */
    private boolean isCurrentWallpaper = false;

    /**
     * 壁纸显示区域。如： “1， 0， 2， 1”
     */
    private String wallpaperRect = "";

    /**
     * 壁纸 来源 【暂不处理】
     */
    private String wallpaperFrom = "";

    /**
     * 开始 时间
     */
    private Date startTime;


    private Date createTime;
    private Date updateTime;

    @Transient
    private boolean isEmptyData = false;


    @Generated(hash = 1459373151)
    public DesktopWallpaperInfo(Long wallpaperId, String wallpaperPath,
                                String wallpaperDescription, Long wallpaperSize, boolean enable,
                                boolean isRepeat, boolean isCurrentWallpaper, String wallpaperRect,
                                String wallpaperFrom, Date startTime, Date createTime,
                                Date updateTime) {
        this.wallpaperId = wallpaperId;
        this.wallpaperPath = wallpaperPath;
        this.wallpaperDescription = wallpaperDescription;
        this.wallpaperSize = wallpaperSize;
        this.enable = enable;
        this.isRepeat = isRepeat;
        this.isCurrentWallpaper = isCurrentWallpaper;
        this.wallpaperRect = wallpaperRect;
        this.wallpaperFrom = wallpaperFrom;
        this.startTime = startTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 547809302)
    public DesktopWallpaperInfo() {
    }


    public Long getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public String getWallpaperPath() {
        return wallpaperPath;
    }

    public void setWallpaperPath(String wallpaperPath) {
        this.wallpaperPath = wallpaperPath;
    }

    public String getWallpaperDescription() {
        return wallpaperDescription;
    }

    public void setWallpaperDescription(String wallpaperDescription) {
        this.wallpaperDescription = wallpaperDescription;
    }

    public Long getWallpaperSize() {
        return wallpaperSize;
    }

    public void setWallpaperSize(Long wallpaperSize) {
        this.wallpaperSize = wallpaperSize;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public boolean isCurrentWallpaper() {
        return isCurrentWallpaper;
    }

    public void setCurrentWallpaper(boolean currentWallpaper) {
        isCurrentWallpaper = currentWallpaper;
    }

    public String getWallpaperRect() {
        return wallpaperRect;
    }

    public void setWallpaperRect(String wallpaperRect) {
        this.wallpaperRect = wallpaperRect;
    }

    public String getWallpaperFrom() {
        return wallpaperFrom;
    }

    public void setWallpaperFrom(String wallpaperFrom) {
        this.wallpaperFrom = wallpaperFrom;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public boolean getIsRepeat() {
        return this.isRepeat;
    }

    public void setIsRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public boolean getIsCurrentWallpaper() {
        return this.isCurrentWallpaper;
    }

    public void setIsCurrentWallpaper(boolean isCurrentWallpaper) {
        this.isCurrentWallpaper = isCurrentWallpaper;
    }

    public boolean isEmptyData() {
        return isEmptyData;
    }

    public void setEmptyData(boolean emptyData) {
        isEmptyData = emptyData;
    }

    @NonNull
    @Override
    public String toString() {
        return "DesktopWallpaperInfo{" +
                "wallpaperId=" + wallpaperId +
                ", wallpaperPath='" + wallpaperPath + '\'' +
                ", wallpaperDescription='" + wallpaperDescription + '\'' +
                ", wallpaperSize=" + wallpaperSize +
                ", enable=" + enable +
                ", isRepeat=" + isRepeat +
                ", isCurrentWallpaper=" + isCurrentWallpaper +
                ", wallpaperRect='" + wallpaperRect + '\'' +
                ", wallpaperFrom='" + wallpaperFrom + '\'' +
                ", startTime=" + startTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isEmptyData=" + isEmptyData +
                '}';
    }
}
