package com.inz.z.note_book.view.activity

import android.view.View
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityMarkDayBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.view.BaseNoteActivity

/**
 * 纪念日。
 *
 * ====================================================
 * Create by 11654 in 2021/6/20 16:12
 */
class MarkDayActivity : BaseNoteActivity(), View.OnClickListener {

    override fun initWindow() {

    }

    private var binding: ActivityMarkDayBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_mark_day
    }

    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityMarkDayBinding.inflate(layoutInflater)
            .apply {
                setContentView(root)
            }
    }

    override fun initView() {
        setSupportActionBar(binding?.markDayToolbar)
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.w(TAG, "onClick: this is fast click, ignore ! ")
            return
        }

    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        binding = null
    }
}