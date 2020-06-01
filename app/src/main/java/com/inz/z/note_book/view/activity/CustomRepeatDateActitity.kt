package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.adapter.RepeatDateRvAdapter
import kotlinx.android.synthetic.main.activity_custom_repeat_date.*

/**
 * 自定义重复日期
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 17:57.
 */
class CustomRepeatDateActitity : AbsBaseActivity() {
    companion object {
        private const val TAG = "CustomRepeatDateActitity"
    }

    var checkedWeek = intArrayOf(0, 0, 0, 0, 0, 0, 0)

    var repeatDataRvAdapter: RepeatDateRvAdapter? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_custom_repeat_date
    }

    override fun initView() {
        activity_crd_content_week_1_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())
        activity_crd_content_week_2_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())
        activity_crd_content_week_3_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())
        activity_crd_content_week_4_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())
        activity_crd_content_week_5_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())
        activity_crd_content_week_6_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())
        activity_crd_content_week_7_check_box?.setOnCheckedChangeListener(CheckBoxClickListenerImpl())

        activity_crd_top_left_ll?.setOnClickListener {
            saveCheckedDate()
        }

        repeatDataRvAdapter = RepeatDateRvAdapter(mContext)
        repeatDataRvAdapter!!.repeatDateRvAdapterListener = RepeatDateRvAdapterListenerImpl()

        activity_crd_content_rv?.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = repeatDataRvAdapter

        }

        activity_crd_top_right_ll?.setOnClickListener {
            saveCheckedDate()
        }
    }

    override fun initData() {
        val bundle = intent?.extras
        bundle?.let {
            checkedWeek = it.getIntArray("RepeatDate") ?: checkedWeek
        }

        repeatDataRvAdapter?.refreshData(checkedWeek.toList())
    }

    /**
     * 保存选中的日期
     */
    private fun saveCheckedDate() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putIntArray("CheckWeek", checkedWeek)
        intent.putExtras(bundle)
        setResult(Constants.CUSTOM_DATE_REQUEST_CODE, intent)
        finish()
    }

    inner class CheckBoxClickListenerImpl : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            when (buttonView?.id) {
                activity_crd_content_week_1_check_box.id -> {
                    checkedWeek[0] = if (isChecked) 1 else 0
                }
                activity_crd_content_week_2_check_box.id -> {
                    checkedWeek[1] = if (isChecked) 1 else 0
                }
                activity_crd_content_week_3_check_box.id -> {
                    checkedWeek[2] = if (isChecked) 1 else 0
                }
                activity_crd_content_week_4_check_box.id -> {
                    checkedWeek[3] = if (isChecked) 1 else 0
                }
                activity_crd_content_week_5_check_box.id -> {
                    checkedWeek[4] = if (isChecked) 1 else 0
                }
                activity_crd_content_week_6_check_box.id -> {
                    checkedWeek[5] = if (isChecked) 1 else 0
                }
                activity_crd_content_week_7_check_box.id -> {
                    checkedWeek[6] = if (isChecked) 1 else 0
                }
                else -> {

                }

            }
        }
    }

    /**
     * 适配器监听 实现
     */
    inner class RepeatDateRvAdapterListenerImpl : RepeatDateRvAdapter.RepeatDateRvAdapterListener {
        override fun onChangeStatus(checked: Boolean, position: Int) {
            if (position > -1 && position < checkedWeek.size) {
                checkedWeek.set(position, if (checked) 1 else 0)
            }
        }
    }
}