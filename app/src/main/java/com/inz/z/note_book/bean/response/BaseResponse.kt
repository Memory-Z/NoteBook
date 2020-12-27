package com.inz.z.note_book.bean.response

import java.io.Serializable

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/26 10:59.
 */
open class BaseResponse<T> : Serializable {

    @ResponseCode.Code
    var code: Int = ResponseCode.SUCCESS_CODE

    var data: T? = null

    var message: String? = ""
}