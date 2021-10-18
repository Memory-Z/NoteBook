package com.inz.z.note_book.database.bean;

import android.content.Context;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inz.z.note_book.R;
import com.inz.z.note_book.bean.inside.ScheduleStatus;
import com.inz.z.note_book.bean.inside.ScheduleWeekDate;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 任务计划  ---- 依附 于任务信息
 * <h1>任务计划</h1>
 * <br/>
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/14 10:31.
 * @see TaskInfo
 */
@Entity(nameInDb = "task_schedule")
public class TaskSchedule {
    /**
     * Id
     */
    @Id
    @Index(unique = true)
    private String taskScheduleId = "";

    /**
     * 任务ID
     */
    @NotNull
    private String taskId = "";

    /**
     * 计划开始时间
     */
    @Index
    private Date scheduleTime;
    /**
     * 计划重复时间
     */
    private String scheduleRepeatTimeStr = "";

    /**
     * 计划是否循环
     */
    private boolean scheduleRepeat = false;

    /**
     * 计划状态，0：未开始；1：进行中；2：已完成；3：已超时；4：已删除
     */
    private int status = 0;

    /**
     * 计划标签 -
     */
    @Index
    private String scheduleTag = "";
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    @Generated(hash = 1836393225)
    public TaskSchedule(String taskScheduleId, @NotNull String taskId, Date scheduleTime,
                        String scheduleRepeatTimeStr, boolean scheduleRepeat, int status, String scheduleTag, Date createTime,
                        Date updateTime) {
        this.taskScheduleId = taskScheduleId;
        this.taskId = taskId;
        this.scheduleTime = scheduleTime;
        this.scheduleRepeatTimeStr = scheduleRepeatTimeStr;
        this.scheduleRepeat = scheduleRepeat;
        this.status = status;
        this.scheduleTag = scheduleTag;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 630349541)
    public TaskSchedule() {
    }

    public String getTaskScheduleId() {
        return this.taskScheduleId;
    }

    public void setTaskScheduleId(String taskScheduleId) {
        this.taskScheduleId = taskScheduleId;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getScheduleRepeat() {
        return this.scheduleRepeat;
    }

    public void setScheduleRepeat(boolean scheduleRepeat) {
        this.scheduleRepeat = scheduleRepeat;
    }

    public Date getScheduleTime() {
        if (this.scheduleTime == null) {
            this.scheduleTime = Calendar.getInstance(Locale.getDefault()).getTime();
        }
        return this.scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

//    public String getScheduleRepeatTimeStr() {
//        return this.scheduleRepeatTimeStr;
//    }
//
//    public void setScheduleRepeatTimeStr(String scheduleRepeatTimeStr) {
//        this.scheduleRepeatTimeStr = scheduleRepeatTimeStr;
//    }

    public void setScheduleRepeatTimeJson(@Nullable List<ScheduleWeekDate> weekDateList) {
        this.scheduleRepeatTimeStr = JSONObject.toJSONString(weekDateList);
    }

    public String getScheduleRepeatTimeJsonStr(@androidx.annotation.Nullable Context context) {
        List<ScheduleWeekDate> timeStrList = getScheduleRepeatDateList();
        String timeStr = timeStrList.toString();
        if (context != null) {
            StringBuilder tStr = new StringBuilder();
            for (ScheduleWeekDate date : timeStrList) {
                tStr.append(date.getString(context)).append(" ");
            }
//            timeStr = tStr.toString();
            if (timeStrList.size() > 0) {
                timeStr = tStr.substring(0, tStr.length() - 1);
//                timeStr = "{" + timeStr + "}";
            } else {
                timeStr = context.getString(R.string._never);
            }

        }
        return timeStr;
    }

    public List<ScheduleWeekDate> getScheduleRepeatDateList() {
        List<ScheduleWeekDate> timeStrList = JSON.parseArray(this.scheduleRepeatTimeStr, ScheduleWeekDate.class);
        if (timeStrList == null) {
            timeStrList = new ArrayList<>();
        }
        return timeStrList;
    }

    public String getScheduleRepeatTimeStr() {
        return this.scheduleRepeatTimeStr;
    }

    public void setScheduleRepeatTimeStr(String scheduleRepeatTimeStr) {
        this.scheduleRepeatTimeStr = scheduleRepeatTimeStr;
    }

    public void setScheduleStatus(ScheduleStatus status) {
        this.status = status.getValue();
    }

    public ScheduleStatus getScheduleStatus() {
        ScheduleStatus status = ScheduleStatus.UNABLE;
        return status.getScheduleStatue(this.status);
    }

    public String getScheduleTag() {
        return this.scheduleTag;
    }

    public void setScheduleTag(String scheduleTag) {
        this.scheduleTag = scheduleTag;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskSchedule{" +
                "taskScheduleId='" + taskScheduleId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", scheduleTime=" + scheduleTime +
                ", scheduleRepeatTimeStr='" + scheduleRepeatTimeStr + '\'' +
                ", scheduleRepeat=" + scheduleRepeat +
                ", status=" + status +
                ", scheduleTag='" + scheduleTag + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
