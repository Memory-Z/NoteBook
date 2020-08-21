package com.inz.z.base.util

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresPermission
import com.inz.z.base.entity.BaseChooseFileBean
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 14:33.
 */
object ProviderUtil {
    var simpleDateFormat: SimpleDateFormat? = null

    /**
     * 通过文件目录查询文件列表
     * @param filePath 文件目录地址
     */
    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun queryFileListByDir(filePath: String?): MutableList<BaseChooseFileBean> {
        var path = filePath
        if (TextUtils.isEmpty(filePath)) {
            path = FileUtils.getExternalStorageDirectory()
        }
        val fileList = mutableListOf<BaseChooseFileBean>()
        path?.apply {
            val file = File(this)
            if (!file.exists()) {
                return fileList
            }
            if (file.isDirectory) {
                file.listFiles()?.forEach { childFile ->
                    val bean = BaseChooseFileBean()
                    bean.fileName = childFile.name
                    bean.filePath = childFile.path
                    bean.fileLength = childFile.length()
                    bean.fileIsDirectory = childFile.isDirectory
                    bean.fileChangeDate = formatDatetime(childFile.lastModified())
                    bean.fileFromDatabase = false
                    bean.fileType = BaseChooseFileBean.FILE_TYPE_FILE
                    fileList.add(bean)
                }
            }
        }
        fileList.sortWith(FileListComparator())
        return fileList
    }

    /**
     * 查询 Media.Image.Media. 文件信息
     */
    fun queryFileImageWithContextProvider(context: Context): MutableList<BaseChooseFileBean> {
        val paramArray = arrayListOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATA
        )
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            paramArray.toTypedArray(), null, null,
            MediaStore.Images.Media.DEFAULT_SORT_ORDER
        )
        val fileList = mutableListOf<BaseChooseFileBean>()
        cursor?.let {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(paramArray[0]))
                val name = cursor.getString(cursor.getColumnIndex(paramArray[1]))
                val modifiedDate = cursor.getLong(cursor.getColumnIndex(paramArray[2]))
                val size = cursor.getLong(cursor.getColumnIndex(paramArray[3]))
                val path = cursor.getString(cursor.getColumnIndex(paramArray[4]))
                val bean = BaseChooseFileBean()
                bean.fileFromDatabase = true
                bean.fileDatabaseTable = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()
                bean.fileName = name
                bean.filePath = path
                bean.fileChangeDate = formatDatetime(modifiedDate)
                bean.fileDatabaseId = id.toString()
                bean.fileIsDirectory = false
                bean.fileLength = size
                bean.fileType = BaseChooseFileBean.FILE_TYPE_IMAGE
                fileList.add(bean)
            }
        }
        cursor?.close()
        fileList.sortedWith(FileListComparator())
        return fileList
    }

    /**
     * 查询 Media.Audio,Media  文件信息
     */
    fun queryFileAudioWithContentProvider(context: Context): MutableList<BaseChooseFileBean> {
        val paramArray = arrayListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA
        )
        val fileList = mutableListOf<BaseChooseFileBean>()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            paramArray.toTypedArray(), null, null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )

        cursor?.let {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(paramArray[0]))
                val name = cursor.getString(cursor.getColumnIndex(paramArray[1]))
                val modifiedDate = cursor.getLong(cursor.getColumnIndex(paramArray[2]))
                val size = cursor.getLong(cursor.getColumnIndex(paramArray[3]))
                val path = cursor.getString(cursor.getColumnIndex(paramArray[4]))
                val bean = BaseChooseFileBean()
                bean.fileFromDatabase = true
                bean.fileDatabaseTable = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                bean.fileName = name
                bean.filePath = path
                bean.fileChangeDate = formatDatetime(modifiedDate)
                bean.fileDatabaseId = id.toString()
                bean.fileIsDirectory = false
                bean.fileLength = size
                bean.fileType = BaseChooseFileBean.FILE_TYPE_AUDIO
                fileList.add(bean)
            }
        }
        cursor?.close()
        fileList.sortWith(FileListComparator())
        return fileList
    }

    /**
     * 文件排序。 文件夹 > 文件名 ...
     */
    open class FileListComparator : Comparator<BaseChooseFileBean> {
        override fun compare(o1: BaseChooseFileBean?, o2: BaseChooseFileBean?): Int {
            if (o1 == null && o2 == null) {
                return 0
            }
            if (o1 != null && o2 == null) {
                return 1
            }
            if (o1 == null && o2 != null) {
                return -1
            }
            if (o1 == null || o2 == null) {
                return 0
            }
            return if (o1.fileIsDirectory) 1
            else if (o2.fileIsDirectory) -1 else
                o1.fileName.compareTo(o2.fileName)
        }
    }

    /**
     * 时间格式化
     */
    fun formatDatetime(time: Long): String {
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        }
        return simpleDateFormat!!.format(time)
    }

}