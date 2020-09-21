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
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/16 09:43.
 */
@Entity(nameInDb = "search_content_info")
public class SearchContentInfo {

    public static final int SEARCH_TYPE_OPERATION_LOG = 0x00EA00;
    public static final int SEARCH_TYPE_NOTE = 0x00EA01;
    public static final int SEARCH_TYPE_NOTE_GROUP = 0x00EA02;
    public static final int SEARCH_TYPE_TASK = 0x00EA03;
    public static final int SEARCH_TYPE_SCHEDULE = 0x00EA04;
    public static final int SEARCH_TYPE_RECORD = 0x00EA05;
    public static final int SEARCH_TYPE_BASE = 0x00EFFF;

    @IntDef({SEARCH_TYPE_BASE, SEARCH_TYPE_NOTE, SEARCH_TYPE_NOTE_GROUP, SEARCH_TYPE_OPERATION_LOG,
            SEARCH_TYPE_TASK, SEARCH_TYPE_SCHEDULE, SEARCH_TYPE_RECORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SearchType {
    }

    @Index
    @Id(autoincrement = true)
    private long id;

    /**
     * 搜索内容
     */
    @Index
    private String searchContent = "";

    /**
     * 搜索类型
     */
    @SearchType
    private int searchType = SEARCH_TYPE_BASE;

    /**
     * 重复次数
     */
    public int repeatCount = 0;

    /**
     * 是否可用： 1：可用，0：不可用
     */
    public int enable = 1;

    public Date createDate;
    public Date updateDate;

    @Generated(hash = 1629218002)
    public SearchContentInfo(long id, String searchContent, int searchType, int repeatCount, int enable,
                             Date createDate, Date updateDate) {
        this.id = id;
        this.searchContent = searchContent;
        this.searchType = searchType;
        this.repeatCount = repeatCount;
        this.enable = enable;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1942283701)
    public SearchContentInfo() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSearchContent() {
        return this.searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public int getSearchType() {
        return this.searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
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
        return "SearchContentInfo{" +
                "id=" + id +
                ", searchContent='" + searchContent + '\'' +
                ", searchType=" + searchType +
                ", repeatCount=" + repeatCount +
                ", enable=" + enable +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }


}
