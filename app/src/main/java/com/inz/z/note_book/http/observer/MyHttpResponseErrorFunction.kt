package com.inz.z.note_book.http.observer

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.functions.Function

/**
 * Http 请求异常 处理
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 10:25.
 */
class MyHttpResponseErrorFunction<T> : Function<Throwable, Observable<T>> {

    override fun apply(t: Throwable): Observable<T> {
        return Observable.error(MyHttpResponseExceptionHandler.handleException(t))
    }
}