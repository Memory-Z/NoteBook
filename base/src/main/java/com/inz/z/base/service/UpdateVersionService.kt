package com.inz.z.base.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.text.TextUtils
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend
import java.io.File

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/03 13:53.
 */
class UpdateVersionService : Service() {

    private var mConstants: Constants? = null

    private var downloadTask: DownloadTask? = null

    companion object {
        private const val TAG = "UpdateVersionService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        bundle?.let {
            val fileUrl =
                bundle.getString(Constants.VersionUpdate.VERSION_UPDATE_URL, "")
            if (!TextUtils.isEmpty(fileUrl)) {
                startDownload(fileUrl)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadTask?.cancel()
        downloadTask = null
    }

    private fun initConstant(context: Context) {
        try {
            val componentName = ComponentName(context, UpdateVersionService::class.java)
            val activityInt =
                context.packageManager.getServiceInfo(componentName, PackageManager.GET_META_DATA)
            val applicationIdStr = activityInt.metaData.getString("applicationId")
            if (!TextUtils.isEmpty(applicationIdStr)) {
                createConstant(applicationIdStr!!)
            }
        } catch (e: Exception) {
            L.e(TAG, "initConstant: ", e)
        }
    }

    private fun createConstant(applicationId: String) {
        if (mConstants == null) {
            mConstants = Constants.getInstant(applicationId)
        }
    }


    private fun startDownload(fileUrl: String) {
        initConstant(applicationContext)
        DownloadTaskThread(fileUrl, SystemClock.currentThreadTimeMillis().toString()).start()
    }

    /**
     * 任务线程
     */
    private inner class DownloadTaskThread(val fileUrl: String, threadName: String) :
        Thread(threadName) {
        override fun run() {
            super.run()
            if (downloadTask == null) {
                val file = File(LauncherHelper.getCacheDownloadVersionPath(applicationContext))
                downloadTask = DownloadTask.Builder(fileUrl, file)
                    .setWifiRequired(false)
                    .setAutoCallbackToUIThread(false)
                    .setPassIfAlreadyCompleted(false)
                    .setMinIntervalMillisCallbackProcess(100)
                    .setConnectionCount(1)
                    .build()
                downloadTask?.execute(OkDownloadSpeedListenerImpl())
            } else {
                L.w(TAG, "startDownload: task is downloading. ")
            }
        }
    }

    private inner class OkDownloadSpeedListenerImpl : DownloadListener4WithSpeed() {
        private var totalProgress: Long = 0L
        override fun taskStart(task: DownloadTask) {

        }

        override fun infoReady(
            task: DownloadTask,
            info: BreakpointInfo,
            fromBreakpoint: Boolean,
            model: Listener4SpeedAssistExtend.Listener4SpeedModel
        ) {
            totalProgress = info.totalLength
        }

        override fun blockEnd(
            task: DownloadTask,
            blockIndex: Int,
            info: BlockInfo?,
            blockSpeed: SpeedCalculator
        ) {

        }

        override fun taskEnd(
            task: DownloadTask,
            cause: EndCause,
            realCause: Exception?,
            taskSpeed: SpeedCalculator
        ) {
            L.i(TAG, "OkDownloadSpeedListenerImpl > taskEnd: $cause -- $realCause")
            val downloadSuccess = cause == EndCause.COMPLETED
            val failureMessage = realCause?.message ?: ""
            val filePath = task.file?.absolutePath ?: ""
            sendDownloadFinish(task.url, filePath, downloadSuccess, failureMessage)
        }

        override fun progress(task: DownloadTask, currentOffset: Long, taskSpeed: SpeedCalculator) {
            L.i(TAG, "OkDownloadSpeedListenerImpl > progress: $currentOffset")
            sendProgressBroadcast(currentOffset, totalProgress, task.url)
        }

        override fun connectEnd(
            task: DownloadTask,
            blockIndex: Int,
            responseCode: Int,
            responseHeaderFields: MutableMap<String, MutableList<String>>
        ) {
        }

        override fun connectStart(
            task: DownloadTask,
            blockIndex: Int,
            requestHeaderFields: MutableMap<String, MutableList<String>>
        ) {
        }

        override fun progressBlock(
            task: DownloadTask,
            blockIndex: Int,
            currentBlockOffset: Long,
            blockSpeed: SpeedCalculator
        ) {
        }


    }

    /**
     * send download progress broadcast .
     */
    private fun sendProgressBroadcast(progress: Long, totalProgress: Long, fileUrl: String) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putString(Constants.VersionUpdate.VERSION_UPDATE_URL, fileUrl)
        bundle.putLong(Constants.VersionUpdate.VERSION_UPDATE_PROGRESS, progress)
        bundle.putLong(Constants.VersionUpdate.VERSION_UPDATE_TOTAL, totalProgress)
        intent.putExtras(bundle)
        intent.action = mConstants?.VERSION_UPDATE_DOWNLOAD_ACTION
        intent.`package` = packageName
        sendBroadcast(intent, Constants.VersionUpdate.VERSION_UPDATE_PERMISSION)
    }

    /**
     * send download finish broadcast
     */
    private fun sendDownloadFinish(
        fileUrl: String,
        filePath: String,
        success: Boolean,
        failureMessage: String?
    ) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putString(Constants.VersionUpdate.VERSION_UPDATE_URL, fileUrl)
        bundle.putString(Constants.VersionUpdate.VERSION_UPDATE_FILE_PATH, filePath)
        bundle.putBoolean(Constants.VersionUpdate.VERSION_UPDAE_SUCCESS, success)
        bundle.putBoolean(Constants.VersionUpdate.VERSION_UPDATE_FAILURE, !success)
        bundle.putString(
            Constants.VersionUpdate.VERSION_UPDATE_FAILURE_MESSAGE,
            failureMessage ?: ""
        )
        intent.putExtras(bundle)
        intent.action = mConstants?.VERSION_UPDATE_DOWNLOAD_ACTION
        intent.`package` = packageName
        sendBroadcast(intent, Constants.VersionUpdate.VERSION_UPDATE_PERMISSION)
        downloadTask = null
    }

}