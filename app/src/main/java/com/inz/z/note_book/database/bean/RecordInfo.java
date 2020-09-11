package com.inz.z.note_book.database.bean;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 记录信息
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 11:49.
 */
@Entity(nameInDb = "record_info")
public class RecordInfo {

    /**
     * 不可用
     */
    public static final int ENABLE_STATE_BAN = 0;
    /**
     * 可用
     */
    public static final int ENABLE_STATE_USE = 1;

    @IntDef({ENABLE_STATE_BAN, ENABLE_STATE_USE})
    @Retention(RetentionPolicy.SOURCE)
    @interface RecordEnableState{

    }

    @Index
    @Id(autoincrement = false)
    private String id = "";
    /**
     * 记录标题
     */
    private String recordTitle = "";
    /**
     * 记录内容
     */
    private String recordContent = "";

    /**
     * 记录日期
     */
    private Date recordDate;

    /**
     * 是否可用： 1： 可用；0：不可用；其他，待定
     */
    @RecordEnableState
    private int enable = ENABLE_STATE_USE;

    private Date createDate;
    private Date updateDate;

    @NonNull
    @Override
    public String toString() {
        return "RecordInfo{" +
                "id='" + id + '\'' +
                ", recordTitle='" + recordTitle + '\'' +
                ", recordContent='" + recordContent + '\'' +
                ", recordDate=" + recordDate +
                ", enable=" + enable +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }

    @Generated(hash = 1517663159)
    public RecordInfo(String id, String recordTitle, String recordContent,
                      Date recordDate, int enable, Date createDate, Date updateDate) {
        this.id = id;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.recordDate = recordDate;
        this.enable = enable;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1863816245)
    public RecordInfo() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordTitle() {
        return this.recordTitle;
    }

    public void setRecordTitle(String recordTitle) {
        this.recordTitle = recordTitle;
    }

    public String getRecordContent() {
        return this.recordContent;
    }

    public void setRecordContent(String recordContent) {
        this.recordContent = recordContent;
    }

    public Date getRecordDate() {
        return this.recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
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


    public int getEnable() {
        return this.enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
