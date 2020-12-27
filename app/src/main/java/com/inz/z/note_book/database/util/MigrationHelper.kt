package com.inz.z.note_book.database.util

import android.text.TextUtils
import android.util.Log
import com.inz.z.note_book.database.DaoMaster
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.database.Database
import org.greenrobot.greendao.internal.DaoConfig
import java.util.*

/**
 * 数据库-更新 合并
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/03/23 13:53.
 */
class MigrationHelper {

    companion object {
        private const val CONVERSION_CLASS_NOT_FOUND_EXCEPTION =
            "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS"
        var instance: MigrationHelper? = null
            get() {
                if (field == null) {
                    field = MigrationHelper()
                }
                return field
            }
            private set

        private fun getColumns(
            db: Database,
            tableName: String
        ): List<String> {
            var columns: List<String> =
                ArrayList()
            try {
                db.rawQuery("SELECT * FROM $tableName limit 1", null).use { cursor ->
                    if (cursor != null) {
                        columns = ArrayList(
                            Arrays.asList(*cursor.columnNames)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.v(tableName, e.message, e)
                e.printStackTrace()
            }
            return columns
        }

        private fun getTableIsUsable(
            db: Database,
            tableName: String
        ): Boolean {
            try {
                db.rawQuery("SELECT * FROM $tableName limit  1", null).use {
                    return true
                }
            } catch (e: Exception) {
                return false
            }
        }
    }

    @SafeVarargs
    fun migrate(
        db: Database?,
        vararg daoClasses: Class<out AbstractDao<*, *>?>
    ) {
        if (db != null) {
            generateTempTables(db, *daoClasses)
            DaoMaster.dropAllTables(db, true)
            DaoMaster.createAllTables(db, false)
            restoreData(db, *daoClasses)
        }
    }

    @SafeVarargs
    private fun generateTempTables(
        db: Database,
        vararg daoClasses: Class<out AbstractDao<*, *>?>
    ) {
        for (daoClass in daoClasses) {
            val daoConfig = DaoConfig(db, daoClass)
            var divider = ""
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_TEMP"
            val properties =
                ArrayList<String?>()
            val createTableStringBuilder = StringBuilder()
            val haveOldTable = getTableIsUsable(db, tableName)
            if (!haveOldTable) {
                return
            }
            createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (")
            for (j in daoConfig.properties.indices) {
                val columnName = daoConfig.properties[j].columnName
                if (getColumns(db, tableName).contains(columnName)) {
                    properties.add(columnName)
                    var type: String? = null
                    try {
                        type = getTypeByClass(daoConfig.properties[j].type)
                    } catch (exception: Exception) {
//                        Crashlytics.logException(exception);
                    }
                    createTableStringBuilder.append(divider).append(columnName).append(" ")
                        .append(type)
                    if ("INTEGER" == type) {
                        createTableStringBuilder.append(" ").append(" NOT NULL DEFAULT 0 ")
                    }
                    if (daoConfig.properties[j].primaryKey) {
                        createTableStringBuilder.append(" PRIMARY KEY")
                    }
                    divider = ","
                }
            }
            createTableStringBuilder.append(");")
            db.execSQL(createTableStringBuilder.toString())
            val insertTableStringBuilder = "INSERT INTO " + tempTableName + " (" +
                    TextUtils.join(",", properties) +
                    ") SELECT " +
                    TextUtils.join(",", properties) +
                    " FROM " + tableName + ";"
            db.execSQL(insertTableStringBuilder)
        }
    }

    @SafeVarargs
    private fun restoreData(
        db: Database,
        vararg daoClasses: Class<out AbstractDao<*, *>?>
    ) {
        for (daoClass in daoClasses) {
            val daoConfig = DaoConfig(db, daoClass)
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_TEMP"
            val properties: MutableList<String?> =
                ArrayList()
            val propertiesQuery: MutableList<String?> =
                ArrayList()
            val haveOldTable = getTableIsUsable(db, tempTableName)
            if (!haveOldTable) {
                return
            }
            for (j in daoConfig.properties.indices) {
                val columnName = daoConfig.properties[j].columnName
                if (getColumns(db, tempTableName).contains(columnName)) {
                    properties.add(columnName)
                    propertiesQuery.add(columnName)
                } else {
                    try {
                        if ("INTEGER" == getTypeByClass(daoConfig.properties[j].type)) {
                            propertiesQuery.add(" 0 as $columnName")
                            properties.add(columnName)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val insertTableStringBuilder = "INSERT INTO " + tableName + " (" +
                    TextUtils.join(",", properties) +
                    ") SELECT " +
                    TextUtils.join(",", propertiesQuery) +
                    " FROM " + tempTableName + ";"
            db.execSQL(insertTableStringBuilder)
            db.execSQL("DROP TABLE $tempTableName")
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
        throw Exception("$CONVERSION_CLASS_NOT_FOUND_EXCEPTION - Class: $type")
    }


}