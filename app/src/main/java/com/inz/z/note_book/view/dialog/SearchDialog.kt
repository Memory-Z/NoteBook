package com.inz.z.note_book.view.dialog

import android.graphics.Point
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.qmuiteam.qmui.util.QMUIKeyboardHelper
import kotlinx.android.synthetic.main.dialog_search.*

/**
 * 搜素界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/15 10:40.
 */
abstract class SearchDialog : AbsBaseDialogFragment(), TextWatcher {

    companion object {

    }

    var searchDialogListener: SearchDialogListener? = null

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoteBookAppTheme_Dialog)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_search
    }

    override fun initView() {
        dialog_search_top_left_iv.setOnClickListener {
            dismissAllowingStateLoss()
        }
        dialog_search_top_search_et.apply {
            this.addTextChangedListener(this@SearchDialog)
            this.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val search = this.text?.toString() ?: ""
                    searchDialogListener?.onSearchClick(search, v)
                    dismissAllowingStateLoss()
                }
                return@setOnEditorActionListener true
            }

        }

    }

    override fun initData() {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchDialogListener?.onSearchContentChange(s)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val size = Point()
            this.windowManager.defaultDisplay.getRealSize(size)
            val lp = this.attributes
            lp.height = size.y
            lp.width = size.x
            this.attributes = lp
            this.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onResume() {
        super.onResume()
        QMUIKeyboardHelper.showKeyboard(dialog_search_top_search_et, true)
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    interface SearchDialogListener {
        fun onSearchClick(search: String, v: View?)

        fun onSearchContentChange(search: CharSequence?)
    }

}