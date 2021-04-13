package com.inz.z.note_book.view.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity

/**
 *
 * 基本添加内容 Activity
 * ====================================================
 * Create by 11654 in 2021/4/11 21:12
 */
class AddContentActivity : BaseNoteActivity() {

    companion object {
        fun getInstanceBundle(@LayoutRes contentId: Int): Bundle {
            val bundle = Bundle()

            return bundle
        }
    }

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_base_add_content
    }

    override fun initView() {

    }

    override fun initData() {

    }
}