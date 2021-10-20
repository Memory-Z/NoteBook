package com.inz.z.note_book.view.dialog

import android.content.pm.PackageInfo
import android.database.Observable
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.ScheduleType
import com.inz.z.note_book.base.TaskValue
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.bean.inside.ScheduleWeekDate
import com.inz.z.note_book.database.bean.RepeatInfo
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.database.controller.TaskInfoController
import com.inz.z.note_book.database.controller.TaskScheduleController
import com.inz.z.note_book.databinding.DialogScheduleAddBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.view.dialog.viewmodel.TaskScheduleAddViewModel
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

    /**
     * 计划类型
     */
    @ScheduleType
    private var scheduleType: Int = TaskValue.SCHEDULE_NONE

    private var taskScheduleId = ""
    private var checkedDate: Date? = null
    private var taskScheduleAddViewModel: TaskScheduleAddViewModel? = null


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
        dialogScheduleAddBinding = DialogScheduleAddBinding.inflate(layoutInflater)
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
        // 初始化 ViewModel
        initViewModel()
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
        L.d(TAG, "initData: checkDate = $checkedDate}")
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

            // 获取 需要 重复的日期
            taskScheduleAddViewModel?.findTaskScheduleRepeatList(it.taskScheduleId)

            // 是否 重复
            val isRepeat = it.scheduleRepeat
            dialogScheduleAddBinding?.dialogScheduleAddContentRepeatSwitch?.isChecked = isRepeat
            // TODO: 2021/10/17 可能多个标签
            // 设置标签
            val scheduleTag = it.scheduleTag
            dialogScheduleAddBinding?.dialogScheduleAddContentTagEt?.setText(scheduleTag)
            // 是否显示重复 日期 内容
            dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateBnl?.visibility =
                if (isRepeat) View.VISIBLE else View.GONE

        }
        if (schedule == null) {
            this.checkTimeHour = checkTimeHour
            this.checkTimeMinute = checkTimeMinute
        }
        taskInfo?.let {
            checkedPackageName = it.taskPackageName
            // 通过 Action 获取对应 的 触发事件
            scheduleType = getScheduleType(it.taskAction)
            L.d(TAG, "initData: scheduleType = $scheduleType")
        }

        // 更新 时间类型 UI
        updateScheduleActionView(scheduleType)

        if (checkedPackageName.isNotEmpty()) {
            checkedPackageInfo =
                LauncherHelper.findApplicationInfoByPackageName(mContext, checkedPackageName)
        }
        // 如果选中 包名不为空时，设置选中 应用
        if (checkedPackageInfo != null) {
            setChockedLauncherApplication(checkedPackageInfo!!)
        }
        // 设置当前 重复时间
        setRepeatWeekDate(checkedRepeatDate)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val point = Point()
