package com.inz.z.note_book.view.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.view.fragment.BaseDialogFragment
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

        fun getInstance(): ChooseImageDialog {
            val dialog = ChooseImageDialog()
            val bundle = Bundle()
            dialog.arguments = bundle
            return dialog
        }
    }


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
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivity(intent)
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