package com.inz.z.note_book.view.activity

import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.view.fragment.ScheduleAddDialogFragment
import kotlinx.android.synthetic.main.schedule_layout.*

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

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.schedule_layout
    }

    override fun initView() {
        schedule_top_back_iv?.setOnClickListener {
            this@ScheduleActivity.finish()
        }
        schedule_top_right_add_iv?.setOnClickListener {
//            showAddPopupMenu()
            showScheduleAddDialog()
        }

        schedule_content_rv?.apply {
            layoutManager = LinearLayoutManager(mContext)
        }
    }

    override fun initData() {

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
     */
    private fun showScheduleAddDialog() {
        if (mContext == null) {
            L.w(TAG, "showScheduleAddDialog: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var scheduleAddDialogFragment =
            manager.findFragmentByTag("ScheduleAddDialogFragment") as ScheduleAddDialogFragment?
        if (scheduleAddDialogFragment == null) {
            scheduleAddDialogFragment =
                ScheduleAddDialogFragment.getInstant(ScheduleAddDialogFragmentListenerImpl())
        }
        if (!scheduleAddDialogFragment.isAdded && !scheduleAddDialogFragment.isVisible) {
            scheduleAddDialogFragment.show(manager, "ScheduleAddDialogFragment")
        }
    }

    private inner class ScheduleAddDialogFragmentListenerImpl :
        ScheduleAddDialogFragment.ScheduleAddDFListener {
        override fun save() {

        }
    }
}