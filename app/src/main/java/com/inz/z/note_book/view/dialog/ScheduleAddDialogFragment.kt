package com.inz.z.note_book.view.dialog

import android.content.pm.PackageInfo
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.RepeatType
import com.inz.z.note_book.base.ScheduleType
import com.inz.z.note_book.base.TaskValue
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.database.bean.RepeatInfo
import com.inz.z.note_book.database.bean.TaskInfo
import com.inz.z.note_book.database.bean.TaskSchedule
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
         * 默认重复信息日期
         */
        private val BASE_REPEAT_DATE_ARRAY = intArrayOf(0, 0, 0, 0, 0, 0, 0)

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
    private var checkedRepeatDate: IntArray = BASE_REPEAT_DATE_ARRAY

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

    /**
     * 重复过类型
     */
    @RepeatType
    private var repeatType: Int = TaskValue.TASK_REPEAT_TYPE_NONE

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
            dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateBnl?.visibility =
                if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                dialogScheduleAddBinding?.let {
                    it.dialogScheduleAddContentNsv.post {
                        it.dialogScheduleAddContentNsv.fullScroll(NestedScrollView.FOCUS_DOWN)
                    }
                }
            }
        }

        dialogScheduleAddBinding?.dialogScheduleAddContentActionTypeBnl?.setOnClickListener(this)
        // 默认 不显示 重复
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateBnl?.visibility = View.GONE
    }

    override fun initData() {
        // 初始化 ViewModel
        initViewModel()

        val bundle = arguments
        bundle?.let {
            taskScheduleId = it.getString("TaskScheduleId", "")
            // 获取 选中时间
            checkedDate = it.getSerializable("checkedDate") as Date?
        }
        L.d(TAG, "initData: taskScheduleId = $taskScheduleId ;  checkDate = $checkedDate ")

        // 加载 ViewModel 数据 。
        loadViewModelData()

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
                    // 选择 重复类型与 时间
                    listener?.setRepeatDate(repeatType, checkedRepeatDate)
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
    }

    /**
     * 加载 ViewModel 数据监听 。
     */
    private fun loadViewModelData() {
        // 获取 任务 计划 信息
        taskScheduleAddViewModel?.getTaskSchedule(taskScheduleId)
            ?.observe(
                this,
                Observer<TaskSchedule> {
                    L.i(TAG, "loadViewModelData: taskSchedule = $it")
                    if (it == null) {
                        this.schedule = createNewTaskScheduleInfo()
                    } else {
                        this.schedule = it
                    }
                    this.schedule?.let { schedule ->
                        // 更新 界面
                        updateViewBySchedule(schedule)
                        // 通过 任务计划信息 加载 其他数据
                        loadViewModelDataBySchedule(schedule)
                    }
                }
            )

    }

    /**
     * 通过 任务 计划 信息 获取 其他数据
     */
    private fun loadViewModelDataBySchedule(schedule: TaskSchedule) {
        // 获取  重复信息
        taskScheduleAddViewModel?.getRepeatInfoList(schedule.taskScheduleId)
            ?.observe(
                this,
                Observer<RepeatInfo> { repeatInfo ->
                    L.i(TAG, "initViewModel: repeat = $repeatInfo")
                    val repeatType: Int
                    val repeatDateArray: IntArray
                    // 获取 重复类型 。 默认为 不重复
                    if (repeatInfo != null) {
                        repeatType = repeatInfo.repeatType
                        repeatDateArray = repeatInfo.customRepeatDataArray
                    } else {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_NONE
                        repeatDateArray = BASE_REPEAT_DATE_ARRAY
                    }
                    // 更新重复类型
                    updateRepeatType(repeatType, repeatDateArray)
                }
            )
        // 获取 任务 信息
        taskScheduleAddViewModel?.getTaskInfo()
            ?.observe(
                this,
                Observer { taskInfo ->
                    L.i(TAG, "loadViewModelDataBySchedule: taskInfo = $taskInfo")
                    // 如果 获取 的任务 信息 为空，新建 任务信息
                    if (taskInfo == null) {
                        this.taskInfo = createNewTaskInfo()
                    } else {
                        this.taskInfo = taskInfo
                        // 通过 Action 获取对应 的 触发事件
                        this.scheduleType = getScheduleType(taskInfo.taskAction)
                        L.d(TAG, "loadViewModelDataBySchedule: scheduleType = $scheduleType")
                        // 处理 启动 任务
                        dealLauncherTask(taskInfo)
                    }
                    // 更新 任务计划 关联 的任务 ID
                    this.schedule?.taskId = this.taskInfo?.taskId ?: ""
                    // 更新 时间类型 UI
                    updateScheduleActionView(this.scheduleType)
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
     * 处理启动类型 任务
     * @param taskInfo 任务信息
     */
    private fun dealLauncherTask(taskInfo: TaskInfo) {
        if (taskInfo.taskAction != TaskValue.TASK_ACTION_LAUNCHER) {
            return
        }
        L.w(TAG, "dealLauncherTask: this task is launcher !!! ")
        this.checkedPackageName = taskInfo.taskPackageName
        // 获取 选中 执行事件包名 。
        if (checkedPackageName.isNotEmpty()) {
            checkedPackageInfo =
                LauncherHelper.findApplicationInfoByPackageName(mContext,
                    checkedPackageName)
            // 如果选中 包名不为空时，设置选中 应用
            if (checkedPackageInfo != null) {
                setChockedLauncherApplication(checkedPackageInfo!!)
            }
        }
    }

    /**
     * 创建信息 任务信息
     */
    private fun createNewTaskInfo(): TaskInfo {
        val id = BaseTools.getUUID()
        val calendar = Calendar.getInstance(Locale.getDefault())
        return TaskInfo()
            .apply {
                this.taskId = id
                this.taskAction = TaskValue.TASK_ACTION_SCHEDULE
                this.createTime = calendar.time
            }
    }

    /**
     * 创建 新 任务计划 信息
     */
    private fun createNewTaskScheduleInfo(): TaskSchedule {
        val scheduleId = BaseTools.getUUID()
        taskScheduleId = scheduleId
        val calendar = Calendar.getInstance(Locale.getDefault())
        return TaskSchedule()
            .apply {
                this.taskScheduleId = scheduleId
                // 新建 时， 默认使用当前 时间
                this.scheduleTime = calendar.time
                this.createTime = calendar.time
            }
    }

    /**
     * 通过 任务计划 更新 UI 界面
     */
    private fun updateViewBySchedule(schedule: TaskSchedule) {
        val scheduleDate = schedule.scheduleTime
        val minuteStr = BaseTools.getDateFormat("mm", Locale.getDefault()).format(scheduleDate)
        this.checkTimeMinute = minuteStr.toInt()
        val hourStr = BaseTools.getDateFormat("HH", Locale.getDefault()).format(scheduleDate)
        this.checkTimeHour = hourStr.toInt()
        // 设置 timePicker 时间
        dialogScheduleAddBinding?.dialogScheduleAddContentNsv?.content_time_picker?.apply {
            hour = checkTimeHour
            minute = checkTimeMinute
        }

        // 是否 重复
        val isRepeat = schedule.scheduleRepeat
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatSwitch?.isChecked = isRepeat

        // TODO: 2021/10/17 可能多个标签
        // 设置标签
        val scheduleTag = schedule.scheduleTag
        dialogScheduleAddBinding?.dialogScheduleAddContentTagEt?.setText(scheduleTag)
        // 是否显示重复 日期 内容
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateBnl?.visibility =
            if (isRepeat) View.VISIBLE else View.GONE
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
        // 更新 任务信息
        taskInfo?.apply {
            this.updateTime = calendar.time
        }
        // 是否需要重复。
        val repeatable =
            dialogScheduleAddBinding?.dialogScheduleAddContentRepeatSwitch?.isChecked ?: false
        // 设置计划信息
        schedule?.apply {
            this.scheduleRepeat = repeatable
            this.updateTime = calendar.time
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
            this.scheduleTime = checkedCalendar.time

            // 更新 计划 标签
            // TODO: 2021/10/19 TAG have many
            this.scheduleTag = dialog_schedule_add_content_tag_et?.text.toString()
            // 设置计划状态 。
            this.scheduleStatus = ScheduleStatus.NOT_STARTED
        }
        if (taskInfo != null && schedule != null) {
            // 更新 任务 与 任务计划信息
            taskScheduleAddViewModel?.updateTaskScheduleInfo(taskInfo!!, schedule!!)
            // 更新 重复 日期 信息
            taskScheduleAddViewModel?.updateRepeatInfo(
                this.taskScheduleId,
                repeatType,
                checkedRepeatDate
            )
        }

        listener?.savedTaskSchedule(taskScheduleId)
        this@ScheduleAddDialogFragment.dismissAllowingStateLoss()
    }

    /**
     * 显示选择类型弹窗
     * @param scheduleType 计划类型
     */
    private fun showChooseScheduleTypeDialogFragment(@ScheduleType scheduleType: Int) {
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
    private fun updateScheduleActionView(@ScheduleType scheduleType: Int) {
        dialogScheduleAddBinding?.let {
            // 是否需要显示
            val needShow = TaskValue.SCHEDULE_LAUNCHER == scheduleType
            // 设置是否显示
            it.dialogScheduleAddContentActionBnl.visibility =
                if (needShow) View.VISIBLE else View.GONE
            // 更新 显示 计划类型
            it.dialogScheduleAddContentActionTypeTv.text = getScheduleTypeStr(scheduleType)
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

    /**
     * 计划添加Dialog 监听。
     */
    interface ScheduleAddDFListener {
        /**
         * 保存 任务 计划信息
         */
        fun savedTaskSchedule(taskScheduleId: String)

        /**
         * 设置重复时间
         * @param repeatType 重复类型
         * @param checkedDateArray 自定义 重复时间
         */
        fun setRepeatDate(@RepeatType repeatType: Int, checkedDateArray: IntArray)

        /**
         * 选择启动应用
         */
        fun chooseLauncherApplication();
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
     * 获取重复日期 显示内容
     * @param repeatType 重复类型
     * @param repeatDateArray 重复时间
     */
    private fun getRepeatDateString(
        @RepeatType repeatType: Int,
        repeatDateArray: IntArray?
    ): String {
        val str = StringBuilder()
        mContext?.let {
            when (repeatType) {
                TaskValue.TASK_REPEAT_TYPE_NONE -> {
                    str.append(it.getString(R.string.repeat_once))
                }
                TaskValue.TASK_REPEAT_TYPE_DATE -> {
                    str.append(it.getString(R.string.every_day))
                }
                TaskValue.TASK_REPEAT_TYPE_WEEK -> {
                    str.append(it.getString(R.string.every_week))
                }
                TaskValue.TASK_REPEAT_TYPE_MONTH -> {
                    str.append(it.getString(R.string.every_month))
                }
                TaskValue.TASK_REPEAT_TYPE_LUNAR_MONTH -> {
                    str.append(it.getString(R.string.every_year_lunar))
                }
                TaskValue.TASK_REPEAT_TYPE_YEAR -> {
                    str.append(it.getString(R.string.every_year))
                }
                TaskValue.TASK_REPEAT_TYPE_CUSTOM -> {
                    str.append(it.getString(R.string.repeat_custom))
                    repeatDateArray?.let { array ->
                        val strArray = it.resources.getStringArray(R.array._date_week_array)
                        array.forEachIndexed { index, i ->
                            if (i == 1) {
                                str.append("，").append(strArray[index])
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
        L.i(TAG, "getRepeatDateString: --->> $str")
        return str.toString()
    }

    /**
     * 更新 重复类型
     * @param repeatType 重复类型
     * @param repeatDateArray 自定义 重复 时间
     */
    fun updateRepeatType(@RepeatType repeatType: Int, repeatDateArray: IntArray?) {
        this.repeatType = repeatType
        this.checkedRepeatDate = repeatDateArray ?: BASE_REPEAT_DATE_ARRAY
        // 设置显示 内容。
        dialogScheduleAddBinding?.dialogScheduleAddContentRepeatDateDetailTv?.text =
            getRepeatDateString(repeatType, repeatDateArray)
    }


    ///////////////////////////////////////////////////////////////////////////
    // 选择 标签
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 显示 标签
     */
    private fun showChooseTagFragment() {

    }

}