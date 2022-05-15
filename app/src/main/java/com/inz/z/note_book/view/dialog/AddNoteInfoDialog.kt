package com.inz.z.note_book.view.dialog

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import com.inz.z.base.util.KeyBoardUtils
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.NoteInfoAddSampleLayoutBinding
import com.inz.z.note_book.util.ClickUtil

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/12 11:20.
 */
class AddNoteInfoDialog : AppCompatDialog, View.OnClickListener {

    companion object {
        private const val TAG = "AddNoteInfoDialog"

        interface AddNoteInfoListener {
            fun onSubmitClick(v: View?, message: String?)
        }
    }

    var addNoteInfoListener: AddNoteInfoListener? = null

    private var binding: NoteInfoAddSampleLayoutBinding? = null

    constructor(context: Context?) : this(context, R.style.NoteBookAppTheme_Dialog_BottomToTop)
    constructor(context: Context?, theme: Int) : super(context, theme)

    override fun onStart() {
        super.onStart()
        window?.let {
            val rect: Rect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = it.windowManager.currentWindowMetrics
                rect = metrics.bounds
            } else {
                rect = Rect()
                @Suppress("DEPRECATION")
                it.windowManager.defaultDisplay.getRectSize(rect)
            }
            val lp = it.attributes
            lp.gravity = Gravity.BOTTOM.or(Gravity.CENTER_HORIZONTAL)
            lp.width = rect.width()
            it.attributes = lp
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = NoteInfoAddSampleLayoutBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }
        initView()
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        L.i(TAG, "initView: ")
        binding?.let {
            L.i(TAG, "initView:  2  ")
            it.noteInfoAddSampleAddIv.setOnClickListener(this)
            // 默认不可点击 。
            it.noteInfoAddSampleAddIv.isClickable = false
            it.noteInfoAddSampleTitleEt.doAfterTextChanged { editable ->
                L.i(TAG, "initView: setOnEditorActionListener ")
                // 根据写入文本长度判断是否可以进行提交/
                val tintId: Int
                val canClick: Boolean
                if ((editable?.toString()?.length ?: 0) > 0) {
                    tintId = R.color.colorPrimary
                    canClick = true
                } else {
                    tintId = R.color.textGray
                    canClick = false
                }
                it.noteInfoAddSampleAddIv.let { addIv ->
                    addIv.isClickable = canClick
                    addIv.backgroundTintList = context.getColorStateList(tintId)
                }
            }
        }
    }

    override fun dismiss() {
        binding?.noteInfoAddSampleTitleEt?.clearFocus()
        binding?.let {
            KeyBoardUtils.hideKeyBoardByWindowToken(it.root.context, it.root.windowToken)
        }
        super.dismiss()
    }

    override fun onStop() {
        super.onStop()
        binding?.noteInfoAddSampleTitleEt?.clearFocus()
        binding = null
        this.addNoteInfoListener = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            return
        }
        binding?.let {
            when (v?.id) {
                // 提交。
                it.noteInfoAddSampleAddIv.id -> {
                    addNoteInfoListener?.onSubmitClick(
                        v,
                        it.noteInfoAddSampleTitleEt.text.toString()
                    )
                }
                else -> {

                }
            }
        }
    }

    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams?) {
        super.onWindowAttributesChanged(params)
    }

}