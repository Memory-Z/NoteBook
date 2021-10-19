package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import com.inz.z.note_book.base.TaskAction;
import com.inz.z.note_book.base.TaskValue;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

/**
 * 任务信息 -- 作用？
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/14 10:32.
 * 作用: 作为 计划 附件
 * Update by lop 3 in 2021/10/18 21:43
 */
@Entity(nameInDb = "task_info")
public class TaskInfo {

    /**
     * ID
     */
    @Id
    @Index(unique = true)
    private String taskId = "";
    /**
     * 任务类型: 0: 普通文本任务；1：普通文件任务
     */
    private int type = 0;
    /**
     * 任务描述
     */
    private String taskDescribe = "";
    /**
     * 是否存在文件 0: 不存在; 1: 存在
     */
    private int haveFile = 0;

    /**
     * 备注
     */
    private String remake = "";
    /**
     * 任务动作: 描述任务需要执行的类型。如: 计划 - {@link TaskSchedule}
     */
    @TaskAction
    private String taskAction = TaskValue.TASK_ACTION_NONE;
    /**
     * 任务包名
     */
    private String taskPackageName = "";

    /**
     * 任务请求码
     */
    private Long taskRequestCode = -1L;

    private Date createTime;
    private Date updateTime;


    @Generated(hash = 2022720704)
    public TaskInfo() {
    }

    @Generated(hash = 638954131)
    public TaskInfo(String taskId, int type, String taskDescribe, int haveFile,
                    String remake, String taskAction, String taskPackageName,
                    Long taskRequestCode, Date createTime, Date updateTime) {
        this.taskId = taskId;
        this.type = type;
        this.taskDescribe = taskDescribe;
        this.haveFile = haveFile;
        this.remake = remake;
        this.taskAction = taskAction;
        this.taskPackageName = taskPackageName;
        this.taskRequestCode = taskRequestCode;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTaskDescribe() {
        return this.taskDescribe;
    }

    public void setTaskDescribe(String taskDescribe) {
        this.taskDescribe = taskDescribe;
    }

    public int getHaveFile() {
        return this.haveFile;
    }

    public void setHaveFile(int haveFile) {
        this.haveFile = haveFile;
    }

    public String getRemake() {
        return this.remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
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

    @TaskAction
    public String getTaskAction() {
        return this.taskAction;
    }

    public void setTaskAction(@TaskAction String taskAction) {
        this.taskAction = taskAction;
    }

    public String getTaskPackageName() {
        return this.taskPackageName;
    }

    public void setTaskPackageName(String taskPackageName) {
        this.taskPackageName = taskPackageName;
    }

    public Long getTaskRequestCode() {
        return this.taskRequestCode;
    }

    public void setTaskRequestCode(Long taskRequestCode) {
        this.taskRequestCode = taskRequestCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskInfo{" +
                "taskId='" + taskId + '\'' +
                ", type=" + type +
                ", taskDescribe='" + taskDescribe + '\'' +
                ", haveFile=" + haveFile +
                ", remake='" + remake + '\'' +
                ", taskAction='" + taskAction + '\'' +
                ", taskPackageName='" + taskPackageName + '\'' +
                ", taskRequestCode=" + taskRequestCode +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
