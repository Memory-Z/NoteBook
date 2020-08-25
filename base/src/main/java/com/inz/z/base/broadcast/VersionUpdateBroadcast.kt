package com.inz.z.base.broadcast

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import com.inz.z.base.entity.Constants
import com.inz.z.base.service.UpdateVersionService
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import java.io.File
import java.lang.Exception

/**
 * Version update broadcast
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/03 15:00.
 */
public class VersionUpdateBroadcast : BroadcastReceiver() {

    companion object {
        private const val TAG = "VersionUpdateBroadcast"
        private val listenerList = mutableListOf<VersionUpdateBroadcastListener>()

        fun addListener(listener: VersionUpdateBroadcastListener) {
            if (!listenerList.contains(listener)) {
                listenerList.add(listener)
            }
        }

        fun removeListener(listener: VersionUpdateBroadcastListener) {
            if (listenerList.contains(listener)) {
                listenerList.remove(listener)
            }
        }
    }

    private var mConstants: Constants? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            L.i(TAG, "onReceive: Context or Intent is null. ")
            return
        }
        if (mConstants == null) {
            initConstant(context)
        }
        val bundle = intent.extras
        when (intent.action) {
            mConstants?.VERSION_UPDATE_DOWNLOAD_ACTION -> {
                bundle?.let {
                    val appUrl = it.getString(Constants.VersionUpdate.VERSION_UPDATE_URL, "")
                    val appFilePath =
                        it.getString(Constants.VersionUpdate.VERSION_UPDATE_FILE_PATH, "")
                    val downloadSuccess =
                        it.getBoolean(Constants.VersionUpdate.VERSION_UPDAE_SUCCESS, false)
                    val downloadFailure =
                        it.getBoolean(Constants.VersionUpdate.VERSION_UPDATE_FAILURE, false)
                    val failureMessage =
                        it.getString(Constants.VersionUpdate.VERSION_UPDATE_FAILURE_MESSAGE, "")
                    val fileProgress =
                        it.getLong(Constants.VersionUpdate.VERSION_UPDATE_PROGRESS, 0L)
                    val fileTotal = it.getLong(Constants.VersionUpdate.VERSION_UPDATE_TOTAL, 0L)
                    L.i(TAG, "downloading : $fileProgress - $fileTotal - $appFilePath - $appUrl")
                    if (downloadSuccess || downloadFailure) {
                        if (downloadSuccess) {
                            for (listener in listenerList) {
                                listener.downloadSuccess(appUrl, appFilePath)
                            }
                        } else {
                            for (listener in listenerList) {
                                listener.downloadFailure(failureMessage, appUrl)
                            }
                        }
                    } else {
                        for (listener in listenerList) {
                            listener.onDownload(fileProgress, fileTotal, appFilePath, appUrl)
                        }
                    }
                }
            }
            mConstants?.VERSION_UPDATE_ACTION -> {
                bundle?.let {
                    val appUrl = it.getString(Constants.VersionUpdate.VERSION_UPDATE_URL, "")
                    if (!TextUtils.isEmpty(appUrl)) {
                        for (listener in listenerList) {
                            listener.startDownload()
                        }
                        startDownloadService(context, appUrl)
                    } else {
                        L.w(TAG, "onReceive: version update url is empty. ")
                    }
                }
            }
            mConstants?.VERSION_UPDATE_INSTALL_ACTION -> {
                bundle?.let {
                    val appFilePath =
                        it.getString(Constants.VersionUpdate.VERSION_UPDATE_FILE_PATH, "")
                    if (listenerList.isNotEmpty()) {
                        for (listenter in listenerList) {
                            listenter.installDownloadFile(appFilePath)
                        }
                    } else {
                        installApkFile(context, appFilePath)
                    }
                }
            }
            else -> {

            }
        }

    }

    private fun initConstant(context: Context) {
        try {
            val componentName = ComponentName(context, VersionUpdateBroadcast::class.java)
            val activityInt =
                context.packageManager.getReceiverInfo(componentName, PackageManager.GET_META_DATA)
            val applicationIdStr = activityInt.metaData.getString("applicationId")
            if (!TextUtils.isEmpty(applicationIdStr)) {
                createConstant(applicationIdStr!!)
            }
        } catch (ignore: Exception) {

        }
    }

    private fun createConstant(applicationId: String) {
        if (mConstants == null) {
            mConstants = Constants.getInstant(applicationId)
        }
    }

    /**
     * start download version file
     */
    private fun startDownloadService(context: Context, fileUrl: String) {
        val intent = Intent(context, UpdateVersionService::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.VersionUpdate.VERSION_UPDATE_URL, fileUrl)
        intent.putExtras(bundle)
        context.startService(intent)
    }


    /**
     * install apk file when no listener impl.
     */
    private fun installApkFile(context: Context, filePath: String) {
        try {
            val intent =
                LauncherHelper.installApk(context, mConstants?.applicationId, File(filePath))
            context.startActivity(intent)
        } catch (ignore: Exception) {
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // Open
    ///////////////////////////////////////////////////////////////////////////

    interface VersionUpdateBroadcastListener {
        /**
         * start download new version file
         */
        fun startDownload()

        /**
         * progress
         * @param progress current progress
         * @param total total progress
         * @param filePath install's file path
         * @param fileUrl version url
         */
        fun onDownload(progress: Long, total: Long, filePath: String, fileUrl: String)

        /**
         * download finish and success
         * @param fileUrl download file url
         * @param filePath download file save path
         */
        fun downloadSuccess(fileUrl: String, filePath: String)

        /**
         * download finish and failure:
         * @param message failure cause
         * @param fileUrl download file url
         */
        fun downloadFailure(message: String, fileUrl: String)

        /**
         * install download file
         * @param filePath file path
         */
        fun installDownloadFile(filePath: String)
    }


}