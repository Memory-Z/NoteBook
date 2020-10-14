package com.inz.z.base.util

import android.content.Context
import android.text.TextUtils
import com.inz.z.base.R
import com.inz.z.base.entity.xml.FileTypeHeaderBean
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/13 09:15.
 */
class FileTypeHelper(val mContext: Context) {

    companion object {
        private var fileTypeHeaderList: MutableList<FileTypeHeaderBean>? = null
        private val IMAGE_HEADER_TYPE = arrayListOf(
            "JPG", "GIF", "PNG", "JPEG"
        )
    }


    /**
     * 获取文件头列表
     */
    fun getFileTypeHeaderList(): MutableList<FileTypeHeaderBean>? {
        if (fileTypeHeaderList == null) {
            val xmlParser = mContext.resources.getXml(R.xml.file_type_header)
            fileTypeHeaderList = XmlFileUtils.getXmlValueDataList(
                xmlParser,
                FileTypeHeaderBean.HEADER_TAG,
                FileTypeHeaderBean::class.java
            )
        }
        return fileTypeHeaderList
    }

    /**
     * 通过文件头 获取文件类型
     */
    fun getFileTypeByHeader(file: File): String {
        if (!file.exists() || file.isDirectory) {
            return ""
        }
        val headerList = getFileTypeHeaderList()
        try {
            val inputStream = FileInputStream(file)
            val bytes = ByteArray(16)
            inputStream.read(bytes)
            val sb = StringBuilder()
            bytes.forEach {
                val b = it.toInt().and(0xFF)
                val str = b.toString(16)
                if (str.length < 2) {
                    sb.append(0)
                }
                sb.append(str)
            }
            val headerStr = sb.toString().toUpperCase(Locale.getDefault())
            headerList?.forEach {
                val s = it.value.toUpperCase(Locale.getDefault())
                if (headerStr.contains(s)) {
                    return it.name
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 通过文件后缀获取文件类型
     */
    fun getFileTypeByName(file: File): String {
        if (!file.exists() || file.isDirectory) {
            return ""
        }
        val fileName = file.name
        var fileType = ""
        val index = fileName.lastIndexOf(".")
        if (index != -1) {
            fileType = fileName.substring(index + 1)
        }
        return fileType
    }

    /**
     * 文件 是否为图片类型
     */
    fun isImageWithFile(file: File): Boolean {
        if (!file.exists() || file.isDirectory) {
            return false
        }
        val fileTypeByHeader = getFileTypeByHeader(file)
        val fileTypeByName = getFileTypeByName(file)
        if (!TextUtils.isEmpty(fileTypeByHeader)) {
            val headerStr = fileTypeByHeader.toUpperCase(Locale.getDefault())
            val nameStr = fileTypeByName.toUpperCase(Locale.getDefault())
            return IMAGE_HEADER_TYPE.contains(headerStr) || IMAGE_HEADER_TYPE.contains(nameStr)
        }
        return false

    }

}