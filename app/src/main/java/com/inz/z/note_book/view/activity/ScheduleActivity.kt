package com.inz.z.note_book.view.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.haibin.calendarview.CalendarView
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.controller.ScheduleController
import com.inz.z.note_book.database.controller.TaskScheduleController
import com.inz.z.note_book.util.ClockAlarmManager
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.adapter.ScheduleRvAdapter
import com.inz.z.note_book.view.fragment.ScheduleAddDialogFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.schedule_layout.*
import java.util.*

/**
 * 任务计划
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/14 10:30.
 */
class ScheduleActivity : AbsBaseActivity() {
    companion object {
        private const val TAG = "ScheduleActivity"
    }

    private var scheduleAddDialogFragment: ScheduleAddDialogFragment? = null

    private var scheduleRvAdapter: ScheduleRvAdapter? = null

    private var checkedCalendar: Calendar? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.schedule_layout
    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)
        schedule_top_back_iv?.setOnClickListener {
            this@ScheduleActivity.finish()
        }
        schedule_top_right_add_iv?.setOnClickListener {
//            showAddPopupMenu()
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = schedule_content_calendar_view?.selectedCalendar?.timeInMillis
                ?: calendar.timeInMillis

            showScheduleAddDialog("", calendar.time)
        }

        schedule_content_calendar_view?.setOnYearChangeListener {
            schedule_top_calendar_date_year_tv?.text = it.toString()
        }
        schedule_content_calendar_view?.setOnCalendarSelectListener(
            object : CalendarView.OnCalendarSelectListener {
                override fun onCalendarSelect(
                    calendar: com.haibin.calendarview.Calendar?,
                    isClick: Boolean
                ) {
                    calendar?.let {
                        schedule_top_calendar_date_year_tv?.text = calendar.year.toString()
                        schedule_top_calendar_date_tv?.text =
                            getString(R.string._date_time_format_M_d).format(
                                calendar.month.toString(),
                                calendar.day.toString()
                            )
                        schedule_top_calendar_date_lunar_tv?.text = calendar.lunar

                        val currentCalendar = Calendar.getInstance(Locale.getDefault())
                        currentCalendar.set(Calendar.YEAR, calendar.year)
                        currentCalendar.set(Calendar.MONTH, calendar.month - 1)
                        currentCalendar.set(Calendar.DAY_OF_MONTH, calendar.day)
                        checkedCalendar = currentCalendar
                        changeCheckCalendar(currentCalendar.time)
                    }
                }

                override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {
                    L.i(TAG, "onCalendarOutOfRange: $calendar")
                }
            }
        )
        schedule_top_calendar_date_iv?.setOnClickListener {
            if (schedule_content_calendar_view?.isYearSelectLayoutVisible ?: false) {
                schedule_content_calendar_view?.closeYearSelectLayout()
            } else {
                schedule_content_calendar_view?.showYearSelectLayout(
                    schedule_content_calendar_view?.curYear ?: checkedCalendar?.get(Calendar.YEAR)
                    ?: 1970
                )
            }
        }
        scheduleRvAdapter = ScheduleRvAdapter(mContext)
        scheduleRvAdapter!!.listener = ScheduleRvAdapterListenerImpl()

        schedule_content_rv?.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = scheduleRvAdapter
        }

