package com.inz.z.base.entity

import android.os.Parcel
import android.os.Parcelable
import com.inz.z.base.base.FileType
import com.inz.z.base.base.FileTypeAnn
import java.io.Serializable

/**
 * 文件选择 实例
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 14:08.
 */
open class BaseChooseFileBean : Serializable, Parcelable {
    constructor()

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
    @FileTypeAnn
    var fileType: Int = FileType.FILE_TYPE_FILE

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

    constructor(parcel: Parcel) : this() {
        fileName = parcel.readString() ?: ""
        filePath = parcel.readString() ?: ""
        fileLength = parcel.readLong()
        fileIsDirectory = parcel.readByte() != 0.toByte()
        fileType = parcel.readInt()
        fileChangeDate = parcel.readString() ?: ""
        fileFromDatabase = parcel.readByte() != 0.toByte()
        fileDatabaseTable = parcel.readString() ?: ""
        fileDatabaseId = parcel.readString() ?: ""
        checked = parcel.readByte() != 0.toByte()
        canChoose = parcel.readByte() != 0.toByte()
    }

    override fun toString(): String {
        return "BaseChooseFileBean(fileName='$fileName', filePath='$filePath', fileLength=$fileLength, fileIsDirectory=$fileIsDirectory, fileType=$fileType, fileChangeDate='$fileChangeDate', fileFromDatabase=$fileFromDatabase, fileDatabaseTable='$fileDatabaseTable', fileDatabaseId='$fileDatabaseId', checked=$checked, canChoose=$canChoose)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileName)
        parcel.writeString(filePath)
        parcel.writeLong(fileLength)
        parcel.writeByte(if (fileIsDirectory) 1 else 0)
        parcel.writeInt(fileType)
        parcel.writeString(fileChangeDate)
        parcel.writeByte(if (fileFromDatabase) 1 else 0)
        parcel.writeString(fileDatabaseTable)
        parcel.writeString(fileDatabaseId)
        parcel.writeByte(if (checked) 1 else 0)
        parcel.writeByte(if (canChoose) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseChooseFileBean> {
        override fun createFromParcel(parcel: Parcel): BaseChooseFileBean {
            return BaseChooseFileBean(parcel)
        }

        override fun newArray(size: Int): Array<BaseChooseFileBean?> {
            return arrayOfNulls(size)
        }
    }


}