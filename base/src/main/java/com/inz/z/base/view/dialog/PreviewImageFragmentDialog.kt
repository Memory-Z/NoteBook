package com.inz.z.base.view.dialog

import android.os.Bundle
import com.inz.z.base.R
import com.inz.z.base.view.AbsBaseDialogFragment

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/14 15:40.
 */
class PreviewImageFragmentDialog private constructor() : AbsBaseDialogFragment() {

    companion object {
        private const val TAG = "PreviewImageFragmentDialog"

        fun getInstant(): PreviewImageFragmentDialog {
            val dialog = PreviewImageFragmentDialog()
            val bundle = Bundle()
            dialog.arguments = bundle
            return dialog
        }
    }


    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.base_dialog_preview_image
    }

    override fun initView() {

    }

    override fun initData() {

    }
}