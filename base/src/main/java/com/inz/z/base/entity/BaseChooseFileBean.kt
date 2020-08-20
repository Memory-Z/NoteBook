package com.inz.z.base.entity

import androidx.annotation.NonNull

/**
 * 文件选择 实例
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 14:08.
 */
class BaseChooseFileBean {
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