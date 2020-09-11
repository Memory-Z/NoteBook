package com.inz.z.note_book.database.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import com.inz.z.note_book.database.*
import org.greenrobot.greendao.database.Database

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/03/23 13:59.
 */
class GreenDaoOpenHelper : DaoMaster.OpenHelper {
    constructor(context: Context?, name: String?) : super(context, name) {}
    constructor(
        context: Context?,
        name: String?,
        factory: CursorFactory?
    ) : super(context, name, factory) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onUpgrade(db, oldVersion, newVersion)
    }

    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
//        super.onUpgrade(db, oldVersion, newVersion);
        if (newVersion > oldVersion) {
            MigrationHelper
                .instance
                ?.migrate(
                    db,
                    NoteGroupDao::class.java,
                    NoteGroupWithInfoDao::class.java,
                    NoteInfoDao::class.java,
                    OperationLogInfoDao::class.java,
                    TaskInfoDao::class.java,
                    TaskScheduleDao::class.java,
                    RecordInfoDao::class.java
                )
        }
    }
}