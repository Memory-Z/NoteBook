package com.inz.z.note_book.view.activity

import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.setting_layout.*

/**
 * 设置界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/17 09:35.
 */
class SettingActivity : BaseNoteActivity() {
    companion object {
        const val TAG = "SettingActivity"

        private const val HANDLER_CACHE_SIZE = 0x000A01
        private const val HANDLER_STYLE = 0x000A02
    }

    private val settingHandler = SettingHandler()

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.setting_layout
    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)
        setting_info_nav_left_rl.setOnClickListener {
            this@SettingActivity.finish()
        }
        setting_info_cache_clear_bnl.setOnClickListener {
            mContext?.let {
                FileUtils.clearCacheData(mContext)
                getCacheSize()
                Toast.makeText(mContext, getString(R.string.cache_cleared), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        setting_info_version_name_tv.text = BuildConfig.VERSION_NAME
        setting_info_version_bnl.setOnClickListener {
            // get last version and update.
            getLastVersion()
            Toast.makeText(
                mContext,
                getString(R.string.check_app_version_update),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun initData() {
        getCacheSize()
    }

    override fun showLastVersionToast() {
        super.showLastVersionToast()
        if (mContext != null) {
            Toast.makeText(
                mContext,
                mContext.getString(com.inz.z.base.R.string.current_version_is_laseted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCacheSize() {
        val cachePath = FileUtils.getCachePath(mContext)
        val fileSize = FileUtils.getFileSize(cachePath)
        var fileSizeStr = ""
        if (fileSize > 1024 * 1024) {
            fileSizeStr = getString(R.string.file_size_format_m).format(fileSize.div(1024F * 1024F))
        } else {
            fileSizeStr = getString(R.string.file_size_fromat_k).format(fileSize.div(1024F))
        }
        L.i(TAG, "getCacheSize: fileSize = $fileSize. $fileSizeStr")
        setting_info_cache_clear_size_tv?.text = fileSizeStr
    }

    private fun changeStyle() {

    }

    /**
     * 设置 Handler
     */
    private inner class SettingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            when (msg.what) {
                HANDLER_CACHE_SIZE -> {
                    val cacheSize = bundle?.getString("cacheSize", "") ?: ""

                }
                HANDLER_STYLE -> {

                }
            }
        }
    }
}