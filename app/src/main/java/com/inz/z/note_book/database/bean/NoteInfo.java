package com.inz.z.note_book.database.bean;

import com.inz.z.note_book.base.NoteStatus;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.DaoException;

import com.inz.z.note_book.database.DaoSession;
import com.inz.z.note_book.database.NoteFileContentDao;
import com.inz.z.note_book.database.NoteInfoDao;

/**
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
    @NoteStatus.NoteInfoStatus
    private int status = 0;

    @ToMany(referencedJoinProperty = "noteId")
    private List<NoteFileContent> noteFileContentList;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 845113378)
    private transient NoteInfoDao myDao;

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

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1421855499)
    public List<NoteFileContent> getNoteFileContentList() {
        if (noteFileContentList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NoteFileContentDao targetDao = daoSession.getNoteFileContentDao();
            List<NoteFileContent> noteFileContentListNew = targetDao
                    ._queryNoteInfo_NoteFileContentList(noteInfoId);
            synchronized (this) {
                if (noteFileContentList == null) {
                    noteFileContentList = noteFileContentListNew;
                }
            }
        }
        return noteFileContentList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 909027651)
    public synchronized void resetNoteFileContentList() {
        noteFileContentList = null;
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
    @Generated(hash = 889194310)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteInfoDao() : null;
    }
}
