package com.inz.z.note_book.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.inz.z.note_book.database.bean.TaskSchedule;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "task_schedule".
*/
public class TaskScheduleDao extends AbstractDao<TaskSchedule, String> {

    public static final String TABLENAME = "task_schedule";

    /**
     * Properties of entity TaskSchedule.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property TaskScheduleId = new Property(0, String.class, "taskScheduleId", true, "TASK_SCHEDULE_ID");
        public final static Property TaskId = new Property(1, String.class, "taskId", false, "TASK_ID");
        public final static Property ScheduleStartTime = new Property(2, java.util.Date.class, "scheduleStartTime", false, "SCHEDULE_START_TIME");
        public final static Property ScheduleFinishTime = new Property(3, java.util.Date.class, "scheduleFinishTime", false, "SCHEDULE_FINISH_TIME");
        public final static Property ScheduleRepeat = new Property(4, boolean.class, "scheduleRepeat", false, "SCHEDULE_REPEAT");
        public final static Property Status = new Property(5, int.class, "status", false, "STATUS");
        public final static Property CreateTime = new Property(6, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property UpdateTime = new Property(7, java.util.Date.class, "updateTime", false, "UPDATE_TIME");
    }


    public TaskScheduleDao(DaoConfig config) {
        super(config);
    }
    
    public TaskScheduleDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"task_schedule\" (" + //
                "\"TASK_SCHEDULE_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: taskScheduleId
                "\"TASK_ID\" TEXT NOT NULL ," + // 1: taskId
                "\"SCHEDULE_START_TIME\" INTEGER," + // 2: scheduleStartTime
                "\"SCHEDULE_FINISH_TIME\" INTEGER," + // 3: scheduleFinishTime
                "\"SCHEDULE_REPEAT\" INTEGER NOT NULL ," + // 4: scheduleRepeat
                "\"STATUS\" INTEGER NOT NULL ," + // 5: status
                "\"CREATE_TIME\" INTEGER," + // 6: createTime
                "\"UPDATE_TIME\" INTEGER);"); // 7: updateTime
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_task_schedule_TASK_SCHEDULE_ID ON \"task_schedule\"" +
                " (\"TASK_SCHEDULE_ID\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"task_schedule\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TaskSchedule entity) {
        stmt.clearBindings();
 
        String taskScheduleId = entity.getTaskScheduleId();
        if (taskScheduleId != null) {
            stmt.bindString(1, taskScheduleId);
        }
        stmt.bindString(2, entity.getTaskId());
 
        java.util.Date scheduleStartTime = entity.getScheduleStartTime();
        if (scheduleStartTime != null) {
            stmt.bindLong(3, scheduleStartTime.getTime());
        }
 
        java.util.Date scheduleFinishTime = entity.getScheduleFinishTime();
        if (scheduleFinishTime != null) {
            stmt.bindLong(4, scheduleFinishTime.getTime());
        }
        stmt.bindLong(5, entity.getScheduleRepeat() ? 1L: 0L);
        stmt.bindLong(6, entity.getStatus());
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(7, createTime.getTime());
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(8, updateTime.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TaskSchedule entity) {
        stmt.clearBindings();
 
        String taskScheduleId = entity.getTaskScheduleId();
        if (taskScheduleId != null) {
            stmt.bindString(1, taskScheduleId);
        }
        stmt.bindString(2, entity.getTaskId());
 
        java.util.Date scheduleStartTime = entity.getScheduleStartTime();
        if (scheduleStartTime != null) {
            stmt.bindLong(3, scheduleStartTime.getTime());
        }
 
        java.util.Date scheduleFinishTime = entity.getScheduleFinishTime();
        if (scheduleFinishTime != null) {
            stmt.bindLong(4, scheduleFinishTime.getTime());
        }
        stmt.bindLong(5, entity.getScheduleRepeat() ? 1L: 0L);
        stmt.bindLong(6, entity.getStatus());
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(7, createTime.getTime());
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(8, updateTime.getTime());
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public TaskSchedule readEntity(Cursor cursor, int offset) {
        TaskSchedule entity = new TaskSchedule( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // taskScheduleId
            cursor.getString(offset + 1), // taskId
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // scheduleStartTime
            cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)), // scheduleFinishTime
            cursor.getShort(offset + 4) != 0, // scheduleRepeat
            cursor.getInt(offset + 5), // status
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // createTime
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)) // updateTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TaskSchedule entity, int offset) {
        entity.setTaskScheduleId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTaskId(cursor.getString(offset + 1));
        entity.setScheduleStartTime(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setScheduleFinishTime(cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)));
        entity.setScheduleRepeat(cursor.getShort(offset + 4) != 0);
        entity.setStatus(cursor.getInt(offset + 5));
        entity.setCreateTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setUpdateTime(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
     }
    
    @Override
    protected final String updateKeyAfterInsert(TaskSchedule entity, long rowId) {
        return entity.getTaskScheduleId();
    }
    
    @Override
    public String getKey(TaskSchedule entity) {
        if(entity != null) {
            return entity.getTaskScheduleId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TaskSchedule entity) {
        return entity.getTaskScheduleId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
