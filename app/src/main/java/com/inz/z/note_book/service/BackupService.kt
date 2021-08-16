package com.inz.z.note_book.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.inz.z.base.util.ThreadPoolUtils
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.SPHelper
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * 备份服务。
 * * 数据库备份
 * * xml 文件备份
 *
 * ====================================================
 * Create by 11654 in 2021/8/15 21:21
 */
class BackupService : Service() {
    companion object {
        private const val TAG = "BackupService"
    }

    /**
     * 备份线程池.
     */
    private var scheduleThreadPoolExecutor: ScheduledExecutorService? = null
    private var backupRunnable: BackupRunnable? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化线程池。
        scheduleThreadPoolExecutor = ThreadPoolUtils.getScheduleThread("${TAG}_BACKUP_SERVICE")
        // 发送启动状态广播
        val createIntent = Intent()
        createIntent.`package` = packageName
        createIntent.action = Constants.BaseBroadcastParams.BACKUP_SERVICE_CREATE_ACTION
        sendBroadcast(createIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 发送销毁状态广播
        val destroyIntent = Intent()
        destroyIntent.`package` = packageName
        destroyIntent.action = Constants.BaseBroadcastParams.BACKUP_SERVICE_DESTROY_ACTION
        sendBroadcast(destroyIntent)
        // 数据重置。
        if (backupRunnable != null) {
            backupRunnable = null
        }
        if (scheduleThreadPoolExecutor != null) {
            scheduleThreadPoolExecutor = null;
        }
    }

    /**
     * 备份线程.
     */
    private inner class BackupRunnable : Runnable {
        override fun run() {
            GreenDaoHelper.getInstance().backupDatabase()
            TODO("Not yet implemented")
        }
    }


}