package com.inz.z.note_book.http

import com.inz.z.note_book.bean.response.BaseResponse
import com.inz.z.note_book.bean.response.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 网络请求地址
 * Create By 11654
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create By 2018/8/12 14:06
 */
internal interface RetrofitInterface {
    /**
     * 用户登录
     * @param userName 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("https://github.com/")
    fun login(
        @Field("userName") userName: String,
        @Field("password") password: String
    ): Observable<LoginResponse>

    /**
     * 用户手机号登录
     * @param userPhone 电话
     * @param verificationCode 验证码
     */
    @FormUrlEncoded
    @POST("")
    fun loginByPhone(
        @Field("userPhone") userPhone: String,
        @Field("phoneVerificationCode") verificationCode: String
    ): Observable<LoginResponse>

    /**
     * 通过手机号获取验证码
     * @param phoneNumber 手机号
     * @param token 用户 Token 可为 null
     * @param type 验证码类型 DEF: 1
     */
    fun getVerificationCodeByPhone(
        @Field("phone") phoneNumber: String,
        @Field("token") token: String?,
        @Field("type") type: String = "0"
    ): Observable<BaseResponse<String>>
}