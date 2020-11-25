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

    override fun resetBottomNavigationBar(): Boolean {
        return true
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

}