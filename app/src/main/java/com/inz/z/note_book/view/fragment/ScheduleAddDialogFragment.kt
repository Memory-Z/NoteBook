package com.inz.z.note_book.view.fragment

import android.content.Intent
import android.graphics.Point
import android.view.Gravity
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.util.ClockAlarmManager
import com.inz.z.note_book.util.Constants
import kotlinx.android.synthetic.main.content_schedule_add.*
import kotlinx.android.synthetic.main.content_schedule_add.view.*
import kotlinx.android.synthetic.main.dialog_schedule_add.*
import java.util.*

/**
 * 计划添加 弹窗
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/15 11:33.
 */
class ScheduleAddDialogFragment private constructor() : AbsBaseDialogFragment() {
    companion object {
        const val TAG = "ScheduleAddDialogFragment"

        fun getInstant(listener: ScheduleAddDFListener?): ScheduleAddDialogFragment {
            val fragment = ScheduleAddDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }

    var listener: ScheduleAddDFListener? = null
    private var checkTimeHour = 0
    private var checkTimeMinute = 0


    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteBookAppTheme_Dialog)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_schedule_add
    }

    override fun initView() {
        dialog_schedule_add_top_cancel_tv?.setOnClickListener {
            this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
        }
        dialog_schedule_add_top_save_tv?.setOnClickListener {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, checkTimeHour)
            calendar.set(Calendar.MINUTE, checkTimeMinute)
            ClockAlarmManager.setAlarm(mContext, calendar.timeInMillis)
            listener?.save()
            this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
        }
        dialog_schedule_add_content_nsv?.content_time_picker?.setIs24HourView(true)
        dialog_schedule_add_content_nsv?.content_time_picker?.setOnTimeChangedListener(
            object : TimePicker.OnTimeChangedListener {
                override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    checkTimeHour = hourOfDay
                    checkTimeMinute = minute
                    L.i(TAG, "change time  $hourOfDay : $minute ")
                }
            }
        )
    }

    override fun initData() {
        val calendar = Calendar.getInstance(Locale.getDefault())
        checkTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
        checkTimeMinute = calendar.get(Calendar.MINUTE)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val point = Point()
            windowManager.defaultDisplay.getRealSize(point)
            val lp = attributes
            lp.height = (point.y * .6F).toInt()
            lp.gravity = Gravity.BOTTOM
            lp.width = point.x
            attributes = lp
            setBackgroundDrawableResource(android.R.color.transparent)

        }
        isCancelable = true
    }


    interface ScheduleAddDFListener {
        fun save()
    }

}