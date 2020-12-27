package com.inz.z.note_book.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.inz.z.note_book.database.DaoSession;
import com.inz.z.note_book.database.FileInfoDao;

import org.greenrobot.greendao.annotation.NotNull;

import com.inz.z.note_book.database.NoteFileContentDao;

/**
 * 笔记 - 文件 内容
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/22 14:41.
 */
@Entity(nameInDb = "note_file_content", indexes = {
        @Index(value = " index ASC ")
})
public class NoteFileContent {

    @Index
    @Id(autoincrement = true)
    private Long noteFileId;

    private String noteId = "";

    /**
     * 笔记内容
     */
    private String noteContent = "";

    /**
     * 文件ID
     */
    private Long fileId;

    @ToOne(joinProperty = "fileId")
    private FileInfo fileInfo;
    /**
     * 排序
     */
    @Property(nameInDb = "contentIndex")
    private int index = 0;

    private Date createDate;
    private Date updateDate;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 190130163)
    private transient NoteFileContentDao myDao;

    @Generated(hash = 665517796)
    private transient Long fileInfo__resolvedKey;

    @Generated(hash = 584814784)
    public NoteFileContent(Long noteFileId, String noteId, String noteContent, Long fileId, int index,
                           Date createDate, Date updateDate) {
        this.noteFileId = noteFileId;
        this.noteId = noteId;
        this.noteContent = noteContent;
        this.fileId = fileId;
        this.index = index;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 112518041)
    public NoteFileContent() {
    }

    public Long getNoteFileId() {
        return this.noteFileId;
    }

    public void setNoteFileId(Long noteFileId) {
        this.noteFileId = noteFileId;
    }

    public String getNoteId() {
        return this.noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteContent() {
        return this.noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 784985547)
    public FileInfo getFileInfo() {
        Long __key = this.fileId;
        if (fileInfo__resolvedKey == null || !fileInfo__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FileInfoDao targetDao = daoSession.getFileInfoDao();
            FileInfo fileInfoNew = targetDao.load(__key);
            synchronized (this) {
                fileInfo = fileInfoNew;
                fileInfo__resolvedKey = __key;
            }
        }
        return fileInfo;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 157445859)
    public void setFileInfo(FileInfo fileInfo) {
        synchronized (this) {
            this.fileInfo = fileInfo;
            fileId = fileInfo == null ? null : fileInfo.getFileId();
            fileInfo__resolvedKey = fileId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 687305624)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteFileContentDao() : null;
    }


    @Override
    public String toString() {
        return "NoteFileContent{" +
                "noteFileId=" + noteFileId +
                ", noteId='" + noteId + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", fileId=" + fileId +
                ", fileInfo=" + fileInfo +
                ", index=" + index +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
