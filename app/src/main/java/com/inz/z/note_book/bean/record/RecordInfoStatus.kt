package com.inz.z.note_book.bean.record

import com.inz.z.note_book.database.bean.RecordInfo
import java.util.*

/**
 * 记录信息状态
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 13:45.
 */
class RecordInfoStatus : RecordInfo() {


    /**
     * 是否为标题
     */
    var isTitle: Boolean = false

    /**
     * 标题名称
     */
    var titleName: String = ""

    fun setRecordInfo(recordInfo: RecordInfo) {
        this.id = recordInfo.id
        this.recordTitle = recordInfo.recordTitle
        this.recordContent = recordInfo.recordContent
        this.recordDate = recordInfo.recordDate
        this.enable = recordInfo.enable
        this.createDate = recordInfo.createDate
        this.updateDate = recordInfo.updateDate
    }

    override fun toString(): String {
        return "RecordInfoStatus(isTitle=$isTitle, titleName='$titleName')"
    }


}