//        schedule_content_calendar_view?.setOnTouchListener(CalendarViewOnToucherListenerImpl())
    }

    override fun initData() {
        checkedCalendar = Calendar.getInstance(Locale.getDefault())

        schedule_top_calendar_date_year_tv?.text = checkedCalendar!!.get(Calendar.YEAR).toString()
        schedule_top_calendar_date_tv?.text = getString(R.string._date_time_format_M_d).format(
            (checkedCalendar!!.get(Calendar.MONTH) + 1).toString(),
            checkedCalendar!!.get(Calendar.DATE).toString()
        )
        schedule_top_calendar_date_lunar_tv?.text =
            schedule_content_calendar_view?.selectedCalendar?.lunar
    }

    override fun onResume() {
        super.onResume()
        if (checkedCalendar == null) {
            checkedCalendar = Calendar.getInstance(Locale.getDefault())
        }
        changeCheckCalendar(checkedCalendar!!.time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {

            Constants.APPLICATION_LIST_REQUEST_CODE -> {
                val packageInfo = data?.extras?.getParcelable("PackageInfo") as PackageInfo?
                if (packageInfo != null) {
                    this.scheduleAddDialogFragment?.setChockedLauncherApplication(packageInfo)
                }
            }
            Constants.CUSTOM_DATE_REQUEST_CODE -> {
                val checkedWeek = data?.extras?.getIntArray("CheckWeek")
                if (checkedWeek != null) {
                    this.scheduleAddDialogFragment?.setCustomDate(checkedWeek)
                }
            }
        }
    }

    /**
     * 切换选中日期
     */
    private fun changeCheckCalendar(date: Date) {
        Observable
            .create(ObservableOnSubscribe<MutableList<TaskSchedule>> {
                val data = TaskScheduleController.findTaskScheduleByDate(date)
                it.onNext(data)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefaultObserver<MutableList<TaskSchedule>>() {

                override fun onComplete() {

                }

                override fun onNext(t: MutableList<TaskSchedule>) {
                    L.i(TAG, "changeCheckCalendar: $t ")
                    scheduleRvAdapter?.refreshData(t)
                }

                override fun onError(e: Throwable) {
                    L.e(TAG, "onError: ", e)
                }
            })
    }

    /**
     * ScheduleRcAdapter listener implementations.
     */
    private inner class ScheduleRvAdapterListenerImpl :
        ScheduleRvAdapter.ScheduleRvAdapterListener {
        override fun itemClick(v: View?, position: Int) {
            val taskSchedule = this@ScheduleActivity.scheduleRvAdapter?.list?.get(position)
            if (taskSchedule != null) {
                showScheduleAddDialog(
                    taskSchedule.taskScheduleId,
                    checkedCalendar?.time ?: Calendar.getInstance(Locale.getDefault()).time
                )
            }
        }

        override fun itemCheckedChanged(
            buttonView: CompoundButton?,
            isChecked: Boolean,
            position: Int
        ) {

        }
    }

    /**
     * 显示添加弹窗
     */
    private fun showAddPopupMenu() {
        val popupMenu = PopupMenu(mContext, schedule_top_right_ll)
        popupMenu.menuInflater.inflate(R.menu.menu_schedule_add, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_sc_add_launcher -> {
//                    showScheduleAddDialog()
                }
                R.id.menu_sc_add_help -> {

                }
                else -> {

                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }


    /**
     * 显示添加弹窗
     * @param taskScheduleId 任务计划ID
     * @param checkedDate 选中日期
     */
    private fun showScheduleAddDialog(taskScheduleId: String, checkedDate: Date) {
        L.i(TAG, "showScheduleAddDialog: ")
        if (mContext == null) {
            L.w(TAG, "showScheduleAddDialog: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var scheduleAddDialogFragment =
            manager.findFragmentByTag("ScheduleAddDialogFragment") as ScheduleAddDialogFragment?
        if (scheduleAddDialogFragment == null) {
            scheduleAddDialogFragment =
                ScheduleAddDialogFragment.getInstant(
                    taskScheduleId,
                    checkedDate,
                    ScheduleAddDialogFragmentListenerImpl()
                )
            this.scheduleAddDialogFragment = scheduleAddDialogFragment
        }
        if (!scheduleAddDialogFragment.isAdded && !scheduleAddDialogFragment.isVisible) {
            scheduleAddDialogFragment.show(manager, "ScheduleAddDialogFragment")
        }
    }


    /**
     * 计划添加弹窗接口实现
     */
    private inner class ScheduleAddDialogFragmentListenerImpl :
        ScheduleAddDialogFragment.ScheduleAddDFListener {
        override fun save(taskInfo: TaskInfo?, taskSchedule: TaskSchedule?) {
            L.i(
                TAG,
                "save: taskInfo = ${taskInfo.toString()} , taskSchedule = ${taskSchedule.toString()}"
            )
            if (taskInfo != null) {
                if (taskSchedule == null) {
                    ScheduleController.insertScheduleTask(taskInfo)
                } else {
                    ScheduleController.insertScheduleTask(taskInfo, taskSchedule)
                }
            }
            // 更新广播
            ClockAlarmManager.setAlarm(mContext, System.currentTimeMillis())
            changeCheckCalendar(
                checkedCalendar?.time ?: Calendar.getInstance(Locale.getDefault()).time
            )
        }

        override fun setRepeatDate(checkedDateArray: IntArray) {
            L.i(TAG, "setRepeatDate: ")
            val intent = Intent(mContext, CustomRepeatDateActitity::class.java)
            val bundle = Bundle()
            bundle.putIntArray("RepeatDate", checkedDateArray)
            intent.putExtras(bundle)
            startActivityForResult(intent, Constants.CUSTOM_DATE_REQUEST_CODE)
        }

        override fun chooseLauncherApplication() {
            L.i(TAG, "chooseLauncherApplication: ")
            val intent = Intent(mContext, ApplicationListActivity::class.java)
            intent.putExtra(
                Constants.APPLICATION_LIST_REQUEST_CODE_FLAG,
                Constants.APPLICATION_LIST_REQUEST_CODE
            )
            startActivityForResult(intent, Constants.APPLICATION_LIST_REQUEST_CODE)
        }

        override fun chooseScheduleType() {
            L.i(TAG, "chooseScheduleType: ")


        }
    }
}