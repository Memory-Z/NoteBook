package com.inz.z.note_book.view.activity

import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.response.LoginResponse
import com.inz.z.note_book.http.BaseHttpResponseListener
import com.inz.z.note_book.http.HttpModel
import com.inz.z.note_book.http.observer.MyHttpResponseException
import com.inz.z.note_book.view.BaseNoteActivity
import kotlinx.android.synthetic.main.activity_new_dynamic.*

/**
 * 新动态  界面 -
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 09:41.
 */
class NewDynamicActivity : BaseNoteActivity() {

    companion object {
        const val TAG = "NewDynamicActivity"
    }

    override fun initWindow() {}
    override fun getLayoutId(): Int {
        return R.layout.activity_new_dynamic
    }

    override fun initView() {
        new_dynamic_top_submit_tv.setOnClickListener {
            HttpModel.loginByPwd("", "",
                object : BaseHttpResponseListener<LoginResponse> {
                    override fun onStart() {
                        L.i(TAG, "loginByPwd: onStart: ")
                    }

                    override fun onSuccess(data: LoginResponse) {
                        L.i(TAG, "loginByPwd: onSuccess: $data")
                    }

                    override fun onFailure(e: MyHttpResponseException) {
                        L.i(TAG, "loginByPwd: onFailure ================================  ${e.errorCode}")
                        L.e(TAG, "loginByPwd: onFailure", e.originThrowable)
                    }

                    override fun onFinish() {
                        L.i(TAG, "loginByPwd: onFinish")
                    }
                }
            )
        }
    }

    override fun initData() {}
}