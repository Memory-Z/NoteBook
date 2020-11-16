package com.inz.z.note_book.http

import com.inz.z.note_book.bean.response.LoginResponse
import com.inz.z.note_book.http.observer.MyDefaultObserver
import com.inz.z.note_book.http.observer.MyHttpResponseErrorFunction
import com.inz.z.note_book.http.observer.MyHttpResponseException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 10:18.
 */
object HttpModel {

    /**
     * 在主线程中执行
     */
    private fun <T> runOnMainThread(
        observable: Observable<T>,
        listener: BaseHttpResponseListener<T>?
    ) {
        observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext(MyHttpResponseErrorFunction<T>())
            .retry(3)
            .subscribe(object : MyDefaultObserver<T>() {
                override fun onStart() {
                    super.onStart()
                    listener?.onStart()
                }

                override fun onNext(t: T) {
                    listener?.onSuccess(t)
                }

                override fun onError(e: MyHttpResponseException) {
                    listener?.onFailure(e)
                }

                override fun onComplete() {
                    listener?.onFinish()
                }
            })
    }

    /**
     * 工作线程中 执行
     */
    private fun <T> runOnWorkThread(
        observable: Observable<T>,
        listener: BaseHttpResponseListener<T>?
    ) {
        observable
            .observeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext(MyHttpResponseErrorFunction<T>())
            .retry(3)
            .subscribe(object : MyDefaultObserver<T>() {
                override fun onStart() {
                    super.onStart()
                    listener?.onStart()
                }

                override fun onNext(t: T) {
                    listener?.onSuccess(t)
                }

                override fun onError(e: MyHttpResponseException) {
                    listener?.onFailure(e)
                }

                override fun onComplete() {
                    listener?.onFinish()
                }
            })
    }

    fun loginByPwd(
        userName: String,
        password: String,
        listener: BaseHttpResponseListener<LoginResponse>
    ) {
        runOnMainThread(
            HttpUtil.loginByPwd(userName, password),
            listener
        )
    }
}