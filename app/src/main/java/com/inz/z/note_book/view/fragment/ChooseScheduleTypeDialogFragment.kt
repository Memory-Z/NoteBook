package com.inz.z.note_book.view.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.dialog_choose_schedule_type.*

/**
 * 选择 计划类型弹窗
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/19 13:40.
 */
class ChooseScheduleTypeDialogFragment private constructor() : AbsBaseDialogFragment() {
    companion object {
        const val TAG = "ChooseScheduleTypeDialogFragment"

        fun getInstant(listener: ChooseScheduleTypeDialogListener): ChooseScheduleTypeDialogFragment {
            val fragment = ChooseScheduleTypeDialogFragment()
            fragment.listener = listener
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    var listener: ChooseScheduleTypeDialogListener? = null


    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteBookAppTheme_Dialog_BottomToTop)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_choose_schedule_type
    }

    override fun initView() {
        dialog_choose_schedule_type_cancel_tv?.setOnClickListener(ScheduleTypeDialogViewClickListenerImpl())
        dialog_choose_schedule_type_clock_tv?.setOnClickListener(ScheduleTypeDialogViewClickListenerImpl())
        dialog_choose_schedule_type_hint_tv?.setOnClickListener(ScheduleTypeDialogViewClickListenerImpl())
        dialog_choose_schedule_type_launcher_tv?.setOnClickListener(ScheduleTypeDialogViewClickListenerImpl())
        dialog_choose_schedule_type_none_tv?.setOnClickListener(ScheduleTypeDialogViewClickListenerImpl())
    }

    override fun initData() {

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val lp = attributes
            lp.gravity = Gravity.BOTTOM
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes = lp
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        isCancelable = true
    }

    inner class ScheduleTypeDialogViewClickListenerImpl : View.OnClickListener {
        override fun onClick(v: View?) {
            var type = 0
            when (v?.id) {
                dialog_choose_schedule_type_cancel_tv?.id -> {
                    type = 0
                }
                dialog_choose_schedule_type_hint_tv?.id -> {
                    type = 1
                }
                dialog_choose_schedule_type_launcher_tv?.id -> {
                    type = 2
                }
                dialog_choose_schedule_type_clock_tv?.id -> {
                    type = 3
                }
                dialog_choose_schedule_type_none_tv?.id -> {
                    type = 4
                }
            }
            listener?.chooseType(type)
        }
    }

    interface ChooseScheduleTypeDialogListener {
        fun chooseType(type: Int)
    }
}