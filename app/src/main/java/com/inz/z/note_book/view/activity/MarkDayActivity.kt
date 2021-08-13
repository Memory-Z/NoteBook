package com.inz.z.note_book.view.activity

import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import kotlinx.android.synthetic.main.activity_mark_day.*

/**
 * 纪念日。
 *
 * ====================================================
 * Create by 11654 in 2021/6/20 16:12
 */
class MarkDayActivity: BaseNoteActivity() {

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mark_day
    }

    override fun initView() {
        setSupportActionBar(mark_day_top_action_btal.toolbar)
    }

    override fun initData() {

    }
}