package com.inz.z.note_book.view

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.BuildConfig
import com.inz.z.note_book.R
import com.inz.z.note_book.base.BaseLifecycleObserver
import com.inz.z.note_book.util.ViewUtil
import com.inz.z.note_book.view.widget.FullFrameLayout
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/16 17:59.
 */
abstract class BaseNoteActivity : AbsBaseActivity() {
    companion object {
        const val TAG = "BaseNoteActivity"
    }

    protected var lockView: View? = null

    /**
     * 生命 周期 监听 列表
     */
    var lifecycleListenerList: List<BaseLifecycleObserver>? = null

    /**
     * 获取生命周期 监听
     * @return T 类型
     */
    open fun getLifecycleObserver(): List<BaseLifecycleObserver> {
        return emptyList()
    }

    override fun resetBottomNavigationBar(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleListenerList = getLifecycleObserver()
        lifecycleListenerList?.forEach {
            lifecycle.addObserver(it)
        }
        setNightMode(ViewUtil.getIsNightMode(this))
        super.onCreate(savedInstanceState)
        initRootViewBackground()
    }

    private fun initRootViewBackground() {
        val rootView: View? = findViewById(android.R.id.content)
        rootView?.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.cardBackgroundColor,
                null
            )
        )
    }

    override fun onResume() {
        super.onResume()
//        addClockView()
    }

    override fun onPause() {
        super.onPause()
//        removeClockView()
    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        lifecycleListenerList = null
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        L.d(TAG, "onNightModeChanged: mode =  $mode")
        when (mode) {
            // 不开启
            AppCompatDelegate.MODE_NIGHT_NO -> {

            }
            // 跟随系统
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                val isNightMode =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        resources.configuration.isNightModeActive
                    } else {
                        resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                    }
                L.d(TAG, "onNightModeChanged: isNightMode = $isNightMode")
                setNightMode(ViewUtil.getIsNightMode(this))
            }
            // 开启
            AppCompatDelegate.MODE_NIGHT_YES -> {
                setNightMode(ViewUtil.getIsNightMode(this))
            }
            // 跟随电量
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> {

            }
            // 未指定
            AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> {

            }
            else -> {

            }
        }
    }

    private fun setNightMode(nightMode: Boolean) {
        L.i(TAG, "setNightMode: night = $nightMode")
        if (nightMode) {
            // 设置状态栏 为 白日模式
            QMUIStatusBarHelper.setStatusBarDarkMode(this)
            // 设置状态栏 颜色
        } else {
            // 设置状态栏 为 白日模式
            QMUIStatusBarHelper.setStatusBarLightMode(this)
//                    window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)
        }
        // 设置状态栏 颜色
        window.statusBarColor = ContextCompat.getColor(this, R.color.cardColor)
    }

    private fun addClockView() {
        if (lockView == null) {
            lockView = initClockView()
        }
        window.addContentView(
            lockView,
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun removeClockView() {
        if (lockView != null) {
            (lockView!!.parent as ViewGroup)
                .removeView(lockView)
            lockView = null
        }
    }

    private fun initClockView(): View {
//        return ImageView(mContext).apply {
//            setImageResource(R.drawable.ic_vd_image)
//        }
        val fullFrameLayout = FullFrameLayout(mContext)
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        fullFrameLayout.addContentView(R.layout.dialog_lock_screen, lp)
        return fullFrameLayout
    }


    override fun getCurrentVersionCode(): Int {
        return BuildConfig.VERSION_CODE
    }
}