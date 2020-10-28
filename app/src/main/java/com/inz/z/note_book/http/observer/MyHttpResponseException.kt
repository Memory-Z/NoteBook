package com.inz.z.note_book.http.observer

import java.lang.Exception

/**
 * Http 请求异常
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 10:22.
 */
class MyHttpResponseException : Exception {

    var errorCode: Int = -1
    var originThrowable: Throwable? = null

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause) {
        this.originThrowable = cause
    }

    constructor(message: String?, cause: Throwable?) : this(-1, message, cause)
    constructor(code: Int, message: String?, cause: Throwable?) : super(message, cause) {
        this.errorCode = code
        this.originThrowable = cause
    }


}