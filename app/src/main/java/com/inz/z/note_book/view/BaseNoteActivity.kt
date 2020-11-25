package com.inz.z.note_book.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.view.marginBottom
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.view.widget.FullFrameLayout
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

    override fun initWindow() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val haveNavigation = checkNavigation(mContext)
        L.i(TAG, "onResume: $haveNavigation")
        if (haveNavigation) {
            val navigationBarHeight = getNavigationBarHeight(this)
            L.i(TAG, "onResume:  $navigationBarHeight")
            val contentView: ContentFrameLayout? = findViewById(android.R.id.content)
            contentView?.apply {
                this.setPadding(
                    paddingLeft,
                    paddingTop,
                    paddingRight,
                    paddingBottom + navigationBarHeight
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        addClockView()
    }

    override fun onPause() {
        super.onPause()
//        removeClockView()
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


    /* -------------------- 底部状态栏 ------------------------ */

    /**
     * 检测是否存在底部栏
     */
    private fun checkNavigation(context: Context): Boolean {
        var haveNavigationBar = false
        val barId = context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if (barId > 0) {
            haveNavigationBar = context.resources.getBoolean(barId)
        }
        try {
            val className = Class.forName("android.os.SystemProperties")
            val method = className.getMethod("get", String::class.java)
            val navBarOverride = method.invoke(className, "qemu.hw.mainkeys")
            if ("1".equals(navBarOverride)) {
                // 不存在 虚拟按键
                haveNavigationBar = false
            } else if ("0".equals(navBarOverride)) {
                // 存在 虚拟按键
                haveNavigationBar = true
//                val navigationBarHeight = getNavigationBarHeight(this)
//                L.i(TAG, "checkNavigate:  $navigationBarHeight")
            }
        } catch (ignore: Exception) {
        }
        return haveNavigationBar
    }

    /**
     * 获取底部导航栏高度
     */
    protected fun getNavigationBarHeight(activity: Activity): Int {
        val resId = activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return activity.resources.getDimensionPixelSize(resId)
    }
    /* -------------------- 底部状态栏 ------------------------ */
}