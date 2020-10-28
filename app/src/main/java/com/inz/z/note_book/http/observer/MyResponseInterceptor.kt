package com.inz.z.note_book.http.observer

import com.inz.z.base.util.L
import okhttp3.Interceptor
import okhttp3.Response

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 11:48.
 */
class MyResponseInterceptor : Interceptor {
    companion object {
        const val TAG = "MyResponseInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val origin = chain.request()
        val response = chain.proceed(origin)
        val requestUrlStr = response.request().url().toString()
        L.i(TAG, "intercept: url = $requestUrlStr")
        getHeader(response)
        return response
    }

    private fun getHeader(response: Response) {
        val headers = response.headers()
        val headerMap = headers.toMultimap()
        val keys = headerMap.keys
        val interator = keys.iterator()
        while (interator.hasNext()) {
            val key = interator.next()
            val data = headerMap.get(key)
            L.i(TAG, "getHeaderMap: $key : $data ")
        }


    }
}