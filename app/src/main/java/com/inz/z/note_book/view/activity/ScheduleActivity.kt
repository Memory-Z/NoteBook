package com.inz.z.note_book.view.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.haibin.calendarview.CalendarView
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.base.FragmentContentTypeValue
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.controller.TaskScheduleController
import com.inz.z.note_book.databinding.ActivityScheduleLayoutBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.adapter.ScheduleRvAdapter
import com.inz.z.note_book.view.dialog.ScheduleAddDialogFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 任务计划
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/14 10:30.
 */
class ScheduleActivity : BaseNoteActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "ScheduleActivity"
    }

    private var activityScheduleLayoutBinding: ActivityScheduleLayoutBinding? = null

    /**
     * 添加计划弹窗
     */
    private var scheduleAddDialogFragment: ScheduleAddDialogFragment? = null

    private var scheduleRvAdapter: ScheduleRvAdapter? = null

    /**
     * 选中日期： 默认设置当前日期为选中日期
     */
    private var checkedCalendar: Calendar? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_schedule_layout
    }

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun setViewBinding() {
        super.setViewBinding()
        activityScheduleLayoutBinding = ActivityScheduleLayoutBinding.inflate(layoutInflater)
        setContentView(activityScheduleLayoutBinding?.root)
    }

    override fun initView() {
        // 设置状态栏 为 白日模式
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        // 设置状态栏 颜色
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)

        // 设置 Toolbar 状态栏
        setSupportActionBar(activityScheduleLayoutBinding?.scheduleTopToolBar)
        // 年份切换 监听
        activityScheduleLayoutBinding?.scheduleContentCalendarView?.setOnYearChangeListener {
            activityScheduleLayoutBinding?.scheduleTopCalendarDateYearTv?.text = it.toString()
        }
        // 日期 选中监听
        activityScheduleLayoutBinding?.scheduleContentCalendarView?.setOnCalendarSelectListener(
            object : CalendarView.OnCalendarSelectListener {
                override fun onCalendarSelect(
                    calendar: com.haibin.calendarview.Calendar?,
                    isClick: Boolean
                ) {
                    calendar?.let {
                        // 设置顶部显示 年份
                        activityScheduleLayoutBinding?.scheduleTopCalendarDateYearTv?.text =
                            calendar.year.toString()
                        // 设置顶部显示 日期
                        activityScheduleLayoutBinding?.scheduleTopCalendarDateTv?.text =
                            getDateStr(
                                calendar.month.toString(),
                                calendar.day.toString()
                            )
                        // 设置顶部显示 农历
                        activityScheduleLayoutBinding?.scheduleTopCalendarDateLunarTv?.text =
                            calendar.lunar

                        // 记录 当前选中日期
                        val currentCalendar = Calendar.getInstance(Locale.getDefault())
                        currentCalendar.set(Calendar.YEAR, calendar.year)
                        currentCalendar.set(Calendar.MONTH, calendar.month - 1)
                        currentCalendar.set(Calendar.DAY_OF_MONTH, calendar.day)
                        checkedCalendar = currentCalendar
                        // 跟新底部显示数据
                        updateScheduleWhenChangeCheckCalendar(currentCalendar.time)
                    }
                }

                override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {
                    L.i(TAG, "onCalendarOutOfRange: $calendar")
                }
            }
        )
        // 日期按钮 点击
        activityScheduleLayoutBinding?.scheduleTopCalendarDateIv?.setOnClickListener(this)
        // 设置底部显示内容
        scheduleRvAdapter = ScheduleRvAdapter(mContext)
            .apply {
                // 监听实现
                listener = ScheduleRvAdapterListenerImpl()
            }
        // 设置 RecyclerView 适配器
        activityScheduleLayoutBinding?.scheduleContentRv?.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = scheduleRvAdapter
        }

        // 设置 浮动按钮点击事件
        activityScheduleLayoutBinding?.scheduleAddFab?.setOnClickListener(this)

