package com.inz.z.note_book.view.app_widget.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/09 16:56.
 */
class WidgetCountdownTimeService : Service() {

    companion object {
        private const val TIME_FORMAT_SECOND = "%02s.%03s"
        private const val TIME_FORMAT_MIN = "%02s:${TIME_FORMAT_SECOND}"
        private const val TIME_FORMAT_HOUR = "%02s:${TIME_FORMAT_MIN}"

        /**
         * 间隔时间, 单位：ms
         */
        private const val DIF_TIME = 1000L
    }

    private val countTime = AtomicBoolean(false)


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private inner class CountTimeThread : Thread() {
        override fun run() {
            super.run()

        }
    }
}