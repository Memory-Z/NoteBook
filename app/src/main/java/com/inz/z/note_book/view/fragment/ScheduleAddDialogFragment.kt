package com.inz.z.note_book.view.fragment

import android.content.pm.PackageInfo
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import com.inz.z.note_book.databinding.DialogScheduleAddBinding
import com.inz.z.note_book.util.ClickUtil
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
class ScheduleAddDialogFragment private constructor() : AbsBaseDialogFragment(),
    View.OnClickListener {
    companion object {
        const val TAG = "ScheduleAddDialogFragment"

        /**
         * 获取实例
         * @param taskScheduleId 任务计划ID
         * @param checkedDate 选中日期
         * @param listener 监听
         */
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

    private var dialogScheduleAddBinding: DialogScheduleAddBinding? = null

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

    /**
     * 任务信息
     */
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

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun getViewBindingView(): View? {
        dialogScheduleAddBinding = DialogScheduleAddBinding.inflate(layoutInflater, null, false)
        return dialogScheduleAddBinding?.root
    }

    override fun initView() {
        // 设置 点击 时间监听
        dialogScheduleAddBinding?.dialogScheduleAddTopCancelTv?.setOnClickListener(this)
        dialogScheduleAddBinding?.dialogScheduleAddTopSaveTv?.setOnClickListener(this)
        dialogScheduleAddBinding?.dialogScheduleAddContentNsv?.content_time_picker?.let {
            it.setIs24HourView(true)
            it.setOnTimeChangedListener { _, hourOfDay, minute ->
                checkTimeHour = hourOfDay
                checkTimeMinute = minute
                L.i(TAG, "change time  $hourOfDay : $minute ")
            }
        }
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateBnl?.setOnClickListener(this)
        dialogScheduleAddBinding?.dialogScheduleAddContentActionBnl?.setOnClickListener(this)
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            dialog_schedule_add_content_repeat_date_bnl?.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        dialogScheduleAddBinding?.dialogScheduleAddContentActionTypeBnl?.setOnClickListener(this)
        // 默认 不显示 重复
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateBnl?.visibility = View.GONE
    }

    override fun initData() {
        // 获取 当前 时间
        val calendar = Calendar.getInstance(Locale.getDefault())
        var checkTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
        var checkTimeMinute = calendar.get(Calendar.MINUTE)

        val bundle = arguments
        bundle?.let {
            taskScheduleId = it.getString("TaskScheduleId", "")
            // 获取的 任务计划 ID 不为空，表明 非新建 计划， 获取原计划信息
            if (taskScheduleId.isNotEmpty()) {
                schedule = TaskScheduleController.findTaskScheduleById(taskScheduleId)
                val taskId = schedule?.taskId ?: ""
                // 判断 当前计划 关联的 任务 是否为空， 如果不为空，获取 关联的任务信息
                if (taskId.isNotEmpty()) {
                    taskInfo = TaskInfoController.queryTaskInfoById(taskId)
                }
            }
            // 获取 选中时间
            checkedDate = it.getSerializable("checkedDate") as Date?
        }
        schedule?.let {
            val scheduleDate = it.scheduleTime
            val minuteStr = BaseTools.getDateFormat("mm", Locale.getDefault()).format(scheduleDate)
            checkTimeMinute = minuteStr.toInt()
            val hourStr = BaseTools.getDateFormat("HH", Locale.getDefault()).format(scheduleDate)
            checkTimeHour = hourStr.toInt()
            // 设置 timePicker 时间
            dialogScheduleAddBinding?.dialogScheduleAddContentNsv?.content_time_picker?.apply {
                hour = checkTimeHour
                minute = checkTimeMinute
            }
            // 获取需要重复的 周期
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

    override fun onClick(v: View?) {
        // 判断是否为 快速点击
        if (ClickUtil.isFastClick(v)) return
        dialogScheduleAddBinding?.let { binding ->
            when (v?.id) {
                // 弹窗 顶部 取消
                binding.dialogScheduleAddTopCancelTv.id -> {
                    // 关闭 弹窗
                    this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
                }
                // 弹窗 顶部 保存
                binding.dialogScheduleAddTopSaveTv.id -> {
                    // 保存任务 计划
                    saveTaskSchedule()
                }
                // 弹窗 重复时间  选项
                binding.dialogScheduleAddContentRepeatDateBnl.id -> {
                    listener?.setRepeatDate(checkedRepeatDate)
                }
                // 弹窗 添加 内容
                binding.dialogScheduleAddContentActionBnl.id -> {
                    listener?.chooseLauncherApplication()
                }
                // 弹窗 内容类型
                binding.dialogScheduleAddContentActionTypeBnl.id -> {
                    showChooseScheduleTypeDialogFragment()
                }

                else -> {

                }
            }
        }
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
            taskRequestCode = SystemClock.uptimeMillis().toInt()
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
            checkedCalendar.apply {
                timeInMillis = checkedDate?.time ?: calendar.timeInMillis
                set(Calendar.HOUR_OF_DAY, checkTimeHour)
                set(Calendar.MINUTE, checkTimeMinute)
                set(Calendar.MILLISECOND, 0)
            }
            if (System.currentTimeMillis() > checkedCalendar.timeInMillis) {
                // 当前系统时间大于 计划 时间，时间时间日期+ 1
                checkedCalendar.set(Calendar.DATE, checkedCalendar.get(Calendar.DATE) + 1)
            }
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