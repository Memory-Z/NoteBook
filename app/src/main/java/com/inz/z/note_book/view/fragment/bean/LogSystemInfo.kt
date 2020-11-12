package com.inz.z.note_book.view.fragment.bean

import com.inz.z.base.util.FileUtils

/**
 * 系统日志信息
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 14:15.
 */
class LogSystemInfo {

    var fileName: String = ""
    var filePath: String = ""

    /**
     * 最后一项
     */
    var lastItem: Boolean = false

    /**
     * 是否加载更多
     */
    var loadMore: Boolean = false

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    fun getFileSize(): String {
        return FileUtils.formatFileSize(FileUtils.getFileSize(filePath))
    }

}