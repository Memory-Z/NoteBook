package com.inz.z.note_book.view.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.FragmentContentType
import com.inz.z.note_book.databinding.ActivityBaseAddContentBinding
import com.inz.z.note_book.util.Constants

/**
 *
 * 基础 添加 内容 弹窗。
 * ====================================================
 * Create by 11654 in 2021/10/24 15:16
 */
class BaseAddContentDialogFragment : AbsBaseDialogFragment() {
    companion object {
        private const val TAG = "BaseAddContentDialogFra"

        /**
         * 获取 实例
         * @param contentType 显示内容
         * @param linkedId 标签 关联 ID ： 如： ScheduleId, NoteId
         */
        fun getInstant(
            @FragmentContentType contentType: Int,
            linkedId: String?
        ): BaseAddContentDialogFragment {
            val bundle = Bundle()
            bundle.putInt(Constants.FragmentParams.PARAMS_CONTENT_TYPE, contentType)
            bundle.putString(Constants.FragmentParams.PARAMS_TAG_LINK_ID, linkedId)
            val fragment = BaseAddContentDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var activityBaseAddContentBinding: ActivityBaseAddContentBinding? = null

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteBookAppTheme_Dialog)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_base_add_content
    }

    override fun initView() {

    }

    override fun initData() {

    }
}