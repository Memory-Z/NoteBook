package com.inz.z.note_book.http.observer

import com.google.gson.JsonParseException
import retrofit2.HttpException

/**
 *
 * Http 请求异常 处理
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 10:35.
 */
object MyHttpResponseExceptionHandler {


    fun handleException(e: Throwable): MyHttpResponseException {
        return when (e) {
            is HttpException -> {
                MyHttpResponseException(e.code(), e.message(), e)
            }
            // TODO: 2020/10/28 添加自定义错误类型
            else -> {
                MyHttpResponseException(e)
            }
        }
    }
}