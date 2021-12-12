package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.SplashLayoutBinding
import com.inz.z.note_book.service.NotificationForegroundService
import kotlinx.android.synthetic.main.splash_layout.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/24 10:05.
 */
class SplashActivity : AbsBaseActivity() {

    companion object {
        const val TAG = "SplashActivity"
    }

    private lateinit var splashLayoutBinding: SplashLayoutBinding
    private var isPause = false


    override fun initWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // 刘海屏支持/ 设置刘海屏不显示界面内容
    }

    override fun resetBottomNavigationBar(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.splash_layout
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun setDataBindingView() {
        super.setDataBindingView()
        splashLayoutBinding = SplashLayoutBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }
    }

    override fun getRootContentView(): View? {
        return splash_root_brl
    }

    override fun initView() {
        splashLayoutBinding.splashVersionTv.text = BuildConfig.VERSION_NAME
        splashLayoutBinding.splashTopEndNumTv.text = time.toString()
    }

    override fun initData() {
        setRightTopTimer()
        val service = Intent(mContext, NotificationForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service)
        } else {
            startService(service)
        }
    }

    override fun onResume() {
        super.onResume()
        isPause = false
//        gotoMainActivity()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun needCheckVersion(): Boolean {
        return false
    }

    private var time = 2

    /**
     * 设置右上角倒计时。
     */
    private fun setRightTopTimer() {
        splash_top_end_num_tv.postDelayed({
            if (time == 1) {
                gotoMainActivity()
            } else {
                setRightTopTimer()
                time -= 1
                splashLayoutBinding.splashTopEndNumTv.text = time.toString()
            }
        }, 1000)
    }

    /**
     * 前往主界面
     */
    private fun gotoMainActivity() {

        if (this.isPause) {
            L.i(TAG, "gotoMainActivity: this activity is isPause. ")
            this@SplashActivity.finish()
            return
        }

        // TODO: 2021/4/25 调试文件选择界面，快速打开选择界面
        val intent = Intent(mContext, MainActivity::class.java)
        startActivity(intent)
        finish()

//        ChooseFileActivity.gotoChooseFileActivity(
//            this,
//            4,
//            ChooseFileActivity.MODE_TABLE,
//            com.inz.z.base.entity.Constants.ChooseFileConstants.SHOW_TYPE_IMAGE,
//            2
//        )
////        val intent = Intent(mContext, TestViewActivity::class.java)
////        startActivity(intent)
//        finish()
    }


}