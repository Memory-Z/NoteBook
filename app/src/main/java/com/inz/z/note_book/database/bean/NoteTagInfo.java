package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 笔记 标签 信息
 * ====================================================
 * Create by 11654 in 2021/4/11 20:29
 */
@Entity(nameInDb = "note_tag_info")
public class NoteTagInfo {

    @Index
    @Id(autoincrement = true)
    private Long noteTagId = 0L;

    /**
     * 标切内容
     */
    private String tagContent = "";

    private Date createDate = null;

    @Generated(hash = 635434356)
    public NoteTagInfo(Long noteTagId, String tagContent, Date createDate) {
        this.noteTagId = noteTagId;
        this.tagContent = tagContent;
        this.createDate = createDate;
    }

    @Generated(hash = 147985785)
    public NoteTagInfo() {
    }

    public Long getNoteTagId() {
        return this.noteTagId;
    }

    public void setNoteTagId(Long noteTagId) {
        this.noteTagId = noteTagId;
    }

    public String getTagContent() {
        return this.tagContent;
    }

    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
