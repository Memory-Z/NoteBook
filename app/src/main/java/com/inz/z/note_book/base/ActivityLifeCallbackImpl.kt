package com.inz.z.note_book.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Message
import java.util.concurrent.atomic.AtomicInteger

/**
 * Activity 生命周期监听
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/22 11:09.
 */
class ActivityLifeCallbackImpl private constructor() : Application.ActivityLifecycleCallbacks {

    private val lifeHandler: Handler
    private val visibleCount = AtomicInteger(0)

    companion object {
        private var activityLifeCallbackListener: ActivityLifeCallbackListener? = null

        private const val HANDLER_ACTIVITY_RESUME = 0x080010
        private const val HANDLER_ACTIVITY_PAUSED = 0x080011

        private var instant: ActivityLifeCallbackImpl? = null

        fun init(application: Application, listener: ActivityLifeCallbackListener?) {
            synchronized(this) {
                if (instant == null) {
                    instant =
                        ActivityLifeCallbackImpl()
                }
                application.registerActivityLifecycleCallbacks(instant)
            }
            activityLifeCallbackListener = listener
        }
    }

    init {
        lifeHandler = Handler(LifeHandlerCallback())
    }

    override fun onActivityPaused(activity: Activity) {
        lifeHandler.sendEmptyMessage(HANDLER_ACTIVITY_PAUSED)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityResumed(activity: Activity) {
        lifeHandler.sendEmptyMessage(HANDLER_ACTIVITY_RESUME)
    }


    private inner class LifeHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            var size = -1
            when (msg.what) {
                HANDLER_ACTIVITY_RESUME -> {
                    synchronized(visibleCount) {
                        size = visibleCount.addAndGet(1)
                    }
                }
                HANDLER_ACTIVITY_PAUSED -> {
                    synchronized(visibleCount) {
                        size = visibleCount.addAndGet(-1)
                    }
                }
                else -> {

                }
            }
            if (size != -1 || size <= 1) {
                activityLifeCallbackListener?.applicationInBackground(size == 0)
            }
            return true
        }
    }

    interface ActivityLifeCallbackListener {
        fun applicationInBackground(background: Boolean)
    }
}