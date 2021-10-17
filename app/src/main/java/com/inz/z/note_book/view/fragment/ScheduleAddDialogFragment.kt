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
import com.inz.z.note_book.base.ScheduleType
import com.inz.z.note_book.base.ScheduleTypeValue
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

    /**
     * 计划类型
     */
    @ScheduleType
    private var scheduleType: Int = ScheduleTypeValue.NONE

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
        // 事件类型
        dialogScheduleAddBinding?.dialogScheduleAddContentActionTypeTv?.text =
            getScheduleTypeStr(scheduleType)
        // 是否显示 事件 行， 仅在 事件 为启动某程序 时显示
        dialogScheduleAddBinding?.dialogScheduleAddContentActionBnl?.visibility =
            if (ScheduleTypeValue.LAUNCHER == scheduleType) View.VISIBLE else View.GONE
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
        return ScheduleTypeValue.getScheduleTypeByContentStr(scheduleTypeStr)
    }

    /**
     * 获取 计划类型 名称
     * @param scheduleType 计划类型
     */
    private fun getScheduleTypeStr(@ScheduleType scheduleType: Int): String {
        return ScheduleTypeValue.getContentStrByType(scheduleType)
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
            // 获取计划 类型
            taskAction = getScheduleTypeStr(scheduleType)
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
            dialog_schedule_add_content_action_bnl?.visibility =
                if (ScheduleTypeValue.LAUNCHER == type) View.VISIBLE else View.GONE
            dialog_schedule_add_content_action_type_tv?.text = getScheduleTypeStr(type)
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
        dialog_schedule_add_content_repeat_date_detail_tv?.text = timeStr
    }

}