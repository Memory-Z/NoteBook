package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 笔记 标签 关联
 * ====================================================
 * Create by 11654 in 2021/4/11 20:33
 */
@Entity(nameInDb = "note_tag_link_info")
public class NoteTagLinkInfo {

    @Index
    @Id(autoincrement = true)
    private Long noteTagLinkId = 0L;
    /**
     * 笔记 ID
     */
    private String noteId = "";
    /**
     * 标签 ID
     */
    private Long noteTagId = 0L;

    /**
     * 排序
     */
    private Long index = 0L;
    /**
     * 是否可用
     */
    private boolean enable = true;
    private Date createDate;
    private Date updateDate;
    @Generated(hash = 263316648)
    public NoteTagLinkInfo(Long noteTagLinkId, String noteId, Long noteTagId,
            Long index, boolean enable, Date createDate, Date updateDate) {
        this.noteTagLinkId = noteTagLinkId;
        this.noteId = noteId;
        this.noteTagId = noteTagId;
        this.index = index;
        this.enable = enable;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
    @Generated(hash = 1404499566)
    public NoteTagLinkInfo() {
    }
    public Long getNoteTagLinkId() {
        return this.noteTagLinkId;
    }
    public void setNoteTagLinkId(Long noteTagLinkId) {
        this.noteTagLinkId = noteTagLinkId;
    }
    public String getNoteId() {
        return this.noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public Long getNoteTagId() {
        return this.noteTagId;
    }
    public void setNoteTagId(Long noteTagId) {
        this.noteTagId = noteTagId;
    }
    public Long getIndex() {
        return this.index;
    }
    public void setIndex(Long index) {
        this.index = index;
    }
    public boolean getEnable() {
        return this.enable;
    }
    public void setEnable(boolean enable) {
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
        return super.toString();
    }
}