//            val displayMetrics = context.resources.displayMetrics
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

    override fun onDestroy() {
        super.onDestroy()
        destroyViewModel()
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
                // 弹窗 计划类型
                binding.dialogScheduleAddContentActionTypeBnl.id -> {
                    // 计划类型
                    val scheduleType = taskInfo?.type ?: 0
                    // 显示 类型 弹窗
                    showChooseScheduleTypeDialogFragment(scheduleType)
                }

                else -> {

                }
            }
        }
    }

    /**
     * 初始化 ViewModel
     */
    private fun initViewModel() {
        taskScheduleAddViewModel =
            ViewModelProvider.NewInstanceFactory().create(TaskScheduleAddViewModel::class.java)
        taskScheduleAddViewModel?.getRepeatInfoList()
            ?.observe(
                this,
                Observer<List<RepeatInfo>> { list ->
                    L.i(TAG, "initViewModel: repeat list = $list")
                    // TODO: 2021/10/20 deal  repeatInfoList.
                }
            )
        taskScheduleAddViewModel?.getTaskSchedule()
            ?.observe(
                this,
                Observer<TaskSchedule> {

                }
            )
    }

    /**
     * 销毁 ViewModel
     */
    private fun destroyViewModel() {
        taskScheduleAddViewModel?.destroy()
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
     * @param scheduleTypeStr 计划类型 描述
     */
    @ScheduleType
    private fun getScheduleType(scheduleTypeStr: String): Int {
        return TaskValue.getScheduleTypeByContentStr(scheduleTypeStr)
    }

    /**
     * 获取 计划类型 名称
     * @param scheduleType 计划类型
     */
    private fun getScheduleTypeStr(@ScheduleType scheduleType: Int): String {
        return TaskValue.getContentStrByType(scheduleType)
    }

    /**
     *
     * 保存 计划任务
     */
    private fun saveTaskSchedule() {
        // 获取当前时间
        val calendar = Calendar.getInstance(Locale.getDefault())
        // 如果 任务信息为 null , 创建 新任务信息， 并关联
        if (taskInfo == null) {
            taskInfo = TaskInfo()
                .apply {
                    taskId = BaseTools.getUUID()
                    createTime = calendar.time
                    updateTime = calendar.time
                }
        }

        taskInfo?.apply {
            // 获取计划 类型
            taskAction = TaskValue.TASK_ACTION_SCHEDULE
            // 任务 包名。
            taskPackageName = checkedPackageName
            taskRequestCode = SystemClock.uptimeMillis()
            updateTime = calendar.time
        }
        // 如果当前 计划 未创建，创建新任务计划。
        if (schedule == null) {
            schedule = TaskSchedule()
            schedule!!.apply {
                taskScheduleId = BaseTools.getUUID()
                createTime = calendar.time
                updateTime = calendar.time
            }
        }
        // 是否需要重复。
        val repeatable =
            dialogScheduleAddBinding?.dialogScheduleAddContentRepeatSwitch?.isChecked ?: false
        // 设置计划信息
        schedule?.apply {
            taskId = taskInfo?.taskId ?: ""
            scheduleRepeat = repeatable
            updateTime = calendar.time
            // 获取选中时间
            val checkedCalendar = Calendar.getInstance(Locale.getDefault())
            checkedCalendar.apply {
                timeInMillis = checkedDate?.time ?: calendar.timeInMillis
                set(Calendar.HOUR_OF_DAY, checkTimeHour)
                set(Calendar.MINUTE, checkTimeMinute)
                set(Calendar.MILLISECOND, 0)
            }
            // 如果 当前时间 大于 选中日期，设置选中时间为 后一日计划
            if (System.currentTimeMillis() > checkedCalendar.timeInMillis) {
                // 当前系统时间大于 计划 时间，时间时间日期+ 1
                checkedCalendar.set(Calendar.DATE, checkedCalendar.get(Calendar.DATE) + 1)
            }
            // 计划时间 。
            scheduleTime = checkedCalendar.time

            // TODO: 2021/10/19 this repeat info .remove.
            val checkedDateList = arrayListOf<ScheduleWeekDate>()
            checkedRepeatDate.forEachIndexed { index, d ->
                if (d == 1) {
                    val scheduleWeekDate = ScheduleWeekDate.NEVER
                }

            }
            for (i in checkedRepeatDate.withIndex()) {
                if (i.value == 1) {
                    var scheduleWeekDate = ScheduleWeekDate.NEVER
                    scheduleWeekDate = scheduleWeekDate.getScheduleWeekDate(i.index + 1)
                    checkedDateList.add(scheduleWeekDate)
                }
            }
//            setScheduleRepeatTimeJson(checkedDateList)

            // TODO: 2021/10/19 TAG have many
            scheduleTag = dialog_schedule_add_content_tag_et?.text.toString()
            // 设置计划状态 。
            scheduleStatus = ScheduleStatus.NOT_STARTED
        }
        // 如果 计划 不为空 且 允许重复
        if (schedule != null && repeatable) {

        }

        listener?.save(taskInfo, schedule)
        this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
    }

    /**
     * 保存 任务计划重复信息， 仅在 设置重复状态时调用，。
     */
    private fun saveTaskScheduleRepeatInfo(schedule: TaskSchedule) {
        // 是否需要 重复
        val isRepeat = schedule.scheduleRepeat
        // 如果需要重复，更新重复内容
        if (isRepeat) {
            // TODO: 2021/10/20 add Repeat Info
        } else {
            L.i(TAG, "saveTaskScheduleRepeatInfo: this not repeat, clear old repeat. ")
            // TODO: 2021/10/20 清除旧 的重复信息
        }

    }

    /**
     * 显示选择类型弹窗
     * @param scheduleType 计划类型
     */
    private fun showChooseScheduleTypeDialogFragment(scheduleType: Int) {
        if (mContext == null) {
            L.w(TAG, "showChooseScheduleTypeDialogFragment: mContext is null. ")
            return
        }
        val manager = childFragmentManager
        var chooseScheduleTypeDialogFragment =
            manager.findFragmentByTag("ChooseScheduleTypeDialogFragment") as ChooseScheduleTypeDialogFragment?
        if (chooseScheduleTypeDialogFragment == null) {
            chooseScheduleTypeDialogFragment =
                ChooseScheduleTypeDialogFragment.getInstant(scheduleType,
                    ChooseScheduleTypeDialogListenerImpl())
        }
        if (!chooseScheduleTypeDialogFragment.isAdded && !chooseScheduleTypeDialogFragment.isVisible) {
            chooseScheduleTypeDialogFragment.show(manager, "ChooseScheduleTypeDialogFragment")
        }
    }

    private inner class ChooseScheduleTypeDialogListenerImpl :
        ChooseScheduleTypeDialogFragment.ChooseScheduleTypeDialogListener {
        override fun chooseType(@ScheduleType type: Int) {
            // 更新 计划类型 View
            updateScheduleActionView(type)
            // 隐藏 计划类型 选择弹窗。
            hideChooseScheduleTypeDialogFragment()
        }
    }

    /**
     * 更新 计划类型 View
     * @param type 计划类型
     */
    private fun updateScheduleActionView(@ScheduleType type: Int) {
        dialogScheduleAddBinding?.let {
            // 是否需要显示
            val needShow = TaskValue.SCHEDULE_LAUNCHER == type
            // 设置是否显示
            it.dialogScheduleAddContentActionBnl.visibility =
                if (needShow) View.VISIBLE else View.GONE
            // 仅在 显示 状态下更新内容
            if (needShow) {
                it.dialogScheduleAddContentActionTypeTv.text = getScheduleTypeStr(type)
            }
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
        dialogScheduleAddBinding?.dialogScheduleAddContentActionNameTv?.text =
            packageInfo.applicationInfo.loadLabel(mContext.packageManager)
        this.checkedPackageInfo = packageInfo
        this.checkedPackageName = packageInfo.packageName
    }

    /**
     * 设置 周内 重复日期
     * @param repeatDateArray 重复日期数组，值为1 表示需要重复
     */
    fun setRepeatWeekDate(repeatDateArray: IntArray) {
        this.checkedRepeatDate = repeatDateArray
        var timeStr = ""
        var i = 0
        for (date in repeatDateArray) {
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
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateDetailTv?.text = timeStr
    }

}