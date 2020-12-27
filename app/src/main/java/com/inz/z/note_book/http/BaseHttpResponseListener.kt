package com.inz.z.note_book.http

import com.inz.z.note_book.http.observer.MyHttpResponseException

/**
 *
 * Http 请求返回结果监听
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 10:20.
 */
interface BaseHttpResponseListener<T> {

    /**
     * 开始
     */
    fun onStart()

    /**
     * 请求成功
     */
    fun onSuccess(data: T)

    /**
     * 失败
     */
    fun onFailure(e: MyHttpResponseException)

    /**
     * 结束
     */
    fun onFinish()

}