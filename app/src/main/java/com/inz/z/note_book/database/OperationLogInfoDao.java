package com.inz.z.note_book.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.inz.z.note_book.bean.OperationLogInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "operation_log_info".
*/
public class OperationLogInfoDao extends AbstractDao<OperationLogInfo, String> {

    public static final String TABLENAME = "operation_log_info";

    /**
     * Properties of entity OperationLogInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property OperationLogId = new Property(0, String.class, "operationLogId", true, "OPERATION_LOG_ID");
        public final static Property TableName = new Property(1, String.class, "tableName", false, "TABLE_NAME");
        public final static Property OperationType = new Property(2, String.class, "operationType", false, "OPERATION_TYPE");
        public final static Property OperationDescribe = new Property(3, String.class, "operationDescribe", false, "OPERATION_DESCRIBE");
        public final static Property OperationData = new Property(4, String.class, "operationData", false, "OPERATION_DATA");
        public final static Property CreateTime = new Property(5, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property UpdateTime = new Property(6, java.util.Date.class, "updateTime", false, "UPDATE_TIME");
    }


    public OperationLogInfoDao(DaoConfig config) {
        super(config);
    }
    
    public OperationLogInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"operation_log_info\" (" + //
                "\"OPERATION_LOG_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: operationLogId
                "\"TABLE_NAME\" TEXT," + // 1: tableName
                "\"OPERATION_TYPE\" TEXT," + // 2: operationType
                "\"OPERATION_DESCRIBE\" TEXT," + // 3: operationDescribe
                "\"OPERATION_DATA\" TEXT," + // 4: operationData
                "\"CREATE_TIME\" INTEGER," + // 5: createTime
                "\"UPDATE_TIME\" INTEGER);"); // 6: updateTime
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_operation_log_info_OPERATION_LOG_ID ON \"operation_log_info\"" +
                " (\"OPERATION_LOG_ID\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"operation_log_info\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, OperationLogInfo entity) {
        stmt.clearBindings();
 
        String operationLogId = entity.getOperationLogId();
        if (operationLogId != null) {
            stmt.bindString(1, operationLogId);
        }
 
        String tableName = entity.getTableName();
        if (tableName != null) {
            stmt.bindString(2, tableName);
        }
 
        String operationType = entity.getOperationType();
        if (operationType != null) {
            stmt.bindString(3, operationType);
        }
 
        String operationDescribe = entity.getOperationDescribe();
        if (operationDescribe != null) {
            stmt.bindString(4, operationDescribe);
        }
 
        String operationData = entity.getOperationData();
        if (operationData != null) {
            stmt.bindString(5, operationData);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(6, createTime.getTime());
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(7, updateTime.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, OperationLogInfo entity) {
        stmt.clearBindings();
 
        String operationLogId = entity.getOperationLogId();
        if (operationLogId != null) {
            stmt.bindString(1, operationLogId);
        }
 
        String tableName = entity.getTableName();
        if (tableName != null) {
            stmt.bindString(2, tableName);
        }
 
        String operationType = entity.getOperationType();
        if (operationType != null) {
            stmt.bindString(3, operationType);
        }
 
        String operationDescribe = entity.getOperationDescribe();
        if (operationDescribe != null) {
            stmt.bindString(4, operationDescribe);
        }
 
        String operationData = entity.getOperationData();
        if (operationData != null) {
            stmt.bindString(5, operationData);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(6, createTime.getTime());
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(7, updateTime.getTime());
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public OperationLogInfo readEntity(Cursor cursor, int offset) {
        OperationLogInfo entity = new OperationLogInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // operationLogId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // tableName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // operationType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // operationDescribe
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // operationData
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // createTime
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)) // updateTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, OperationLogInfo entity, int offset) {
        entity.setOperationLogId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTableName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOperationType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setOperationDescribe(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setOperationData(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreateTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUpdateTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    @Override
    protected final String updateKeyAfterInsert(OperationLogInfo entity, long rowId) {
        return entity.getOperationLogId();
    }
    
    @Override
    public String getKey(OperationLogInfo entity) {
        if(entity != null) {
            return entity.getOperationLogId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(OperationLogInfo entity) {
        return entity.getOperationLogId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}