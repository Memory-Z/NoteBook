package com.inz.z.note_book.database.controller

import android.net.Uri
import com.inz.z.note_book.database.FileInfoDao
import com.inz.z.note_book.database.bean.FileInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.database.util.GreenDaoOpenHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/22 14:59.
 */
object FileInfoController {

    private fun getFileInfoDao(): FileInfoDao? =
        GreenDaoHelper.getInstance().getDaoSession()?.fileInfoDao

    /**
     * 添加文件信息
     */
    fun insertFileInfo(fileInfo: FileInfo) {
        getFileInfoDao()?.apply {
            this.insert(fileInfo)
            LogController.log(LogController.TYPE_INSERT, fileInfo, "添加文件信息", this.tablename)
        }
    }

    /**
     * 更新文件信息
     */
    fun updateFileInfo(fileInfo: FileInfo) {
        getFileInfoDao()?.apply {
            val fileId = fileInfo.fileId
            val oldFileInfo = findFileInfoById(fileId)
            if (oldFileInfo != null) {
                this.update(fileInfo)
                LogController.log(LogController.TYPE_UPDATE, fileInfo, "更新文件信息", this.tablename)
            } else {
                insertFileInfo(fileInfo)
            }
        }
    }

    /**
     * 通过ID 查询文件信息
     */
    fun findFileInfoById(id: Long): FileInfo? {
        getFileInfoDao()?.apply {
            val list = this.queryBuilder()
                .where(
                    FileInfoDao.Properties.FileId.eq(id)
                )
                .list()
            if (!list.isNullOrEmpty()) {
                return list[0]
            }
        }
        return null
    }

    /**
     * 通过uri 查询文件信息
     * @param uri uri
     */
    fun findFileInfoByUri(uri: Uri): FileInfo? {
        val path = uri.path
        path?.let {
            return findFileInfoByPath(it)
        }
        return null
    }

    /**
     * 通过文件地址 查询文件信息
     * @param path 文件地址
     */
    fun findFileInfoByPath(path: String): FileInfo? {
        getFileInfoDao()?.apply {
            val list = this.queryBuilder()
                .where(
                    FileInfoDao.Properties.FilePath.eq(path)
                )
                .orderDesc(FileInfoDao.Properties.UpdateDate)
                .list()
            if (!list.isNullOrEmpty()) {
                return list[0]
            }
        }
        return null
    }
}