package com.inz.z.base.util

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.inz.z.base.BuildConfig
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * 线程池工具
 * ====================================================
 * Create by 11654 in 2021/1/1 1:55
 */
object ThreadPoolUtils {
    private const val TAG = "ThreadPoolUtils"

    private var uiThread: MainThreadExecutor
    private var workerThread: ThreadPoolExecutor
    private var scheduleThread: ScheduledExecutorService
    private var singleThread: ExecutorService

    init {
        uiThread = MainThreadExecutor()
        workerThread = ThreadPoolExecutor(
            10,
            20,
            5,
            TimeUnit.MINUTES,
            LinkedBlockingDeque<Runnable>(),
            ThreadGroupThreadFactory("worker")
        )
        scheduleThread = Executors.newScheduledThreadPool(
            10,
            ThreadGroupThreadFactory("schedule")
        )
        singleThread = Executors.newSingleThreadExecutor(
            ThreadGroupThreadFactory("disk")
        )
    }

    /**
     * 线程工厂
     */
    private class ThreadGroupThreadFactory(prefix: String) : ThreadFactory {
        companion object {
            val groupNumber = AtomicInteger(1)
            val threadNumber = AtomicInteger(1)
        }

        var threadGroup: ThreadGroup
        var namePrefix = ""

        init {
            val manager = System.getSecurityManager()
            threadGroup = manager?.threadGroup ?: Thread.currentThread().threadGroup!!
            namePrefix = "$prefix-${groupNumber.getAndIncrement()}-thread-"
        }

        override fun newThread(r: Runnable?): Thread {
            return Thread(
                threadGroup,
                r,
                "$namePrefix${threadNumber.getAndIncrement()}",
                0
            )
                .apply {
                    isDaemon = false
                    priority = Thread.NORM_PRIORITY
                }
        }

    }

    /**
     * 主线程
     */
    class MainThreadExecutor : Executor {
        private val mainHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            command?.let {
                mainHandler.post(it)
            }
        }

        fun postDelayed(command: Runnable?, delay: Long) {
            command?.let {
                mainHandler.postDelayed(it, delay)
            }

        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


    /**
     * 获取UI线程
     */
    fun getUiThread(tag: String): MainThreadExecutor {
        if (BuildConfig.DEBUG || !TextUtils.isEmpty(tag)) {
            Log.i(TAG, "getUiThread: $tag")
        }
        return uiThread
    }

    fun getScheduleThread(tag: String): ScheduledExecutorService {
        if (BuildConfig.DEBUG || !TextUtils.isEmpty(tag)) {
            Log.i(TAG, "getScheduleThread: $tag")
        }
        return scheduleThread
    }

    fun getSingleThread(tag: String): Executor {
        if (BuildConfig.DEBUG || !TextUtils.isEmpty(tag)) {
            Log.i(TAG, "getSingleThread: $tag")
        }
        return singleThread
    }

    fun getWorkThread(tag: String): ThreadPoolExecutor {
        if (BuildConfig.DEBUG || !TextUtils.isEmpty(tag)) {
            Log.i(TAG, "getWorkThread: $tag")
        }
        return workerThread
    }

}