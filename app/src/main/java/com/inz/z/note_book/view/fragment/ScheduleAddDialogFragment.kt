package com.inz.z.note_book.view.fragment

import android.content.pm.PackageInfo
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.bean.inside.ScheduleWeekDate
import com.inz.z.note_book.bean.inside.TaskAction
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.controller.TaskInfoController
import com.inz.z.note_book.database.controller.TaskScheduleController
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

        fun getInstant(
            taskScheduleId: String,
            checkedDate: Date,
            listener: ScheduleAddDFListener?
        ): ScheduleAddDialogFragment {
            val fragment = ScheduleAddDialogFragment()
            fragment.listener = listener
            val bundle = Bundle()
            bundle.putSerializable("checkedDate", checkedDate)
            bundle.putString("TaskScheduleId", taskScheduleId)
            fragment.arguments = bundle
            return fragment
        }
    }

    var listener: ScheduleAddDFListener? = null
    private var checkTimeHour = 0
    private var checkTimeMinute = 0

    /**
     * 选中需要运行的程序
     */
    private var checkedPackageInfo: PackageInfo? = null
    private var checkedPackageName: String = ""

    /**
     * 选中的重复日期
     */
    private var checkedRepeatDate: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)

    /**
     * 计划描述
     */
    private var schedule: TaskSchedule? = null
    private var taskInfo: TaskInfo? = null

    private var scheduleTaskAction: TaskAction = TaskAction.NONE

    private var taskScheduleId = ""
    private var checkedDate: Date? = null


    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteBookAppTheme_Dialog_BottomToTop)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_schedule_add
    }

    override fun initView() {
        dialog_schedule_add_top_cancel_tv?.setOnClickListener { view ->
            this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
        }
        dialog_schedule_add_top_save_tv?.setOnClickListener { view ->
            saveTaskSchedule()
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
        dialog_schedule_add_content_repeat_date_bnl?.setOnClickListener { view ->
            listener?.setRepeatDate(checkedRepeatDate)
        }
        dialog_schedule_add_content_action_bnl?.setOnClickListener { view ->
            listener?.chooseLauncherApplication()
        }
        dialog_schedule_add_content_repeat_switch?.setOnCheckedChangeListener { buttonView, isChecked ->
            dialog_schedule_add_content_repeat_date_bnl?.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        dialog_schedule_add_content_action_type_bnl?.setOnClickListener {
            showChooseScheduleTypeDialogFragment()
        }

        dialog_schedule_add_content_repeat_date_bnl?.visibility = View.GONE
    }

    override fun initData() {
        val calendar = Calendar.getInstance(Locale.getDefault())
        var checkTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
        var checkTimeMinute = calendar.get(Calendar.MINUTE)

        val bundle = arguments
        bundle?.let {
            taskScheduleId = it.getString("TaskScheduleId", "")
            if (taskScheduleId.isNotEmpty()) {
                schedule = TaskScheduleController.findTaskScheduleById(taskScheduleId)
                val taskId = schedule?.taskId ?: ""
                if (taskId.isNotEmpty()) {
                    taskInfo = TaskInfoController.queryTaskInfoById(taskId)
                }
            }
            checkedDate = it.getSerializable("checkedDate") as Date?
        }
        schedule?.let {
            val scheduleDate = it.scheduleTime
            val minuteStr = BaseTools.getDateFormat("mm", Locale.getDefault()).format(scheduleDate)
            checkTimeMinute = minuteStr.toInt()
            val hourStr = BaseTools.getDateFormat("HH", Locale.getDefault()).format(scheduleDate)
            checkTimeHour = hourStr.toInt()
            dialog_schedule_add_content_nsv?.content_time_picker?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = checkTimeHour
                    minute = checkTimeMinute
                } else {
                    currentHour = checkTimeHour
                    currentMinute = checkTimeMinute
                }
            }
            val repeatDateList = it.scheduleRepeatDateList
            if (repeatDateList.isNotEmpty()) {
                for (d in repeatDateList) {
                    when (d) {
                        ScheduleWeekDate.MONDAY -> {
                            checkedRepeatDate[0] = 1
                        }
                        ScheduleWeekDate.TUESDAY -> {
                            checkedRepeatDate[1] = 1
                        }
                        ScheduleWeekDate.WEDNESDAY -> {
                            checkedRepeatDate[2] = 1
                        }
                        ScheduleWeekDate.THURSDAY -> {
                            checkedRepeatDate[3] = 1
                        }
                        ScheduleWeekDate.FRIDAY -> {
                            checkedRepeatDate[4] = 1
                        }
                        ScheduleWeekDate.SATURDAY -> {
                            checkedRepeatDate[5] = 1
                        }
                        ScheduleWeekDate.SUNDAY -> {
                            checkedRepeatDate[6] = 1
                        }
                        else -> {

                        }
                    }
                }
            }
            val isRepeat = it.scheduleRepeat
            dialog_schedule_add_content_repeat_switch?.isChecked = isRepeat
            val scheduleTag = it.scheduleTag
            dialog_schedule_add_content_tag_et?.setText(scheduleTag)
            dialog_schedule_add_content_repeat_date_bnl?.visibility =
                if (isRepeat) View.VISIBLE else View.GONE

        }
        if (schedule == null) {
            this.checkTimeHour = checkTimeHour
            this.checkTimeMinute = checkTimeMinute
        }
        taskInfo?.let {
            checkedPackageName = it.taskPackageName
            scheduleTaskAction = getTaskAction(it.taskAction)

        }
        dialog_schedule_add_content_action_type_tv?.text =
            getTaskActionString(scheduleTaskAction.value)
        dialog_schedule_add_content_action_bnl?.visibility =
            if (TaskAction.LAUNCHER.equals(scheduleTaskAction)) View.VISIBLE else View.GONE
        if (checkedPackageName.isNotEmpty()) {
            checkedPackageInfo =
                LauncherHelper.findApplicationInfoByPackageName(mContext, checkedPackageName)
        }

        if (checkedPackageInfo != null) {
            setChockedLauncherApplication(checkedPackageInfo!!)
        }
        setCustomDate(checkedRepeatDate)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val point = Point()
            windowManager.defaultDisplay.getRealSize(point)
            val lp = attributes
            lp.height = (point.y * .67F).toInt()
            lp.gravity = Gravity.BOTTOM
            lp.width = point.x
            attributes = lp
            setBackgroundDrawableResource(android.R.color.transparent)

        }
        isCancelable = true
    }

    /**
     * 加载选中的程序
     * @param packageName 包名
     */
    private fun loadLauncherApplication(packageName: String) {
        checkedPackageInfo = LauncherHelper.findApplicationInfoByPackageName(mContext, packageName)
        checkedPackageName = checkedPackageInfo?.packageName ?: ""
    }

    /**
     * 获取任务类型
     */
    private fun getTaskAction(taskActionStr: String): TaskAction {
        return TaskAction.NONE.getTaskAction(taskActionStr)
    }

    /**
     * 获取任务名称
     */
    private fun getTaskActionString(taskAction: String): String {
        val name: String
        val action = getTaskAction(taskAction)
        when (action) {
            TaskAction.HINT -> {
                name = getString(R.string._tips)
            }
            TaskAction.CLOCK -> {
                name = getString(R.string._clock)
            }
            TaskAction.LAUNCHER -> {
                name = getString(R.string._launcher)
            }
            else -> {
                name = getString(R.string._nothing)
            }
        }
        return name
    }

    /**
     *
     * 保存 计划任务
     */
    private fun saveTaskSchedule() {
        val calendar = Calendar.getInstance(Locale.getDefault())
        if (taskInfo == null) {
            taskInfo = TaskInfo()
            taskInfo!!.apply {
                taskId = BaseTools.getUUID()
                createTime = calendar.time
                updateTime = calendar.time
            }
        }

        taskInfo?.apply {
            taskAction = this@ScheduleAddDialogFragment.scheduleTaskAction.value
            taskPackageName = checkedPackageName
            updateTime = calendar.time
        }
        if (schedule == null) {
            schedule = TaskSchedule()
            schedule!!.apply {
                taskScheduleId = BaseTools.getUUID()
                createTime = calendar.time
                updateTime = calendar.time
            }
        }
        schedule?.apply {
            taskId = taskInfo?.taskId ?: ""
            val repeatable = dialog_schedule_add_content_repeat_switch?.isChecked
            scheduleRepeat = repeatable ?: false
            updateTime = calendar.time
            val checkedCalendar = Calendar.getInstance(Locale.getDefault())
            checkedCalendar.timeInMillis = checkedDate?.time ?: calendar.timeInMillis
            checkedCalendar.set(Calendar.HOUR_OF_DAY, checkTimeHour)
            checkedCalendar.set(Calendar.MINUTE, checkTimeMinute)
            scheduleTime = checkedCalendar.time
            val checkedDateList = arrayListOf<ScheduleWeekDate>()
            for (i in checkedRepeatDate.withIndex()) {
                if (i.value == 1) {
                    var scheduleWeekDate = ScheduleWeekDate.NEVER
                    scheduleWeekDate = scheduleWeekDate.getScheduleWeekDate(i.index + 1)
                    checkedDateList.add(scheduleWeekDate)
                }
            }
            setScheduleRepeatTimeJson(checkedDateList)

            scheduleTag = dialog_schedule_add_content_tag_et?.text.toString()
            setScheduleStatus(ScheduleStatus.DOING)
        }

        listener?.save(taskInfo, schedule)
        this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
    }

    /**
     * 显示选择类型弹窗
     */
    private fun showChooseScheduleTypeDialogFragment() {
        if (mContext == null) {
            L.w(TAG, "showChooseScheduleTypeDialogFragment: mContext is null. ")
            return
        }
        val manager = childFragmentManager
        var chooseScheduleTypeDialogFragment =
            manager.findFragmentByTag("ChooseScheduleTypeDialogFragment") as ChooseScheduleTypeDialogFragment?
        if (chooseScheduleTypeDialogFragment == null) {
            chooseScheduleTypeDialogFragment =
                ChooseScheduleTypeDialogFragment.getInstant(ChooseScheduleTypeDialogListenerImpl())
        }
        if (!chooseScheduleTypeDialogFragment.isAdded && !chooseScheduleTypeDialogFragment.isVisible) {
            chooseScheduleTypeDialogFragment.show(manager, "ChooseScheduleTypeDialogFragment")
        }
    }

    private inner class ChooseScheduleTypeDialogListenerImpl :
        ChooseScheduleTypeDialogFragment.ChooseScheduleTypeDialogListener {
        override fun chooseType(type: Int) {
            // 提示 启动 闹钟 无
            when (type) {
                1 -> {
                    // 提示
                    scheduleTaskAction = TaskAction.HINT
                }
                2 -> {
                    // 启动
                    scheduleTaskAction = TaskAction.LAUNCHER
                }
                3 -> {
                    // 闹钟
                    scheduleTaskAction = TaskAction.CLOCK
                }
                4 -> {
                    // 无
                    scheduleTaskAction = TaskAction.NONE
                }
                else -> {
                    // 取消
                }
            }
            dialog_schedule_add_content_action_bnl?.visibility =
                if (TaskAction.LAUNCHER.equals(scheduleTaskAction)) View.VISIBLE else View.GONE
            dialog_schedule_add_content_action_type_tv?.text =
                getTaskActionString(scheduleTaskAction.value)
            hideChooseScheduleTypeDialogFragment()
        }
    }

    /**
     * 隐藏类型选择弹窗
     */
    private fun hideChooseScheduleTypeDialogFragment() {
        if (mContext == null) {
            L.w(TAG, "hideChooseScheduleTypeDialogFragment: mContext is null. ")
            return
        }
        val manager = childFragmentManager
        val chooseScheduleTypeDialogFragment =
            manager.findFragmentByTag("ChooseScheduleTypeDialogFragment") as ChooseScheduleTypeDialogFragment?
        chooseScheduleTypeDialogFragment?.apply {
            dismissAllowingStateLoss()
        }
    }

    interface ScheduleAddDFListener {
        fun save(taskInfo: TaskInfo?, taskSchedule: TaskSchedule?)

        /**
         * 设置重复时间
         */
        fun setRepeatDate(checkedDateArray: IntArray)

        /**
         * 选择启动应用
         */
        fun chooseLauncherApplication();

        /**
         * 选择计划类型
         */
        fun chooseScheduleType()
    }

    /**
     * 设置选中的程序信息
     */
    fun setChockedLauncherApplication(packageInfo: PackageInfo) {
        dialog_schedule_add_content_action_name_tv?.text =
            packageInfo.applicationInfo.loadLabel(mContext.packageManager)
        this.checkedPackageInfo = packageInfo
        this.checkedPackageName = packageInfo.packageName
    }

    /**
     * 设置自定义时间
     */
    fun setCustomDate(customDate: IntArray) {
        this.checkedRepeatDate = customDate
        var timeStr = ""
        var i = 0
        for (date in customDate) {
            if (date == 1) {
                var scheduleWeekDate = ScheduleWeekDate.NEVER
                scheduleWeekDate = scheduleWeekDate.getScheduleWeekDate(i + 1)
                timeStr += scheduleWeekDate.getString(mContext)
                timeStr += ","
            }
            i += 1
        }
        if (timeStr.isNotEmpty()) {
            timeStr = timeStr.substring(0, timeStr.length - 1)
        } else {
            timeStr = getString(R.string._never)
        }
        dialog_schedule_add_content_repeat_date_detail_tv?.text = timeStr
    }

}