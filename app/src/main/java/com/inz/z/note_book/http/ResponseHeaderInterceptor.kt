package com.inz.z.note_book.http

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.*

/**
 * 请求头添加参数
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/15 09:13.
 */
class ResponseHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader(
                "timestamp",
                Calendar.getInstance(Locale.getDefault()).timeInMillis.toString()
            )
            .build()
        return chain.proceed(request)
    }
}