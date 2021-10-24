package com.inz.z.note_book.database.util

import android.text.TextUtils
import android.util.Log
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.database.Database
import org.greenrobot.greendao.internal.DaoConfig
import java.lang.StringBuilder
import kotlin.collections.ArrayList

/**
 *
 * 数据库复制
 * ====================================================
 * Create by 11654 in 2021/4/5 16:58
 */
class CopyHelper {

    companion object {
        private const val TAG = "CopyHelper"
        private const val CONVERSION_CLASS_NOT_FOUND_EXCEPTION =
            "COPY HELPER - CLASS DOESN'T MATCH WATCH WITH THE CURRENT PARAMETERS"
        var instance: CopyHelper? = null
            get() {
                if (field == null) {
                    field = CopyHelper()
                }
                return field
            }

        private fun getColumns(db: Database, tableName: String): List<String> {
            var columns: List<String> = ArrayList()
            try {
                db.rawQuery("SELECT * FROM $tableName LIMIT 1", null)
                    .use { cursor ->
                        if (cursor != null) {
                            columns = ArrayList(listOf(*cursor.columnNames))
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "getColumns: ", e)
                e.printStackTrace()
            }
            return columns
        }

        private fun getTableIsUsable(db: Database, tableName: String): Boolean {
            try {
                db.rawQuery("SELECT * FROM $tableName LIMIT 1 ", null)
                    .use { return true }

            } catch (e: Exception) {
                Log.e(TAG, "getTableIsUsable: ", e)
                e.printStackTrace()
            }
            return false
        }
    }

    @SafeVarargs
    fun copy(db: Database?, vararg daoClasses: Class<out AbstractDao<*, *>?>) {
        Log.i(TAG, "copy: ---> $db ")
        if (db != null) {
            generateTempTables(db, *daoClasses)
        }
    }

    /**
     * 创建临时表格
     */
    @SafeVarargs
    private fun generateTempTables(db: Database, vararg daoClasses: Class<out AbstractDao<*, *>?>) {
        for (daoClass in daoClasses) {
            val daoConfig = DaoConfig(db, daoClass)
            var divider = ""
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_COPY"
            val properties = ArrayList<String?>()
            val createTableStringBuilder = StringBuilder()
            val haveOldTable = getTableIsUsable(db, tableName)
            if (!haveOldTable) {
                return
            }
            createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (")
            for (j in daoConfig.properties.indices) {
                val p = daoConfig.properties[j]
                val columnName = p.columnName
                if (getColumns(db, tableName).contains(columnName)) {
                    properties.add(columnName)
                    var type: String? = null
                    try {
                        type = getTypeByClass(p.type)
                    } catch (exception: Exception) {
                        Log.e(TAG, "generateTempTables: ", exception)
                    }
                    createTableStringBuilder.append(divider).append(columnName).append(" ")
                        .append(type)
                    if ("INTEGER" == type) {
                        createTableStringBuilder.append(" ").append(" NOT NULL DEFAULT 0 ")
                    }
                    if (p.primaryKey) {
                        createTableStringBuilder.append(" PRIMARY KEY")
                    }
                    divider = ","
                }
            }
            // 创建表
            createTableStringBuilder.append(" );")
            db.execSQL(createTableStringBuilder.toString())
            // 数据复制
            val insertTableStringBuilder =
                "INSERT INTO $tableName ( ${TextUtils.join(",", properties)} ) " +
                        "SELECT ${TextUtils.join(",", properties)} FROM $tableName ; "
            db.execSQL(insertTableStringBuilder)
        }
    }

    @Throws(Exception::class)
    private fun getTypeByClass(type: Class<*>): String {
        if (type == String::class.java) {
            return "TEXT"
        }
        if (type == Long::class.java || type == Int::class.java || type == Long::class.javaPrimitiveType) {
            return "INTEGER"
        }
        if (type == Boolean::class.java) {
            return "BOOLEAN"
        }
        throw Exception("$CONVERSION_CLASS_NOT_FOUND_EXCEPTION - class: $type")
    }

}