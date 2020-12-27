package com.inz.z.note_book.database.bean.local;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 本地 图片数据信息
 * ====================================================
 * Create by 11654 in 2020/12/26 23:27
 */
@Entity(nameInDb = "local_image_info")
public class LocalImageInfo {

    /**
     * id
     */
    @Id(autoincrement = true)
    @Index
    private Long id = 0L;

    /**
     * 图片Id
     */
    private String localImageId = "";
    /**
     * 图片名称
     */
    @Index
    private String localImageName = "";
    /**
     * 图片地址
     */
    private String localImagePath = "";
    /**
     * 图片大小
     */
    private Long localImageSize = -1L;
    /**
     * 图片修改时间
     */
    private String localImageDate = "";
    /**
     * 数据创建时间
     */
    private Date createDate = null;
    /**
     * 数据更新时间
     */
    private Date updateDate = null;

    @Generated(hash = 1576803393)
    public LocalImageInfo(Long id, String localImageId, String localImageName,
                          String localImagePath, Long localImageSize, String localImageDate,
                          Date createDate, Date updateDate) {
        this.id = id;
        this.localImageId = localImageId;
        this.localImageName = localImageName;
        this.localImagePath = localImagePath;
        this.localImageSize = localImageSize;
        this.localImageDate = localImageDate;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 2012187249)
    public LocalImageInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalImageId() {
        return this.localImageId;
    }

    public void setLocalImageId(String localImageId) {
        this.localImageId = localImageId;
    }

    public String getLocalImageName() {
        return this.localImageName;
    }

    public void setLocalImageName(String localImageName) {
        this.localImageName = localImageName;
    }

    public String getLocalImagePath() {
        return this.localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }

    public Long getLocalImageSize() {
        return this.localImageSize;
    }

    public void setLocalImageSize(Long localImageSize) {
        this.localImageSize = localImageSize;
    }

    public String getLocalImageDate() {
        return this.localImageDate;
    }

    public void setLocalImageDate(String localImageDate) {
        this.localImageDate = localImageDate;
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
        return "LocalImageInfo{" +
                "id=" + id +
                ", localImageId='" + localImageId + '\'' +
                ", localImageName='" + localImageName + '\'' +
                ", localImagePath='" + localImagePath + '\'' +
                ", localImageSize=" + localImageSize +
                ", localImageDate='" + localImageDate + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
