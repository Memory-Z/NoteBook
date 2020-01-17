package com.inz.z.note_book.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.inz.z.note_book.bean.NoteGroup;
import com.inz.z.note_book.bean.NoteGroupWithInfo;
import com.inz.z.note_book.bean.NoteInfo;
import com.inz.z.note_book.bean.OperationLogInfo;
import com.inz.z.note_book.bean.TaskInfo;
import com.inz.z.note_book.bean.TaskSchedule;

import com.inz.z.note_book.database.NoteGroupDao;
import com.inz.z.note_book.database.NoteGroupWithInfoDao;
import com.inz.z.note_book.database.NoteInfoDao;
import com.inz.z.note_book.database.OperationLogInfoDao;
import com.inz.z.note_book.database.TaskInfoDao;
import com.inz.z.note_book.database.TaskScheduleDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig noteGroupDaoConfig;
    private final DaoConfig noteGroupWithInfoDaoConfig;
    private final DaoConfig noteInfoDaoConfig;
    private final DaoConfig operationLogInfoDaoConfig;
    private final DaoConfig taskInfoDaoConfig;
    private final DaoConfig taskScheduleDaoConfig;

    private final NoteGroupDao noteGroupDao;
    private final NoteGroupWithInfoDao noteGroupWithInfoDao;
    private final NoteInfoDao noteInfoDao;
    private final OperationLogInfoDao operationLogInfoDao;
    private final TaskInfoDao taskInfoDao;
    private final TaskScheduleDao taskScheduleDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        noteGroupDaoConfig = daoConfigMap.get(NoteGroupDao.class).clone();
        noteGroupDaoConfig.initIdentityScope(type);

        noteGroupWithInfoDaoConfig = daoConfigMap.get(NoteGroupWithInfoDao.class).clone();
        noteGroupWithInfoDaoConfig.initIdentityScope(type);

        noteInfoDaoConfig = daoConfigMap.get(NoteInfoDao.class).clone();
        noteInfoDaoConfig.initIdentityScope(type);

        operationLogInfoDaoConfig = daoConfigMap.get(OperationLogInfoDao.class).clone();
        operationLogInfoDaoConfig.initIdentityScope(type);

        taskInfoDaoConfig = daoConfigMap.get(TaskInfoDao.class).clone();
        taskInfoDaoConfig.initIdentityScope(type);

        taskScheduleDaoConfig = daoConfigMap.get(TaskScheduleDao.class).clone();
        taskScheduleDaoConfig.initIdentityScope(type);

        noteGroupDao = new NoteGroupDao(noteGroupDaoConfig, this);
        noteGroupWithInfoDao = new NoteGroupWithInfoDao(noteGroupWithInfoDaoConfig, this);
        noteInfoDao = new NoteInfoDao(noteInfoDaoConfig, this);
        operationLogInfoDao = new OperationLogInfoDao(operationLogInfoDaoConfig, this);
        taskInfoDao = new TaskInfoDao(taskInfoDaoConfig, this);
        taskScheduleDao = new TaskScheduleDao(taskScheduleDaoConfig, this);

        registerDao(NoteGroup.class, noteGroupDao);
        registerDao(NoteGroupWithInfo.class, noteGroupWithInfoDao);
        registerDao(NoteInfo.class, noteInfoDao);
        registerDao(OperationLogInfo.class, operationLogInfoDao);
        registerDao(TaskInfo.class, taskInfoDao);
        registerDao(TaskSchedule.class, taskScheduleDao);
    }
    
    public void clear() {
        noteGroupDaoConfig.clearIdentityScope();
        noteGroupWithInfoDaoConfig.clearIdentityScope();
        noteInfoDaoConfig.clearIdentityScope();
        operationLogInfoDaoConfig.clearIdentityScope();
        taskInfoDaoConfig.clearIdentityScope();
        taskScheduleDaoConfig.clearIdentityScope();
    }

    public NoteGroupDao getNoteGroupDao() {
        return noteGroupDao;
    }

    public NoteGroupWithInfoDao getNoteGroupWithInfoDao() {
        return noteGroupWithInfoDao;
    }

    public NoteInfoDao getNoteInfoDao() {
        return noteInfoDao;
    }

    public OperationLogInfoDao getOperationLogInfoDao() {
        return operationLogInfoDao;
    }

    public TaskInfoDao getTaskInfoDao() {
        return taskInfoDao;
    }

    public TaskScheduleDao getTaskScheduleDao() {
        return taskScheduleDao;
    }

}
