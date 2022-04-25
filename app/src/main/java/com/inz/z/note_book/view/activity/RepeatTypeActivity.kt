package com.inz.z.note_book.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.base.RepeatType
import com.inz.z.note_book.base.TaskValue
import com.inz.z.note_book.databinding.ActivityChooseRepeatTypeBinding
import com.inz.z.note_book.util.Constants
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

/**
 * remove: # 自定义重复日期
 *
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 17:57.
 * <br/>
 * update by laptop 3 in 2021/10/20 22:26
 * 重复类型。
 *
 */
class RepeatTypeActivity : AbsBaseActivity(), CompoundButton.OnCheckedChangeListener {
    companion object {
        private const val TAG = "RepeatTypeActivity"

        /**
         * 启动 选择重复类型
         * @param activity 启动界面
         * @param repeatType 重复类型
         * @param repeatDates 自定义重复时间
         */
        fun startChooseRepeatTypeActivityForResult(
            activity: Activity,
            requestCode: Int,
            @RepeatType repeatType: Int?,
            repeatDates: IntArray?
        ) {
            val intent = Intent(activity, RepeatTypeActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(Constants.TaskParams.PARAMS_REPEAT_TYPE,
                repeatType ?: TaskValue.TASK_REPEAT_TYPE_NONE)
            bundle.putIntArray(Constants.TaskParams.PARAMS_REPEAT_DATE, repeatDates)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private var activityChooseRepeatTypeBinding: ActivityChooseRepeatTypeBinding? = null

    /**
     * 重复类型
     */
    @RepeatType
    private var repeatType: Int = TaskValue.TASK_REPEAT_TYPE_NONE

    /**
     * 自定义   重复周 日期
     */
    private var repeatWeekArray: IntArray? = intArrayOf(0, 0, 0, 0, 0, 0, 0)
    private var repeatTypeRadioButtons: Array<RadioButton>? = null
    private var repeatWeekChockBox: Array<CheckBox>? = null

    override fun initWindow() {

    }

    override fun resetBottomNavigationBar(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_choose_repeat_type
    }

    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        activityChooseRepeatTypeBinding = ActivityChooseRepeatTypeBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }

    }

    override fun initView() {
//        // 设置状态栏 为 白日模式
//        QMUIStatusBarHelper.setStatusBarLightMode(this)
//        // 设置状态栏 颜色
//        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)

        activityChooseRepeatTypeBinding?.chooseRepeatTypeToolbar?.let {
            setSupportActionBar(it)
        }
        activityChooseRepeatTypeBinding?.let {
            repeatTypeRadioButtons = arrayOf(
                it.activityCrdRepeatOnceRbtn,
                it.activityCrdRepeatEveryDayRbtn,
                it.activityCrdRepeatEveryWeekRbtn,
                it.activityCrdRepeatEveryMonthRbtn,
                it.activityCrdRepeatEveryYearRbtn,
                it.activityCrdRepeatEveryYearLunarRbtn,
                it.activityCrdRepeatCustomRbtn
            )
            repeatTypeRadioButtons?.forEach { radioButton ->
                radioButton.setOnCheckedChangeListener(this@RepeatTypeActivity)
            }
            repeatWeekChockBox = arrayOf(
                it.activityCrdCustomDay1Cbox,
                it.activityCrdCustomDay2Cbox,
                it.activityCrdCustomDay3Cbox,
                it.activityCrdCustomDay4Cbox,
                it.activityCrdCustomDay5Cbox,
                it.activityCrdCustomDay6Cbox,
                it.activityCrdCustomDay7Cbox
            )
            repeatWeekChockBox?.forEach { checkBox ->
                checkBox.setOnCheckedChangeListener(this@RepeatTypeActivity)
            }
        }

    }

    override fun initData() {
        val bundle = intent?.extras
        bundle?.let {
            repeatType =
                it.getInt(Constants.TaskParams.PARAMS_REPEAT_TYPE, TaskValue.TASK_REPEAT_TYPE_NONE)
            repeatWeekArray =
                it.getIntArray(Constants.TaskParams.PARAMS_REPEAT_DATE) ?: repeatWeekArray
        }

        updateCustomCheckBox()
        // 更新 选中类型
        updateChooseRepeatType()
        // 更新是否显示重复
        updateCustomRepeatView(repeatType == TaskValue.TASK_REPEAT_TYPE_CUSTOM)
//
//        repeatDataRvAdapter?.refreshData(checkedWeek.toList())
    }

    private fun updateChooseRepeatType() {
        activityChooseRepeatTypeBinding?.let {
            when (repeatType) {
                TaskValue.TASK_REPEAT_TYPE_NONE -> {
                    it.activityCrdRepeatOnceRbtn.isChecked = true
                }
                TaskValue.TASK_REPEAT_TYPE_DATE -> {
                    it.activityCrdRepeatEveryDayRbtn.isChecked = true
                }
                TaskValue.TASK_REPEAT_TYPE_WEEK -> {
                    it.activityCrdRepeatEveryWeekRbtn.isChecked = true
                }
                TaskValue.TASK_REPEAT_TYPE_MONTH -> {
                    it.activityCrdRepeatEveryMonthRbtn.isChecked = true
                }
                TaskValue.TASK_REPEAT_TYPE_YEAR -> {
                    it.activityCrdRepeatEveryYearRbtn.isChecked = true
                }
                TaskValue.TASK_REPEAT_TYPE_LUNAR_MONTH -> {
                    it.activityCrdRepeatEveryYearLunarRbtn.isChecked = true
                }
                TaskValue.TASK_REPEAT_TYPE_CUSTOM -> {
                    it.activityCrdRepeatCustomRbtn.isChecked = true
                }
            }
        }
    }

    /**
     * 更新 自定义 选中
     */
    private fun updateCustomCheckBox() {
        repeatWeekArray?.forEachIndexed { index, value ->
            repeatWeekChockBox?.get(index)?.isChecked = value == 1
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 点击 返回 键
            android.R.id.home -> {
                // 保存数据
                saveCheckedDate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        activityChooseRepeatTypeBinding?.let {
            var isDeal = false
            // 如果当前选中 自定义 重复，更新界面
            if (buttonView?.id == it.activityCrdRepeatCustomRbtn.id) {
                if (isChecked) {
                    repeatType = TaskValue.TASK_REPEAT_TYPE_CUSTOM
                    isDeal = true
                }
                // 更新自定义 重复时间显示
                updateCustomRepeatView(isChecked)
            }
            // 避免 执行 过多判断
            if (isDeal) return
            if (isChecked) {
                when (buttonView?.id) {
                    it.activityCrdRepeatOnceRbtn.id -> {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_NONE
                        isDeal = true
                    }
                    it.activityCrdRepeatEveryDayRbtn.id -> {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_DATE
                        isDeal = true
                    }
                    it.activityCrdRepeatEveryWeekRbtn.id -> {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_WEEK
                        isDeal = true
                    }
                    it.activityCrdRepeatEveryMonthRbtn.id -> {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_MONTH
                        isDeal = true
                    }
                    it.activityCrdRepeatEveryYearRbtn.id -> {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_YEAR
                        isDeal = true
                    }
                    it.activityCrdRepeatEveryYearLunarRbtn.id -> {
                        repeatType = TaskValue.TASK_REPEAT_TYPE_LUNAR_MONTH
                        isDeal = true
                    }

                }
            }
            // 避免 执行 过多判断
            if (isDeal) return

            repeatWeekArray?.let { repeatWeekArray ->
                when (buttonView?.id) {
                    it.activityCrdCustomDay1Cbox.id -> {
                        repeatWeekArray[0] = if (isChecked) 1 else 0
                    }
                    it.activityCrdCustomDay2Cbox.id -> {
                        repeatWeekArray[1] = if (isChecked) 1 else 0
                    }
                    it.activityCrdCustomDay3Cbox.id -> {
                        repeatWeekArray[2] = if (isChecked) 1 else 0
                    }
                    it.activityCrdCustomDay4Cbox.id -> {
                        repeatWeekArray[3] = if (isChecked) 1 else 0
                    }
                    it.activityCrdCustomDay5Cbox.id -> {
                        repeatWeekArray[4] = if (isChecked) 1 else 0
                    }
                    it.activityCrdCustomDay6Cbox.id -> {
                        repeatWeekArray[5] = if (isChecked) 1 else 0
                    }
                    it.activityCrdCustomDay7Cbox.id -> {
                        repeatWeekArray[6] = if (isChecked) 1 else 0
                    }
                }
            }
        }
    }

    override fun onDestroyData() {
        super.onDestroyData()
        repeatTypeRadioButtons = null
        repeatWeekChockBox = null
        repeatWeekArray = null
    }

    /**
     * 更新 自定义 重复 界面
     * @param show 是否显示
     */
    private fun updateCustomRepeatView(show: Boolean) {
        activityChooseRepeatTypeBinding?.let {
            it.activityCrdCustomLl.visibility = if (show) View.VISIBLE else View.GONE
            // 界面滑动到 底部
            it.activityCrdContentNsv.let { scrollView ->
                if (show) {
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }

                }
            }
        }
    }

    /**
     * 保存选中的日期
     */
    private fun saveCheckedDate() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(Constants.TaskParams.PARAMS_REPEAT_TYPE, repeatType)
        bundle.putIntArray(Constants.TaskParams.PARAMS_REPEAT_DATE, repeatWeekArray)
        intent.putExtras(bundle)
        setResult(Constants.TaskParams.RESULT_REPEAT_TYPE_CODE, intent)
        finish()
    }

}