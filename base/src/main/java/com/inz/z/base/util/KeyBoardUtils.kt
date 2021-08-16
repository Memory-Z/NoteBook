package com.inz.z.base.util

import android.app.Activity
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 软件盘 工具
 *
 * ====================================================
 * Create by 11654 in 2021/6/9 7:27
 */
object KeyBoardUtils {
    private fun getInputMethodManager(context: Context): InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    /**
     * 显示键盘
     * @param editText 编辑框
     */
    fun showKeyBoard(editText: EditText) {
        val manager = getInputMethodManager(editText.context)
        manager?.showSoftInput(editText, 0)
    }

    fun hidKeyBoard(editText: EditText) {
        hideKeyBoardByWindowToken(editText.context, editText.windowToken)
    }

    fun hideKeyBoardByWindowToken(context: Context, windowToken: IBinder) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}