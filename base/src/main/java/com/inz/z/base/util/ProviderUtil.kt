package com.inz.z.base.util

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import com.inz.z.base.BuildConfig
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.Constants
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 14:33.
 */
object ProviderUtil {
    var simpleDateFormat: SimpleDateFormat? = null
    val TAG = "ProviderUtil"

    /**
     * 通过文件目录查询文件列表
     * @param filePath 文件目录地址
     */
    @NonNull
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
                    bean.fileType = Constants.FileType.FILE_TYPE_FILE
                    fileList.add(bean)
                    Log.i(TAG, "queryFileListByDir: ----------> ${bean.fileName} ")
                }
            }
        }
        fileList.sortWith(FileListComparator())
        return fileList
    }

    /**
     * 查询 Media.Image.Media. 文件信息
     */
    @NonNull
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
                bean.fileType = Constants.FileType.FILE_TYPE_IMAGE
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
    @NonNull
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
                bean.fileType = Constants.FileType.FILE_TYPE_AUDIO
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
            return if (o1.fileIsDirectory && !o2.fileIsDirectory) {
                -1
            } else if (!o1.fileIsDirectory && o2.fileIsDirectory) {
                1
            } else if (TextUtils.isEmpty(o1.fileName) || TextUtils.isEmpty(o2.fileName)) {
                0
            } else {
                o1.fileName.compareTo(o2.fileName)
            }
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

    /**
     * 插入图片文件
     * @param context 上下文
     * @param filePath 文件实际地址
     * @param fileName 文件名
     * @param mimeType 文件类型 如：image/JPEG
     */
    fun insertImageFileToDCIM(
        context: Context,
        filePath: String,
        fileName: String
    ) {
        try {
            val contentValues = ContentValues()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "DCIM/${BuildConfig.LIBRARY_PACKAGE_NAME}"
                )
            } else {
                contentValues.put(
                    MediaStore.Images.Media.DATA,
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                )
            }
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            val fileNameMap = URLConnection.getFileNameMap()
            var mimeType = fileNameMap.getContentTypeFor(fileName.replace("#", ""))
            if (mimeType == null) {
                mimeType = "*/*"
            }
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            val contentProvider = context.contentResolver
            val uri =
                contentProvider.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.apply {
                val outputStream = contentProvider.openOutputStream(this)
                outputStream?.let {
                    val file = File(filePath)
                    if (file.exists()) {
                        val inputStream = FileInputStream(file)
                        val byteArray = ByteArray(1024)
                        var reader = inputStream.read(byteArray)
                        while ((reader) != -1) {
                            outputStream.write(byteArray, 0, reader)
                            reader = inputStream.read(byteArray)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            L.e(TAG, "insertImageFileToDCIM: ", e)
        }

    }

}