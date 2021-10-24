package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 标签 信息
 * # 与 Task / Note 进行  关联
 *
 * @see TagNoteLink
 * @see TagScheduleLink
 * ====================================================
 * Create by 11654 in 2021/10/23 21:00
 */
@Entity(nameInDb = "tag_info")
public class TagInfo {

    /**
     * 标签 ID
     */
    @Id(autoincrement = true)
    @Index
    private Long tagId = 0L;

    /**
     * 标签内容
     */
    @Index
    private String tagContent = "";

    private Date createDate;

    @Generated(hash = 625773284)
    public TagInfo(Long tagId, String tagContent, Date createDate) {
        this.tagId = tagId;
        this.tagContent = tagContent;
        this.createDate = createDate;
    }

    @Generated(hash = 792670162)
    public TagInfo() {
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagContent() {
        return tagContent;
    }

    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "TagInfo{" +
                "tagId=" + tagId +
                ", tagContent='" + tagContent + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
