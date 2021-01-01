package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 用户信息
 * ====================================================
 * Create by 11654 in 2021/1/1 0:45
 */
@Entity(nameInDb = "user_info")
public class UserInfo {

    @Id(autoincrement = true)
    @Index
    private Long userId = 0L;

    /**
     * 用户名
     */
    @Index
    private String nickName = "";
    /**
     * 用户密码
     */
    private String password = "";
    /**
     * 头像地址
     */
    private String photoPath = "";
    /**
     * 用户信息是否可用： 0：不可用， 1： 可用. 默认；0
     */
    private int enable = 0;
    /**
     * 创建时间
     */
    private Date createDate = null;
    /**
     * 更新时间
     */
    private Date updateDate = null;

    @Generated(hash = 1086173282)
    public UserInfo(Long userId, String nickName, String password, String photoPath,
                    int enable, Date createDate, Date updateDate) {
        this.userId = userId;
        this.nickName = nickName;
        this.password = password;
        this.photoPath = photoPath;
        this.enable = enable;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
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
        return "UserInfo{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", enable=" + enable +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }

///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////


}
