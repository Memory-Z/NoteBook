package com.inz.z.base.util

import android.content.Context
import android.widget.Toast

/**
 * Toast 工具类
 *
 * ====================================================
 * Create by 11654 in 2021/3/28 21:44
 */
public object ToastUtil {
    @Deprecated("need context,")
    fun showToast(message: String) {

    }

    fun showToast(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_SHORT)
    }

    fun showToast(context: Context, message: String, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }


}