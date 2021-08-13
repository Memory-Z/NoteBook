package com.inz.z.note_book.view.fragment.add_content

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.base.view.widget.TagLayout
import com.inz.z.note_book.R
import com.inz.z.note_book.view.widget.add_content.TagEditText

/**
 *
 * 笔记标签 Fragment
 * ====================================================
 * Create by 11654 in 2021/4/17 16:40
 */
class NoteTagFragment private constructor() : AbsBaseFragment() {

    companion object {
        private const val TAG = "NoteTagFragment"

        fun getInstance(): NoteTagFragment {
            val fragment = NoteTagFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var editTagLl: LinearLayout? = null

    /**
     * 可使用的 TagLayout 与编辑同行
     */
    private val usableTagLayoutList: MutableList<TagLayout> = mutableListOf()

    /**
     * 可选择的 TagLayout 位于底部
     */
    private val canChooseTagLayoutList: MutableList<TagLayout> = mutableListOf()

    /**
     * 当前 可编辑 TagLayout
     */
    private var currentTagLayout: TagLayout? = null

    /**
     * 编辑行 LayoutParams.
     */
    private var editableLayoutParams: LinearLayout.LayoutParams? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_ac_note_tag
    }

    override fun initView() {
        val hsv: HorizontalScrollView = mView.findViewById(R.id.fm_ac_note_info_hsv)
        hsv.apply {
            this.isClickable = true
            this.setOnClickListener {
//                currentTagEt?.onTouchOut()
                // TODO: 2021/5/28 Add new Tag Layout
                // 判断是否编辑完成， 添加 新TagLayout
                addNewEditableTagLayout()
            }
        }

        editTagLl = mView.findViewById(R.id.fm_ac_note_info_used_ll)

        editableLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
            .apply {
                this.gravity = Gravity.CENTER
                this.marginStart = resources.getDimensionPixelOffset(R.dimen.margin_text)
            }
    }

    override fun initData() {
        // 默认添加新TagLayout.
        addNewEditableTagLayout()
    }

    /**
     * 添加新可编辑TagLayout.
     *
     */
    private fun addNewEditableTagLayout() {
        // 如果 当前 TagLayout 不为空 不存在内容，返回，不进行新TagLayout 添加，
        if (currentTagLayout != null && currentTagLayout?.haveContent() == false) {
            L.i(TAG, "addNewEditableTagLayout: this tag have not content. ")
            return
        }
        val tagLayout = TagLayout(mContext)
        tagLayout.tagLayoutListener = TagLayoutListenerImpl()

        currentTagLayout = tagLayout
        currentTagLayout?.requestFocusFromTouch()
        // 界面
        editTagLl?.addView(tagLayout, editableLayoutParams)
        L.i(TAG, "addNewEditableTagLayout: >>> add View. ")
    }


    /**
     * 移除TagLayout.
     */
    private fun removeTagLayout(view: TagLayout?) {
        editTagLl?.removeView(view)
    }

    /**
     * TagLayout 监听实现。
     */
    private inner class TagLayoutListenerImpl : TagLayout.TagLayoutListener {
        override fun onKeyDone() {
            addNewEditableTagLayout()
        }

        override fun onClickClose(v: TagLayout?) {
            removeTagLayout(v)
        }

        override fun haveFocus(view: View?, hasFocus: Boolean) {
            // 失去焦点，判断是否存在内容，不存在时，移除TagLayout.
            if (!hasFocus) {
                currentTagLayout?.let {
                    if (!it.haveContent()) {
                        removeTagLayout(it)
                    }
                }
            }
        }
    }
}