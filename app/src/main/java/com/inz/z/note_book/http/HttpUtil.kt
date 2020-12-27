package com.inz.z.note_book.http

import com.inz.z.base.http.*
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.bean.response.LoginResponse
import com.inz.z.note_book.http.observer.MyRequestInterceptor
import com.inz.z.note_book.http.observer.MyResponseInterceptor
import com.inz.z.note_book.util.Constants
import io.reactivex.Observable
import io.reactivex.Observer
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * HTTP 请求
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/15 09:12.
 */
object HttpUtil {

    /**
     * 默认连接超时时间，单位：秒
     */
    private val DEFAULT_TIMEOUT: Long = 10


    /**
     * 网络连接管理
     */
    private var client: OkHttpClient? = null
    private val textType = MediaType.parse("text/plain")


    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    init {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLogInterceptor())
        }
        // 添加 自定义拦截器
        builder.addInterceptor(MyRequestInterceptor())
        builder.addInterceptor(MyResponseInterceptor())

        client = builder
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }


    /**
     * 获取更新数据接口
     *
     * @return RetrofitInterface
     */
    @Synchronized
    private fun getRetrofitInterface(): RetrofitInterface {
        // 初始化 Retrofit 配置
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client!!)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        if (retrofitInterface == null) {
            retrofitInterface = retrofit!!.create(RetrofitInterface::class.java)
        }
        return retrofitInterface!!
    }

    /**
     * 文件 传输 网络接口
     */
    private class FileRetrofitInterfaceBuilder internal constructor() {
        /**
         * 上传进度监听
         */
        private var progressRequestListener: ProgressRequestListener? = null

        /**
         * 下载进度监听
         */
        private var progressResponseListener: ProgressResponseListener? = null
        private val builder: OkHttpClient.Builder

        // 文件超时时间
        private var fileTimeOut: Long = 2

        /**
         * 添加 上传进度监听 [.setProgressResponseListener]
         *
         * @param progressRequestListener 上传监听
         * @return Builder
         */
        fun setProgressRequestListener(progressRequestListener: ProgressRequestListener?): FileRetrofitInterfaceBuilder {
            this.progressRequestListener = progressRequestListener
            return this
        }

        /**
         * 添加 下载进度监听 [.setProgressRequestListener]
         *
         * @param progressResponseListener 下载监听
         * @return Builder
         */
        fun setProgressResponseListener(progressResponseListener: ProgressResponseListener?): FileRetrofitInterfaceBuilder {
            this.progressResponseListener = progressResponseListener
            return this
        }

        /**
         * 设置文件超时时间
         *
         * @param fileTimeOut 超时时间 单位：分
         * @return Builder
         */
        fun setFileTimeOut(fileTimeOut: Long): FileRetrofitInterfaceBuilder {
            this.fileTimeOut = fileTimeOut
            return this
        }

        fun build(): RetrofitInterface {
            // 上传
            if (progressRequestListener != null) {
                builder.addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .method(
                            original.method(),
                            ProgressRequestBody(original.body(), progressRequestListener)
                        )
                        .build()
                    chain.proceed(request)
                }
            }
            // 下载
            if (progressResponseListener != null) {
                builder.addInterceptor { chain ->
                    val original = chain.proceed(chain.request())
                    original.newBuilder()
                        .body(ProgressResponseBody(original.body(), progressResponseListener))
                        .build()
                }
            }
            // 请求区
            val client =
                builder.connectTimeout(fileTimeOut, TimeUnit.MINUTES)
                    .readTimeout(fileTimeOut, TimeUnit.MINUTES)
                    .writeTimeout(fileTimeOut, TimeUnit.MINUTES)
                    .build()
            // Retrofit
            val fileRetrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return fileRetrofit.create(RetrofitInterface::class.java)
        }

        init {
            builder = OkHttpClient.Builder()
        }
    }


    /**
     * 账号/密码登录
     */
    fun loginByPwd(userName: String, password: String): Observable<LoginResponse> {
        return getRetrofitInterface().login(userName, password)
    }
}