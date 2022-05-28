package com.inz.z.note_book.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.inz.z.base.util.L
import com.inz.z.base.util.ToastUtil
import com.inz.z.note_book.service.BackupService
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.ViewUtil

/**
 *
 * 应用广播 > 监听启动 相关服务
 *
 * ====================================================
 * Create by 11654 in 2021/8/15 22:01
 */
class ApplicationStatusBroadcast : BroadcastReceiver() {
    companion object {
        private const val TAG = "ApplicationBroadcast"

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.w(TAG, "onReceive: Context or Intent is null. ")
            return
        }
        val action = intent.action
        Log.i(TAG, "onReceive: ACTION = ${action}")
        val bundle = intent.extras
        when (action) {
            // 应用启动广播
            Constants.BaseBroadcastParams.APPLICATION_CREATE_ACTION -> {
                // 启动 备份服务
                val backupService = Intent(context, BackupService::class.java)
                context.startService(backupService)
            }
            // 应用暂停广播
            Constants.BaseBroadcastParams.APPLICATION_PAUSE_ACTION -> {

            }
            // 备份服务启动广播
            Constants.BaseBroadcastParams.BACKUP_SERVICE_CREATE_ACTION -> {
                Log.w(TAG, "onReceive: BackupService is running !!! ")
            }
            // 备份服务销毁广播
            Constants.BaseBroadcastParams.BACKUP_SERVICE_DESTROY_ACTION -> {
                Log.w(TAG, "onReceive: BackupService is Destroy !!! ")
            }
            // 创建 LovePanel 失败
            Constants.BaseBroadcastParams.CREATE_LOVE_PANEL_FAILURE_ACTION -> {
                L.w(TAG, "onReceive: Create LovePanel failure . ")
                val message =
                    bundle?.getString(Constants.BaseBroadcastParams.CREATE_LOVE_PANEL_FAILURE_MESSAGE_TAG)
                message?.let {
                    ToastUtil.showToast(context, it)
                }
            }
            else -> {

            }
        }

    }


}