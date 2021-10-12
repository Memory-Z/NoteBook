package com.inz.z.note_book.view.activity

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivitySettingLayoutBinding
import com.inz.z.note_book.view.BaseNoteActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

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

    private val settingHandler = Handler(Looper.getMainLooper(), SettingHandlerCallback())

    private lateinit var activitySettingLayoutBinding: ActivitySettingLayoutBinding

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting_layout
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun setDataBindingView() {
        super.setDataBindingView()
        activitySettingLayoutBinding = ActivitySettingLayoutBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }
    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)

        setSupportActionBar(activitySettingLayoutBinding.settingInfoTopToolBar)

        activitySettingLayoutBinding.settingInfoCacheClearBnl.setOnClickListener {
            mContext?.let {
                FileUtils.clearCacheData(mContext)
                getCacheSize()
                Toast.makeText(mContext, getString(R.string.cache_cleared), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        activitySettingLayoutBinding.settingInfoVersionNameTv.text = BuildConfig.VERSION_NAME
        activitySettingLayoutBinding.settingInfoVersionBnl.setOnClickListener {
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 返回
            android.R.id.home -> {
                this@SettingActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
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
        activitySettingLayoutBinding.settingInfoCacheClearSizeTv.text = fileSizeStr
    }

    private fun changeStyle() {

    }

    /**
     * 设置 Handler
     */
    private inner class SettingHandlerCallback : Handler.Callback {

        override fun handleMessage(msg: Message): Boolean {
            val bundle = msg.data
            when (msg.what) {
                HANDLER_CACHE_SIZE -> {
                    val cacheSize = bundle?.getString("cacheSize", "") ?: ""

                }
                HANDLER_STYLE -> {

                }
            }
            return true
        }
    }
}