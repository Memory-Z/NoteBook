package com.inz.z.base.entity.xml

/**
 * 文件类型 文件头标识
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/12 14:49.
 */
class FileTypeHeaderBean {
    companion object {
        const val ROOT_TAG = "file_type"
        const val HEADER_TAG = "header"
    }

    var name = ""
    var value = ""
    var detail = ""

    override fun toString(): String {
        return "FileTypeHeaderBean: {" +
                "name = $name; " +
                "value = $value; " +
                "detail = $detail" +
                "}"
    }


}