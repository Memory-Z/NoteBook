package com.inz.z.base.entity

import androidx.annotation.IntDef
import androidx.annotation.NonNull
import java.io.Serializable

/**
 * 文件选择 实例
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 14:08.
 */
class BaseChooseFileBean : Serializable {

    companion object {
        const val FILE_TYPE_FILE = 0x0000A001
        const val FILE_TYPE_IMAGE = 0x0000A002
        const val FILE_TYPE_AUDIO = 0x0000A003
        const val FILE_TYPE_VIDEO = 0x0000A004
        const val FILE_TYPE_TEXT = 0x0000A005
        const val FILE_TYPE_APPLICATION = 0x0000A006
        const val FILE_TYPE_OTHER = 0x0000A007
    }

    @IntDef(
        FILE_TYPE_FILE,
        FILE_TYPE_IMAGE,
        FILE_TYPE_AUDIO,
        FILE_TYPE_VIDEO,
        FILE_TYPE_TEXT,
        FILE_TYPE_APPLICATION,
        FILE_TYPE_OTHER
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class FileType

    /**
     * 文件名
     */
    var fileName: String = ""

    /**
     * 文件地址
     */
    var filePath: String = ""

    /**
     * 文件大小
     */
    var fileLength: Long = 0

    /**
     * 文件是否为文件夹
     */
    var fileIsDirectory: Boolean = false

    /**
     * 文件类型
     */
    @FileType
    var fileType: Int = FILE_TYPE_FILE

    /**
     * 文件修改日期
     */
    var fileChangeDate: String = "";

    /**
     * 文件是否来自数据库
     */
    var fileFromDatabase: Boolean = false

    /**
     * 文件所属表
     */
    var fileDatabaseTable: String = ""

    /**
     * 文件 数库 id
     */
    var fileDatabaseId: String = ""

    /**
     * 是否选中
     */
    var checked: Boolean = false

    /**
     * 是否可以选择
     */
    var canChoose: Boolean = true

    @NonNull
    override fun toString(): String {
        return "BaseChooseFileBean(fileName='$fileName', filePath='$filePath', fileLength=$fileLength, fileIsDirectory=$fileIsDirectory, fileChangeDate='$fileChangeDate', fileFromDatabase=$fileFromDatabase, fileDatabaseTable='$fileDatabaseTable', fileDatabaseId='$fileDatabaseId', checked=$checked, canChoose=$canChoose)"
    }


}