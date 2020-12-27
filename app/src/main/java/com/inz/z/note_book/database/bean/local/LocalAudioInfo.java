package com.inz.z.note_book.database.bean.local;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * 本地 音频 信息
 * ===========================================
 *
 * @author Administrator
 * Create by inz. in 2020/12/27 14:01.
 */
@Entity(nameInDb = "local_audio_info")
public class LocalAudioInfo {

    @Index
    @Id(autoincrement = true)
    private Long id = 0L;

    /**
     * 音频ID
     */
    private String localAudioId = "";
    /**
     * 音频 名称
     */
    private String localAudioName = "";
    /**
     * 音频地址
     */
    private String localAudioPath = "";
    /**
     * 音频修改时间
     */
    private Long localAudioModifiedDate = -1L;
    /**
     * 音频大小
     */
    private Long localAudioSize = -1L;
    /**
     * 音频 MimeType
     */
    private String localAudioMimeType = "";

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;

    @Generated(hash = 502985910)
    public LocalAudioInfo(Long id, String localAudioId, String localAudioName,
                          String localAudioPath, Long localAudioModifiedDate, Long localAudioSize,
                          String localAudioMimeType, Date createDate, Date updateDate) {
        this.id = id;
        this.localAudioId = localAudioId;
        this.localAudioName = localAudioName;
        this.localAudioPath = localAudioPath;
        this.localAudioModifiedDate = localAudioModifiedDate;
        this.localAudioSize = localAudioSize;
        this.localAudioMimeType = localAudioMimeType;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1168848039)
    public LocalAudioInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalAudioId() {
        return this.localAudioId;
    }

    public void setLocalAudioId(String localAudioId) {
        this.localAudioId = localAudioId;
    }

    public String getLocalAudioName() {
        return this.localAudioName;
    }

    public void setLocalAudioName(String localAudioName) {
        this.localAudioName = localAudioName;
    }

    public String getLocalAudioPath() {
        return this.localAudioPath;
    }

    public void setLocalAudioPath(String localAudioPath) {
        this.localAudioPath = localAudioPath;
    }

    public Long getLocalAudioModifiedDate() {
        return this.localAudioModifiedDate;
    }

    public void setLocalAudioModifiedDate(Long localAudioModifiedDate) {
        this.localAudioModifiedDate = localAudioModifiedDate;
    }

    public Long getLocalAudioSize() {
        return this.localAudioSize;
    }

    public void setLocalAudioSize(Long localAudioSize) {
        this.localAudioSize = localAudioSize;
    }

    public String getLocalAudioMimeType() {
        return this.localAudioMimeType;
    }

    public void setLocalAudioMimeType(String localAudioMimeType) {
        this.localAudioMimeType = localAudioMimeType;
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

    ///


    @NonNull
    @Override
    public String toString() {
        return "LocalAudioInfo{" +
                "id=" + id +
                ", localAudioId='" + localAudioId + '\'' +
                ", localAudioName='" + localAudioName + '\'' +
                ", localAudioPath='" + localAudioPath + '\'' +
                ", localAudioModifiedDate=" + localAudioModifiedDate +
                ", localAudioSize=" + localAudioSize +
                ", localAudioMimeType='" + localAudioMimeType + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
