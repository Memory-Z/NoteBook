package com.inz.z.base

import android.app.Application
import android.content.Context
import com.inz.z.base.util.CrashHandler
import com.inz.z.base.util.SPHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/4/15 9:17.
 */
open class BaseConfig(var mContext: Context?) {


    fun setInitData() {
        SPHelper.getInstance()
    }
}