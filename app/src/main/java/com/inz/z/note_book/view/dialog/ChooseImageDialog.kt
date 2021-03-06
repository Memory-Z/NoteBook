package com.inz.z.note_book.view.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.base.view.activity.ChooseFileActivity
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.dialog_choose_image.*

/**
 * 图片选择弹窗
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 10:20.
 */
class ChooseImageDialog : AbsBaseDialogFragment() {
    companion object {
        private const val TAG = "ChooseImageDialog"

        private const val REQUEST_CODE_TAG = "requestCode"

        fun getInstance(requestCode: Int): ChooseImageDialog {
            val dialog = ChooseImageDialog()
            val bundle = Bundle()
            bundle.putInt(REQUEST_CODE_TAG, requestCode)
            dialog.arguments = bundle
            return dialog
        }
    }

    private var requestCode = -1

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoteBookAppTheme_Dialog_BottomToTop)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_choose_image
    }

    override fun initView() {
        dialog_choose_image_album_tv?.setOnClickListener {
            pickPhotoFromAlbum()
            dismissAllowingStateLoss()
        }
        dialog_choose_image_cancel_tv?.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun initData() {
        arguments?.apply {
            requestCode = this.getInt(REQUEST_CODE_TAG, -1)
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.apply {
            val lp = this.attributes
            lp.gravity = Gravity.BOTTOM
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            this.attributes = lp
        }
    }

    /**
     * 选择图片 (相册)
     */
    private fun pickPhotoFromAlbum() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        startActivity(intent)
//        ChooseFileActivity.gotoChooseFileActivity(requireActivity(), 800)
        ChooseFileActivity.gotoChooseFileActivity(
            requireActivity(),
            requestCode,
            ChooseFileActivity.MODE_TABLE,
            com.inz.z.base.entity.Constants.ChooseFileConstants.SHOW_TYPE_IMAGE,
            2
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // open
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 选择图片监听
     */
    interface ChooseImageDialogListener {
        /**
         *
         */
        fun startPickFromAlbum(resultCode: Int)
    }
}