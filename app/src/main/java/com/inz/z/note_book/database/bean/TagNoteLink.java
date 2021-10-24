package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 标签-笔记 链接
 * ====================================================
 * Create by 11654 in 2021/10/24 14:14
 */
@Entity(nameInDb = "tag_note_link")
public class TagNoteLink {

    /**
     * 链接 ID
     */
    @Id(autoincrement = true)
    private Long linkId = 0L;
    /**
     * 标签ID
     *
     * @see TagInfo
     */
    @Index
    private Long tagId = 0L;
    /**
     * 笔记 ID
     *
     * @see NoteInfo
     */
    @Index
    private String noteId = "";

    private int index = 0;
    private Date createDate;

    @Generated(hash = 603249453)
    public TagNoteLink(Long linkId, Long tagId, String noteId, int index,
            Date createDate) {
        this.linkId = linkId;
        this.tagId = tagId;
        this.noteId = noteId;
        this.index = index;
        this.createDate = createDate;
    }

    @Generated(hash = 2093735606)
    public TagNoteLink() {
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
        return "TagNoteLink{" +
                "linkId=" + linkId +
                ", tagId=" + tagId +
                ", noteId='" + noteId + '\'' +
                ", index=" + index +
                ", createDate=" + createDate +
                '}';
    }
}
