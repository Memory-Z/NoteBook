package com.inz.z.note_book.database.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.inz.z.base.util.FileUtils
import com.inz.z.note_book.database.DaoMaster
import com.inz.z.note_book.database.DaoSession
import com.inz.z.note_book.util.Constants
import java.io.File

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/01 14:32.
 */
class GreenDaoHelper private constructor() {

    companion object {

        private const val TAG = "GreenDaoHelper"
        private var mDaoSession: DaoSession? = null
        private var mDaoMaster: DaoMaster? = null
        private var database: SQLiteDatabase? = null

        private var helper: GreenDaoHelper? = null
            get() {
                if (field == null) {
                    field = GreenDaoHelper()
                }
                return field
            }

        private const val DATABASE_NAME = "note_book"

        @Synchronized
        fun getInstance(): GreenDaoHelper {
            return helper!!
        }
    }

    private lateinit var baseBackupPath: String

    /**
     * 初始化 GreenDao 工具类
     */
    fun initGreenDaoHelper(context: Context) {
        synchronized(GreenDaoHelper) {
            // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
            // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
            // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
            // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
            // 此处 note_book 表示数据库名称 可以任意填写
            val mDaoHelper = GreenDaoOpenHelper(context, DATABASE_NAME, null)
            database = mDaoHelper.writableDatabase
            // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
            mDaoMaster = DaoMaster(database)
            mDaoSession = mDaoMaster?.newSession()
            // 获取默认备份地址
            baseBackupPath = Constants.Base.getBaseBackupPath(context)
        }
    }

    fun getDaoSession(): DaoSession? {
        return mDaoSession
    }

    fun getDatabase(): SQLiteDatabase? {
        return database
    }

    /**
     * 备份数据库
     */
    fun backupDatabase() {
        synchronized(GreenDaoHelper) {
            val original = database?.path
            val target =
                baseBackupPath + File.separatorChar + DATABASE_NAME + "_${database?.version}.db"
            Log.i(TAG, "copyDatabase: original - $original > target - $target")
            // 数据库备份
            val result = FileUtils.copyFile(original, target, true)
            Log.i(TAG, "backupDatabase: ---Result: $result")
        }
    }
}