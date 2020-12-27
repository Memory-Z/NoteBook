package com.inz.z.base.view.dialog

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.inz.z.base.R
import com.inz.z.base.broadcast.VersionUpdateBroadcast
import com.inz.z.base.entity.Constants
import com.inz.z.base.entity.UpdateVersionBean
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.util.SPHelper
import com.inz.z.base.view.AbsBaseDialogFragment
import kotlinx.android.synthetic.main.base_update_version.*
import java.io.File

/**
 * 版本更新 弹窗
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/04 14:18.
 */
class UpdateVersionDialog : AbsBaseDialogFragment() {

    companion object {
        private const val TAG = "UpdateVersionDialog"

        /**
         * 默认的
         */
        private const val STATE_DEFAULT = 0x00EA01

        /**
         * 下载中...
         */
        private const val STATE_DOWNLOADING = 0x00EA02

        /**
         * 下载成功
         */
        private const val STATE_DOWNLOAD_SUCCESS = 0x00EA03

        /**
         * 下载失败
         */
        private const val STATE_DOWNLOAD_FAILURE = 0x00EA04

        /**
         * 未知状态
         */
        private const val STATE_UNKNOWN = 0x00EA10


    }

    class Builder(@NonNull val fragmentManager: FragmentManager) {
        private val dialog = UpdateVersionDialog()

        /**
         * 设置更新版本
         */
        fun setUpdateVersion(bean: UpdateVersionBean): Builder {
            dialog.updateVersion = bean
            return this
        }

        fun show() {
            val d = fragmentManager.findFragmentByTag(TAG) as UpdateVersionDialog?
            if (d == null || (!d.isAdded && !d.isVisible)) {
                dialog.show(fragmentManager, TAG)
            }
        }
    }


