package com.inz.z.calendar_view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 日期 view 对象
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/02/01 13:34.
 */
public class DayViewBean implements Serializable {
    /**
     * 日期
     */
    private String dateStr = "1";
    /**
     * 农历
     */
    private String lunarStr = "初一";
    /**
     * 是否为当前日期
     */
    private boolean isCurrentDate = false;
    /**
     * 是否选中
     */
    private boolean isChecked = false;
    /**
     * 是否为第一个选中
     */
    private boolean isFirshChecked = false;
    /**
     * 是否为最后一个选中
     */
    private boolean isLastChecked = false;
    /**
     * 是否存在任务
     */
    private boolean hasSchedule = false;
    /**
     * 是否拥有标签
     */
    private boolean haveTag = true;
    /**
     * 标签名称
     */
    private String tagStr = "";
    /**
     * 是否为其他月份 在月/年视图 下显示
     */
    private boolean isOtherMonth = false;
    /**
     * 当前日期
     */
    private Calendar calendar = null;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getLunarStr() {
        return lunarStr;
    }

    public void setLunarStr(String lunarStr) {
        this.lunarStr = lunarStr;
    }

    public boolean isCurrentDate() {
        return isCurrentDate;
    }

    public void setCurrentDate(boolean currentDate) {
        isCurrentDate = currentDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isFirshChecked() {
        return isFirshChecked;
    }

    public void setFirshChecked(boolean firshChecked) {
        isFirshChecked = firshChecked;
    }

    public boolean isLastChecked() {
        return isLastChecked;
    }

    public void setLastChecked(boolean lastChecked) {
        isLastChecked = lastChecked;
    }

    public boolean isHasSchedule() {
        return hasSchedule;
    }

    public void setHasSchedule(boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    public boolean isHaveTag() {
        return haveTag;
    }

    public void setHaveTag(boolean haveTag) {
        this.haveTag = haveTag;
    }

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
    }


    public boolean isOtherMonth() {
        return isOtherMonth;
    }

    public void setOtherMonth(boolean otherMonth) {
        isOtherMonth = otherMonth;
    }

    @Nullable
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @NonNull
    @Override
    public String toString() {
        return "DayViewBean{" +
                "dateStr='" + dateStr + '\'' +
                ", lunarStr='" + lunarStr + '\'' +
                ", isCurrentDate=" + isCurrentDate +
                ", isChecked=" + isChecked +
                ", isFirshChecked=" + isFirshChecked +
                ", isLastChecked=" + isLastChecked +
                ", hasSchedule=" + hasSchedule +
                ", haveTag=" + haveTag +
                ", tagStr='" + tagStr + '\'' +
                ", isOtherMonth=" + isOtherMonth +
                ", calendar=" + calendar +
                '}';
    }
}
