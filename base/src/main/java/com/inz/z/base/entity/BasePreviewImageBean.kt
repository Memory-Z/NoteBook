package com.inz.z.base.entity

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/15 09:52.
 */
class BasePreviewImageBean : BaseChooseFileBean {

    constructor() : super()

    constructor(bean: BaseChooseFileBean) {
        this.fileName = bean.fileName
        this.filePath = bean.filePath
        this.fileLength = bean.fileLength
        this.fileIsDirectory = bean.fileIsDirectory
        this.fileType = bean.fileType
        this.fileChangeDate = bean.fileChangeDate
        this.fileFromDatabase = bean.fileFromDatabase
        this.fileDatabaseTable = bean.fileDatabaseTable
        this.fileDatabaseId = bean.fileDatabaseId
        this.checked = bean.checked
        this.canChoose = bean.canChoose
    }

    /**
     * 是否为当前预览图片
     */
    var isSelectedPreview: Boolean = false
    override fun toString(): String {
        return "BasePreviewImageBean(isSelectedPreview=$isSelectedPreview)"
    }


}