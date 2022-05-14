package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import com.inz.z.note_book.base.NoteInfoStatus;
import com.inz.z.note_book.base.NoteStatus;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * 笔记信息
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/28 14:28.
 */
@Entity(nameInDb = "note_info")
public class NoteInfo {

    /**
     * id
     */
    @Id(autoincrement = false)
    private String noteInfoId = "";
    /**
     * 标题
     */
    private String noteTitle = "";
    /**
     * 内容
     */
    private String noteContent = "";
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;
    /**
     * 状态
     *
     * @see NoteStatus 日记状态
     */
    @NoteInfoStatus
    private int status = 0;

    @Generated(hash = 675196626)
    public NoteInfo(String noteInfoId, String noteTitle, String noteContent,
                    Date createDate, Date updateDate, int status) {
        this.noteInfoId = noteInfoId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.status = status;
    }

    @Generated(hash = 1097220926)
    public NoteInfo() {
    }

    public String getNoteInfoId() {
        return this.noteInfoId;
    }

    public void setNoteInfoId(String noteInfoId) {
        this.noteInfoId = noteInfoId;
    }

    public String getNoteTitle() {
        return this.noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return this.noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
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

    public int getStatus() {
        return this.status;
    }

    public String getStatusStr() {
        return String.valueOf(this.status);
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @NonNull
    @Override
    public String toString() {
        return "NoteInfo{" +
                "noteInfoId='" + noteInfoId + '\'' +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", status=" + status +
                '}';
    }
}
