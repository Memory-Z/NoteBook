package com.inz.z.note_book.view.dialog

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.view.fragment.BaseDialogFragment

/**
 * 锁屏界面
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/12 13:53.
 */
class LockScreenDialogFragment : AbsBaseDialogFragment() {

    companion object {
        private const val TAG = "LockScreenDialogFragment"

        fun getInstant(): LockScreenDialogFragment {
            val fragment = LockScreenDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }

        /**
         * 显示锁屏弹窗
         */
        fun showLockScreenDialog(activity: AppCompatActivity?) {
            if (activity == null) {
                return
            }
            val manager = activity.supportFragmentManager
            var fragment =
                manager.findFragmentByTag("LockScreenDialog") as LockScreenDialogFragment?
            if (fragment == null) {
                fragment = LockScreenDialogFragment.getInstant()
            }
            if (!fragment.isAdded || !fragment.isVisible) {
                fragment.show(manager, "LockScreenDialog")
            }
        }

        /**
         * 隐藏锁屏弹窗
         */
        fun hideLockScreenDialog(activity: AppCompatActivity?) {
            if (activity == null) {
                return
            }
            val manager = activity.supportFragmentManager
            val fragment =
                manager.findFragmentByTag("LockScreenDialog") as LockScreenDialogFragment?
            fragment?.dismissAllowingStateLoss()
        }
    }

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoteBookAppTheme_Dialog)
    }

    override fun getLayoutId(): Int = R.layout.dialog_lock_screen

    override fun initView() {

    }

    override fun initData() {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val size = Point()
            this.windowManager.defaultDisplay.getRealSize(size)
            val lp = this.attributes
            lp.apply {
                width = size.x
                height = size.y
                gravity = Gravity.CENTER
            }
            attributes = lp
            this.setBackgroundDrawableResource(R.color.card_inverse_thread_color)
        }
    }
}