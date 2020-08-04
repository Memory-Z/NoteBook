package com.inz.z.base.view.dialog

import androidx.fragment.app.DialogFragment
import com.inz.z.base.R
import com.inz.z.base.view.AbsBaseDialogFragment

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/04 14:18.
 */
class UpdateVersionDialog : AbsBaseDialogFragment() {
    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Base_Dialog_Center)
    }

    override fun getLayoutId(): Int {
        return R.layout.base_icon_layout;
    }

    override fun initView() {

    }

    override fun initData() {

    }
}