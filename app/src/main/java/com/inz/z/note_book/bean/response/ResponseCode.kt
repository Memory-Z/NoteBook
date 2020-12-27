package com.inz.z.note_book.bean.response

import androidx.annotation.IntDef

/**
 * 请求结果 码
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 09:45.
 */
object ResponseCode {

    /**
     * 成功
     */
    const val SUCCESS_CODE = 1000

    /**
     * 失败
     */
    const val FAILURE_CODE = 1001

    /**
     * 未处理
     */
    const val UNKNOWN_CODE = 1999

    /**
     * 错误码注解
     */
    @IntDef(SUCCESS_CODE, FAILURE_CODE, UNKNOWN_CODE)
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    annotation class Code {

    }


}