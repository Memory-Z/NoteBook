package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 用户日志 信息
 * ====================================================
 * Create by 11654 in 2021/1/1 0:51
 */
@Entity(nameInDb = "user_log_info")
public class UserLogInfo {

    static {
        String LOG_TYPE_UPDATE_NAME = "updateName";
        String LOG_TYPE_UPDATE_PHOTO = "updatePhoto";
        String LOG_TYPE_SWITCH_USER = "switchUser";
        String LOG_TYPE_CREATE_USER = "createUser";
        String LOG_TYPE_UPDATE_STATE = "updateState";
        String LOG_TYPE_OTHER = "other";
    }

    @Id(autoincrement = true)
    @Index
    private Long userLogId = 0L;

    /**
     * 用户ID
     */
    @Index
    private Long userId = 0L;
    /**
     * 日志类型
     */
    private String logType = "";
    /**
     * 日志详情
     */
    private String logDescribe = "";
    /**
     * 旧值
     */
    private String oldValue = "";
    /**
     * 新值
     */
    private String newValue = "";
    /**
     * 创建时间
     */
    private Date createDate = null;

    @Generated(hash = 777643928)
    public UserLogInfo(Long userLogId, Long userId, String logType,
                       String logDescribe, String oldValue, String newValue, Date createDate) {
        this.userLogId = userLogId;
        this.userId = userId;
        this.logType = logType;
        this.logDescribe = logDescribe;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createDate = createDate;
    }

    @Generated(hash = 1645660937)
    public UserLogInfo() {
    }

    public Long getUserLogId() {
        return this.userLogId;
    }

    public void setUserLogId(Long userLogId) {
        this.userLogId = userLogId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogType() {
        return this.logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogDescribe() {
        return this.logDescribe;
    }

    public void setLogDescribe(String logDescribe) {
        this.logDescribe = logDescribe;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserLogInfo{" +
                "userLogId=" + userLogId +
                ", userId=" + userId +
                ", logType='" + logType + '\'' +
                ", logDescribe='" + logDescribe + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
