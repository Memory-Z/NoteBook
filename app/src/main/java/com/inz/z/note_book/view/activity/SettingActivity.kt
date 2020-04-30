package com.inz.z.note_book.view.activity

import android.widget.Toast
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.setting_layout.*
import java.io.File

/**
 * 设置界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/17 09:35.
 */
class SettingActivity : AbsBaseActivity() {
    companion object {
        const val TAG = "SettingActivity"
    }

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.setting_layout
    }

    override fun initView() {
        setting_info_nav_left_rl.setOnClickListener {
            this@SettingActivity.finish()
        }
        setting_info_cache_clear_bnl.setOnClickListener {
            mContext?.let {
                FileUtils.clearCacheData(mContext)
                getCacheSize()
                Toast.makeText(mContext, "缓存已清除", Toast.LENGTH_SHORT).show()
            }
        }
        setting_info_version_name_tv.text = BuildConfig.VERSION_NAME
        setting_info_version_bnl.setOnClickListener {
            Toast.makeText(mContext, "检查更新", Toast.LENGTH_SHORT).show()
        }

    }

    override fun initData() {
        getCacheSize()
    }

    private fun getCacheSize() {
        val cachePath = FileUtils.getCachePath(mContext)
        val fileSize = FileUtils.getFileSize(cachePath)
        var fileSizeStr = ""
        if (fileSize > 1024 * 1024) {
            fileSizeStr = String.format("%.2f", fileSize.div(1024F * 1024F)) + "Mb"
        } else {
            fileSizeStr = String.format("%.2f", fileSize.div(1024F)) + "Kb"
        }
        L.i(TAG, "getCacheSize: fileSize = $fileSize. $fileSizeStr")
        setting_info_cache_clear_size_tv.text = fileSizeStr
    }

}