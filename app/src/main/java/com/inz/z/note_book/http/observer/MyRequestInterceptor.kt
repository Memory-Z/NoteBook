package com.inz.z.note_book.http.observer

import android.os.Build
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.util.Constants
import okhttp3.*
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 11:23.
 */
class MyRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val origin: Request = chain.request()
        val requestBody = origin.body()
        val requestBuilder = origin.newBuilder()
        if (requestBody is FormBody) {
            val newFormBodyBuilder = FormBody.Builder()
            for (index in 0 until requestBody.size()) {
                newFormBodyBuilder.add(requestBody.name(index), requestBody.value(index))
            }
            val timeStamp = System.currentTimeMillis().toString()
            newFormBodyBuilder
                .add(
                    Constants.HttpRequestParams.VERSION_NAME,
                    BuildConfig.VERSION_NAME
                )
                .add(Constants.HttpRequestParams.VERSION_CODE, BuildConfig.VERSION_CODE.toString())
                .add(Constants.HttpRequestParams.TIME_STAMP, timeStamp)
            requestBuilder.method(origin.method(), newFormBodyBuilder.build())
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}