    @IntDef(
        STATE_DEFAULT,
        STATE_DOWNLOADING,
        STATE_DOWNLOAD_SUCCESS,
        STATE_DOWNLOAD_FAILURE,
        STATE_UNKNOWN
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class VersionState

    private var updateBroadcastListener: UpdateVersionListenerImpl? = null
    private var updateVersion: UpdateVersionBean? = null

    /**
     * 当前状态
     */
    @VersionState
    private var state: Int = STATE_DEFAULT

    /**
     * 安装文件 地址
     */
    private var installFilePath = ""

    private var ignoreVersion = false

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Base_Dialog_Center)
    }

    override fun getLayoutId(): Int {
        return R.layout.base_update_version
    }

    override fun initView() {
        isCancelable = false
        base_update_version_background_tv?.setOnClickListener { this.dismissAllowingStateLoss() }
        base_update_version_later_notification_tv?.setOnClickListener {
            if (ignoreVersion) {
                updateVersion?.let {
                    SPHelper.getInstance().saveIgnoreCurrentVersion(it.versionCode)
                }
                mContext?.let {
                    showToast(it.getString(R.string.you_can_check_new_version_in_setting))
                }
            } else {
                SPHelper.getInstance().saveLaterUpdateVersion(true)
            }
            this.dismissAllowingStateLoss()
        }
        base_update_version_operation_download_tv?.setOnClickListener {
            updateVersion?.let {
                sendDownloadBroadcast(it.url)
            }
        }
        base_update_version_finish_tv?.setOnClickListener {
            when (state) {
                STATE_DOWNLOAD_SUCCESS -> {
                    val intent = LauncherHelper.installApk(
                        mContext,
                        mContext.packageName,
                        File(installFilePath)
                    )
                    intent?.let {
                        mContext?.applicationContext?.startActivity(it)
                    }
                    dismissAllowingStateLoss()
                }
                STATE_DOWNLOAD_FAILURE -> {
                    updateVersion?.let {
                        sendDownloadBroadcast(it.url)
                    }
                }
                else -> {
                    L.i(TAG, "base_update_version_finish_tv > not state $state")
                }
            }
        }


    }

    override fun initData() {
        updateBroadcastListener = UpdateVersionListenerImpl()
        updateBroadcastListener?.let {
            VersionUpdateBroadcast.addListener(it)
        }
        var showUpdateNum = 0
        var content = ""
        var newVersionName = ""
        updateVersion?.let {
            content = it.content.replace("#", "\n")
            newVersionName = getString(R.string.new_version_name_format).format(it.versionName)
            // 更新显示次数
            SPHelper.getInstance().saveShowUpdateVersionNum(it.versionCode)
            showUpdateNum = SPHelper.getInstance().getShowUpdateVersionNum(it.versionCode)
        }

        if (showUpdateNum == Constants.VersionUpdate.MAX_SHOW_UPDATE_NUMBER) {
            base_update_version_later_notification_tv?.text =
                getString(R.string.ignore_current_version)
            ignoreVersion = true
        }
        base_update_version_content_tv?.text = content
        base_update_version_content_name_tv?.text = newVersionName

    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateBroadcastListener?.let {
            VersionUpdateBroadcast.removeListener(it)
        }
        updateBroadcastListener = null
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.apply {
            val size = Point()
            val lp = this.attributes
            this.windowManager.defaultDisplay.getRealSize(size)
            val width = size.x * .8
            lp.width = width.toInt()
            val h = (size.y * .8).toInt()
            val ratoteH = (width / 3 * 5).toInt()
            lp.height = if (h < ratoteH) {
                h
            } else {
                ratoteH
            }
            lp.gravity = Gravity.CENTER
            this.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    /**
     * 发送下载文件 广播
     */
    private fun sendDownloadBroadcast(downloadUrl: String) {
        if (mContext == null) {
            L.i(TAG, "sendDownloadBroadcast: context is null ")
            dismissAllowingStateLoss()
            return
        }
        val intent = Intent()
        intent.setPackage(mContext.packageName)
        intent.setAction(Constants.getInstant(mContext.packageName).VERSION_UPDATE_ACTION)
        val bundle = Bundle()
        bundle.putString(Constants.VersionUpdate.VERSION_UPDATE_URL, downloadUrl)
        intent.putExtras(bundle)
        mContext.sendBroadcast(intent)
    }

    /**
     * 切换底部显示内容
     */
    private fun targetBottomContent(@VersionState state: Int) {
        when (state) {
            STATE_DEFAULT -> {
                base_update_version_operation_ll?.visibility = View.VISIBLE
                base_update_version_progress_ll?.visibility = View.GONE
                base_update_version_finish_ll?.visibility = View.GONE
            }
            STATE_DOWNLOADING -> {
                base_update_version_operation_ll?.visibility = View.GONE
                base_update_version_progress_ll?.visibility = View.VISIBLE
                base_update_version_finish_ll?.visibility = View.GONE
            }
            STATE_DOWNLOAD_FAILURE,
            STATE_DOWNLOAD_SUCCESS -> {
                base_update_version_operation_ll?.visibility = View.GONE
                base_update_version_progress_ll?.visibility = View.GONE
                base_update_version_finish_ll?.visibility = View.VISIBLE
            }
            STATE_UNKNOWN -> {

            }
        }
    }

    /**
     * 设置当前进度
     */
    private fun setDownloadProgress(currentProgress: Long, totalProgress: Long) {
        val currentP = (currentProgress * 100 / totalProgress).toInt()
        base_update_version_progress_bar?.progress = currentP
    }

    /**
     * 版本更新监听 实现
     */
    private inner class UpdateVersionListenerImpl :
        VersionUpdateBroadcast.VersionUpdateBroadcastListener {
        override fun startDownload() {
            state = STATE_DOWNLOADING
            targetBottomContent(state)
        }

        override fun onDownload(progress: Long, total: Long, filePath: String, fileUrl: String) {
            state = STATE_DOWNLOADING
            setDownloadProgress(progress, total)
        }

        override fun downloadSuccess(fileUrl: String, filePath: String) {
            state = STATE_DOWNLOAD_SUCCESS
            mContext?.let {
                base_update_version_finish_tv?.apply {
                    text = it.getString(R.string._install)
                    backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(it, R.color.colorPrimary))
                }
            }
            targetBottomContent(state)
            installFilePath = filePath
        }

        override fun downloadFailure(message: String, fileUrl: String) {
            state = STATE_DOWNLOAD_FAILURE
            mContext?.let {
                base_update_version_finish_tv?.apply {
                    text = it.getString(R.string._retry)
                    backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(it, R.color.danger_color))
                }
            }
            targetBottomContent(state)
        }

        override fun installDownloadFile(filePath: String) {
            installFilePath = filePath
            val intent = LauncherHelper.installApk(mContext, mContext.packageName, File(filePath))
            mContext.startActivity(intent)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

}