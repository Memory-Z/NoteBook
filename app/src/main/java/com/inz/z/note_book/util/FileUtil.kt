package com.inz.z.note_book.util

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.inz.z.base.util.FileUtils
import com.inz.z.note_book.base.PickImageActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.util.*

/**
 *
 * 文件工具 扩展
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/13 09:25.
 */
object FileUtil : FileUtils() {

    /**
     * 数据类型
     */
    enum class DataType(val fileDirName: String) {
        /**
         * 计划
         */
        SCHEDULE("schedule"),

        /**
         * 用户
         */
        USER("user"),

        /**
         * 设置
         */
        SETTING("setting")


    }

    /**
     * 创建文件数据地址
     */
    fun createDataDirFile(context: Context, dataType: DataType): File {
        val cachePath = getFileDataPath(context)
        val file = File(cachePath + File.separatorChar + dataType.fileDirName)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * 保存数据至文件中
     */
    fun <T> saveData2File(context: Context, dataType: DataType, data: T): String {
        val dir = createDataDirFile(context, dataType)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val name = UUID.randomUUID().toString().toUpperCase(Locale.getDefault())
        val file = File(dir, name)
        if (file.exists()) {
            file.delete()
        }
        var isCreateFile = false
        try {
            isCreateFile = file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (isCreateFile) {
            if (saveObjectToFile(file, data)) {
                return name
            }
        }
        return ""
    }

    /**
     * 保存对象至文件中
     *
     * @param file   文件
     * @param `object` 对象
     * @return 是否保存成功
     */
    private fun saveObjectToFile(file: File, `object`: Any?): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(`object`)
            objectOutputStream.flush()
            objectOutputStream.close()
            outputStream.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * 删除数据文件
     */
    fun deleteDataFile(context: Context, dataType: DataType, fileName: String): Boolean {
        val dir = createDataDirFile(context, dataType)
        val file = File(dir, fileName)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }


    /**
     * 图片选择 （目前只支持单选 ）
     */
    fun getContentWithResult(
        activity: AppCompatActivity,
        callback: ActivityResultCallback<Uri?>
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            PickImageActivityResultContracts(),
            callback
        )
    }

}