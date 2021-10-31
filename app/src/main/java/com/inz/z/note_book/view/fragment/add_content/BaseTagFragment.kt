package com.inz.z.note_book.view.fragment.add_content

import android.os.Bundle
import android.view.View
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.FragmentContentType
import com.inz.z.note_book.databinding.FragmentAcBaseTagBinding
import com.inz.z.note_book.util.Constants

/**
 * 默认 标签 界面
 *
 * ====================================================
 * Create by 11654 in 2021/10/31 19:48
 */
class BaseTagFragment : AbsBaseFragment() {
    companion object {
        private const val TAG = "BaseTagFragment"

        /**
         * 获取 实例
         * @param contentType 内容 类型
         * @param linkId 链接 地址
         */
        fun getInstances(@FragmentContentType contentType: Int, linkId: String): BaseTagFragment {

            val bundle = Bundle()
            bundle.putInt(Constants.FragmentParams.PARAMS_CONTENT_TYPE, contentType)
            bundle.putString(Constants.FragmentParams.PARAMS_TAG_LINK_ID, linkId)

            val fragment = BaseTagFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var fragmentAcBaseTagBinding: FragmentAcBaseTagBinding? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_ac_base_tag
    }

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun getViewBindingView(): View {
        return super.getViewBindingView()
    }

    override fun initView() {

    }

    override fun initData() {

    }
}