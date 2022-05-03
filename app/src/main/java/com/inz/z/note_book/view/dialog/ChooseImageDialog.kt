package com.inz.z.note_book.view.dialog

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.DialogChooseImageBinding
import com.inz.z.note_book.util.ClickUtil

/**
 * 图片选择弹窗
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 10:20.
 */
class ChooseImageDialog : AbsBaseDialogFragment(), View.OnClickListener {
    companion object {
        private const val TAG = "ChooseImageDialog"


        fun getInstance(): ChooseImageDialog {
            val dialog = ChooseImageDialog()
            val bundle = Bundle()
            dialog.arguments = bundle
            return dialog
        }
    }

    /**
     * 监听。/
     */
    interface ChooseImageDialogListener {
        /**
         * 从相册选择照片
         */
        fun chooseImageFromAlbum();

        /**
         * 拍照
         */
        fun takePicture()
    }

    var dialogListener: ChooseImageDialogListener? = null

    private var binding: DialogChooseImageBinding? = null

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoteBookAppTheme_Dialog_BottomToTop)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_choose_image
    }

    override fun useViewBinding(): Boolean = true

    override fun getViewBindingView(): View? {
        binding = DialogChooseImageBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {
        binding?.let {
            it.dialogChooseImageAlbumTv.setOnClickListener(this)
            it.dialogChooseImageCameraTv.setOnClickListener(this)
            it.dialogChooseImageCancelTv.setOnClickListener(this)
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

    override fun onDestroy() {
        super.onDestroy()
        dialogListener = null
        binding = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            return
        }
        binding?.let {
            when (v?.id) {
                it.dialogChooseImageAlbumTv.id -> {
                    dialogListener?.chooseImageFromAlbum()
                }
                it.dialogChooseImageCameraTv.id -> {
                    dialogListener?.takePicture()
                }
                it.dialogChooseImageCancelTv.id -> {}
                else -> {
                    return
                }
            }
            dismissAllowingStateLoss()
        }
    }
}