//        schedule_content_calendar_view?.setOnTouchListener(CalendarViewOnToucherListenerImpl())
    }

    override fun initData() {
        // 默认设置当前日期为选中日期
        checkedCalendar = Calendar.getInstance(Locale.getDefault())

        // 设置默认显示内容
        activityScheduleLayoutBinding?.let { binding ->
            // 显示年份
            binding.scheduleTopCalendarDateYearTv.text =
                checkedCalendar!!.get(Calendar.YEAR).toString()
            // 显示 日期
            binding.scheduleTopCalendarDateTv.text = getCurrentShowDateStr()
            // 显示农历时间
            binding.scheduleTopCalendarDateLunarTv.text =
                binding.scheduleContentCalendarView.selectedCalendar.lunar
        }
    }

    override fun onResume() {
        super.onResume()
        // 如果 当前无 选中 日期，按现在日期作为 选中日期
        if (checkedCalendar == null) {
            checkedCalendar = Calendar.getInstance(Locale.getDefault())
        }
        // 更新 schedule 数据
        updateScheduleWhenChangeCheckCalendar(checkedCalendar!!.time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            // 应用列表请求返回
            Constants.APPLICATION_LIST_REQUEST_CODE -> {
                val packageInfo = data?.extras?.getParcelable("PackageInfo") as PackageInfo?
                if (packageInfo != null) {
                    this.scheduleAddDialogFragment?.setChockedLauncherApplication(packageInfo)
                }
            }
            // 请求 选择 重复类型。
            Constants.TaskParams.RESULT_REPEAT_TYPE_CODE -> {
                data?.let {
                    it.extras?.let { bundle ->
                        val repeatType = bundle.getInt(Constants.TaskParams.PARAMS_REPEAT_TYPE, -1)
                        val repeatDate = bundle.getIntArray(Constants.TaskParams.PARAMS_REPEAT_DATE)
                        // 如果 repeatType == -1 ，未 获取到相关内容, 不进行更新。
                        if (repeatType != -1) {
                            this.scheduleAddDialogFragment?.updateRepeatType(repeatType, repeatDate)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_schedule_add, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 返回按钮
            android.R.id.home -> {
                this@ScheduleActivity.finish()
            }
            // 点击添加按钮
            R.id.menu_sc_add_item -> {
                // 获取当前时间
                val calendar = Calendar.getInstance(Locale.getDefault())
                calendar.timeInMillis =
                    activityScheduleLayoutBinding?.scheduleContentCalendarView?.selectedCalendar?.timeInMillis
                        ?: calendar.timeInMillis
                // 显示 添加计划弹窗
                showScheduleAddDialog("", calendar.time)
            }
            // 点击 帮助按钮
            R.id.menu_sc_add_help_item -> {
                // TODO: 2021/10/10 get help
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        // 判断 是否为快速 点击
        if (ClickUtil.isFastClick(v)) return
        activityScheduleLayoutBinding?.let { binding ->
            when (v?.id) {
                // 顶部切换年份/月份按钮 点击
                binding.scheduleTopCalendarDateIv.id -> {
                    // 年份选择是否开启并显示
                    val isYearShow =
                        binding.scheduleContentCalendarView.isYearSelectLayoutVisible
                    L.d(TAG, "onClick: 【scheduleTopCalendarDateIv】 isYearShow = $isYearShow")
                    // 如果 以显示 年份选择，关闭；否则开启
                    if (isYearShow) {
                        binding.scheduleContentCalendarView.closeYearSelectLayout()
                    } else {
                        // 设置当前选中年份
                        binding.scheduleContentCalendarView.showYearSelectLayout(
                            binding.scheduleContentCalendarView.curYear
                        )
                    }
                }
                // 浮动 按钮 点击 事件
                binding.scheduleAddFab.id -> {
                    // 获取当前时间
                    val calendar = Calendar.getInstance(Locale.getDefault())
                    calendar.timeInMillis =
                        activityScheduleLayoutBinding?.scheduleContentCalendarView?.selectedCalendar?.timeInMillis
                            ?: calendar.timeInMillis
                    L.d(TAG,
                        "onClick: 【scheduleAddFab】 calender time = ${
                            BaseTools.getDateFormatTime().format(calendar.time)
                        }")
                    // 显示 添加计划弹窗
                    showScheduleAddDialog("", calendar.time)
                }
                else -> {

                }
            }
        }
    }

    /**
     * 获取当前 需要显示的时间
     * @return String
     */
    private fun getCurrentShowDateStr() =
        getDateStr(
            (checkedCalendar!!.get(Calendar.MONTH) + 1).toString(),
            checkedCalendar!!.get(Calendar.DATE).toString()
        )

    /**
     * 获取显示时间串
     * @param monthStr 月份
     * @param dayStr 日期
     */
    private fun getDateStr(monthStr: String, dayStr: String): String =
        getString(R.string._date_time_format_M_d).format(monthStr, dayStr)

    /**
     * 切换 底部浮动按钮显示状态
     * @param show 是否显示
     */
    private fun targetFloatButton(show: Boolean) {
        activityScheduleLayoutBinding?.scheduleAddFab?.apply {
            if (show) {
                show()
            } else {
                hide()
            }
        }
    }

    /**
     * 切换选中日期，并更具新 日期信息获取对应的计划内容
     * @param date 日期
     */
    private fun updateScheduleWhenChangeCheckCalendar(date: Date) {
        Observable
            .create(ObservableOnSubscribe<MutableList<TaskSchedule>> {
                // 根据 日期 获取对应的数据信息
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
            // 获取点击项 数据信息
            val taskSchedule = this@ScheduleActivity.scheduleRvAdapter?.list?.get(position)
            if (taskSchedule != null) {
                // 显示添加 弹窗
                showScheduleAddDialog(
                    taskSchedule.taskScheduleId,
                    checkedCalendar?.time ?: Calendar.getInstance(Locale.getDefault()).time
                )
            }
        }

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
        L.i(TAG,
            "showScheduleAddDialog: ---> ${scheduleAddDialogFragment.isAdded} -- ${scheduleAddDialogFragment.isVisible}")
        if (!scheduleAddDialogFragment.isAdded && !scheduleAddDialogFragment.isVisible) {
            scheduleAddDialogFragment.show(manager, "ScheduleAddDialogFragment")
        }
    }


    /**
     * 计划添加弹窗接口实现
     * @see ScheduleAddDialogFragment.ScheduleAddDFListener
     */
    private inner class ScheduleAddDialogFragmentListenerImpl :
        ScheduleAddDialogFragment.ScheduleAddDFListener {

        override fun savedTaskSchedule(taskScheduleId: String) {
            L.i(TAG, "savedTaskSchedule: taskScheduleId = $taskScheduleId")
        }

        override fun setRepeatDate(repeatType: Int, checkedDateArray: IntArray) {
            L.i(TAG, "setRepeatDate: --> repeatType = $repeatType ")
            // 弹出选择重复类型弹窗。
            RepeatTypeActivity.startChooseRepeatTypeActivityForResult(
                this@ScheduleActivity,
                Constants.TaskParams.REQUEST_REPEAT_TYPE_CODE,
                repeatType,
                checkedDateArray
            )
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

        override fun chooseScheduleTag(scheduleId: String) {
            L.i(TAG, "chooseScheduleTag: scheduleId = $scheduleId")
            // 跳转 至  选择内容界面
            AddContentActivity.startActivityForResult(
                this@ScheduleActivity,
                FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SCHEDULE_TAG,
                scheduleId,
                Constants.TaskParams.REQUEST_TAG_CODE
            )

        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // TEST
    ///////////////////////////////////////////////////////////////////////////

}