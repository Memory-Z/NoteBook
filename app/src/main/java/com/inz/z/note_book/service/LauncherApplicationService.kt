package com.inz.z.note_book.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.inz.z.note_book.util.Constants
import java.util.*

/**
 *
 * 启动应用服务
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/12 17:28.
 */
class LauncherApplicationService : Service() {
    companion object {
        private const val TAG = "LauncherApplicationService"
    }

    private var binder: LauncherAppServiceBinder? = null

    override fun onBind(intent: Intent?): IBinder? {
        if (binder == null) {
            binder = LauncherAppServiceBinder()
        }
        return binder
    }

    inner class LauncherAppServiceBinder : Binder() {
        fun getService(): LauncherApplicationService {
            return this@LauncherApplicationService
        }
    }



}