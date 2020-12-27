package com.inz.z.note_book.view.fragment.bean

import com.inz.slide_table.bean.SlideTableBean
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.database.bean.OperationLogInfo

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/09 17:02.
 */
class LogOperationSlideTableBean : SlideTableBean<OperationLogInfo>() {

    override fun getRowTitle(): String? {
        return data.operationLogId
    }

    override fun toDataColumnList(): MutableList<String> {
        return mutableListOf(
            data.tableName,
            data.operationType,
            data.operationDescribe,
            data.operationData,
            BaseTools.getBaseDateFormat().format(data.createTime),
            BaseTools.getBaseDateFormat().format(data.updateTime)
        )
    }

    override fun toDataHeaderList(): MutableList<String> {
        return mutableListOf(
            "表名",
            "操作类型",
            "操作描述",
            "操作数据",
            "创建时间",
            "更新时间"
        )
    }
}