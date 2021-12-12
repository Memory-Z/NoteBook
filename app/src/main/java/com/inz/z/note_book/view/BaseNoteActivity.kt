package com.inz.z.note_book.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.view.marginBottom
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.base.BaseLifecycleObserver
import com.inz.z.note_book.view.widget.FullFrameLayout
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import java.lang.Exception
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf

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
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleListenerList = getLifecycleObserver()
        lifecycleListenerList?.forEach {
            lifecycle.addObserver(it)
        }
        super.onCreate(savedInstanceState)
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
        L.d(TAG, "onNightModeChanged: ")
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
            }
            // 开启
            AppCompatDelegate.MODE_NIGHT_YES -> {

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

}