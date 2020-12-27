package com.inz.z.note_book.http.observer

import io.reactivex.observers.DefaultObserver

/**
 * 自定义监听
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 14:07.
 */
abstract class MyDefaultObserver<T> : DefaultObserver<T>() {

    override fun onError(e: Throwable) {
        onError(MyHttpResponseExceptionHandler.handleException(e))
    }

    abstract fun onError(e: MyHttpResponseException)
}