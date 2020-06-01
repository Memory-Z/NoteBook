package com.inz.z.note_book.base

import android.os.Handler
import android.os.Message
import com.inz.z.base.view.AbsBaseActivity
import java.lang.ref.WeakReference

/**
 * Handler 回调监听实现
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/29 11:24.
 */
abstract class AbsHandlerCallback(activity: AbsBaseActivity) : Handler.Callback {
    private var activity: WeakReference<AbsBaseActivity>? = null

    init {
        this.activity = WeakReference<AbsBaseActivity>(activity)
    }

    override fun handleMessage(msg: Message): Boolean {
        if (activity?.get() == null) {
            return false
        }
        return handleMessage(msg, activity?.get()!!)
    }

    abstract fun handleMessage(msg: Message, activity: AbsBaseActivity): Boolean
}