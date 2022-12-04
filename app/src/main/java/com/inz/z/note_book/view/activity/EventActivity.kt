package com.inz.z.note_book.view.activity

import android.view.MenuItem
import android.view.View
import androidx.annotation.NonNull
import com.haibin.calendarview.CalendarView
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityDayEventBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.dialog.EventDayAddDialogFragment
import java.util.*

/**
 * 事件记录。
 * # 记录每日发生事件，
 * # 事件是否需要进行显示发生后日期
 * # 是否需要进行提示
 *
 * ====================================================
 * Create by 11654 in 2022/6/9 10:26
 */
class EventActivity : BaseNoteActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "EventActivity"
    }

    private var binding: ActivityDayEventBinding? = null

    override fun useViewBinding(): Boolean = true

    override fun initWindow() {
    }

    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityDayEventBinding.inflate(layoutInflater)
            .apply { setContentView(root) }
    }

    override fun getLayoutId(): Int = R.layout.activity_day_event

    private var currentCalendar: Calendar? = null

    override fun initView() {
        binding?.let {
            setSupportActionBar(it.toolbarDayEventTop)
            it.fabDayEventAddEvent.setOnClickListener(this)
            it.calendarViewDayEvent.apply {
                this.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {

                    override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {
                    }

                    override fun onCalendarSelect(
                        calendar: com.haibin.calendarview.Calendar?,
                        isClick: Boolean
                    ) {
                        currentCalendar?.apply {
                            calendar?.let { c ->
                                this.set(Calendar.DAY_OF_YEAR, c.day)
                                this.set(Calendar.MONTH, c.month - 1)
                                this.set(Calendar.YEAR, c.year)
                            }
                        }
                    }
                })
            }
        }
    }

    override fun initData() {
        currentCalendar = Calendar.getInstance(Locale.getDefault())
        L.i(TAG, "initData: current Calendar $currentCalendar")
    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // back
            finish()
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.w(TAG, "onClick: this is fast click. ")
            return
        }
        binding?.let { bind ->
            when (v?.id) {
                bind.fabDayEventAddEvent.id -> {
                    // TODO: 2022/8/24 show Add Day Event Dialog .
                    currentCalendar?.let { calendar ->
                        showAddDayEventDialog(calendar)
                    }
                }

                else -> {

                }
            }
        }
    }

    /**
     * 显示 添加  日 事件 弹窗
     */
    private fun showAddDayEventDialog(calendar: Calendar) {
        val tag = Constants.FragmentParams.EVENT_DAY_DIALOG_TAG
        val manager = supportFragmentManager
        var fragment = manager.findFragmentByTag(tag) as EventDayAddDialogFragment?
        if (fragment == null) {
            fragment = EventDayAddDialogFragment.getInstance(
                calendar,
                object : EventDayAddDialogFragment.EventDayAddFmListener {
                    override fun onSaveEvent() {
                        TODO("Not yet implemented")
                    }
                }
            )
        }
        if (!fragment.isAdded || !fragment.isVisible) {
            fragment.show(manager, tag)
        }
    }

    private fun hideAddDayEventDialog() {
        val tag = Constants.FragmentParams.EVENT_DAY_DIALOG_TAG
        val manager = supportFragmentManager
        val fragment = manager.findFragmentByTag(tag) as EventDayAddDialogFragment?
        fragment?.dismissAllowingStateLoss()
    }
}