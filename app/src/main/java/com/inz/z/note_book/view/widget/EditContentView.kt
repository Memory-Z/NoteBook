package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Scroller
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.inz.z.base.util.L
import com.inz.z.note_book.R

/**
 * 内容编辑视图
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/22 16:21.
 */
class EditContentView : NestedScrollView {

    companion object {
        private const val TAG = "EditContentView"
    }

    private var mView: View? = null
    private var mContext: Context
    private lateinit var scroller: Scroller
    private lateinit var velocityTracker: VelocityTracker
    private lateinit var mRootLayout: LinearLayout

    private var currentCursorPosition: Int = 0

    private var oldFocusedEditText: EditText? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initStyle(attrs)
        initView()
    }

    @SuppressLint("RestrictedApi")
    private fun initStyle(attrs: AttributeSet?) {
        val array = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.EditContentView,
            0,
            0
        )

        array.recycle()
    }

    private fun initView() {
        scroller = Scroller(mContext)
        velocityTracker = VelocityTracker.obtain()
        mRootLayout = LinearLayout(mContext)
        mRootLayout.apply {
            this.orientation = LinearLayout.VERTICAL
            this.gravity = Gravity.TOP.or(Gravity.CENTER_HORIZONTAL)
            this.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        }
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(mRootLayout, lp)
    }


    override fun addView(child: View?) {
        if (childCount == 0) {
            super.addView(child)
        } else {
            mRootLayout.addView(child)
        }
    }

    override fun addView(child: View?, index: Int) {
        if (childCount == 0) {
            super.addView(child, index)
        } else {
            mRootLayout.addView(child, index)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (childCount == 0) {
            super.addView(child, params)
        } else {
            mRootLayout.addView(child, params)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (childCount == 0) {
            super.addView(child, index, params)
        } else {
            mRootLayout.addView(child, index, params)
        }
    }

    private fun addLayoutView(view: View) {
        mRootLayout.addView(view)
    }


    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        L.i(TAG, "onInterceptTouchEvent: --- >>>>>>>>>>>>>>>>>>>>>>>>>> ${ev?.action}")
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val x = it.rawX.toInt() - mRootLayout.left
                val y = it.rawY.toInt() - mRootLayout.top
                L.i(TAG, "onInterceptTouchEvent:  [ $x , $y ] <<----")
                val view = findClickView(x, y, mRootLayout)
                L.i(TAG, "onInterceptTouchEvent: ----- ${view} ==== $mRootLayout")
                if (view != mRootLayout) {
                    targetFocusedEditText(view, "ON_INTERCEPT_TOUCH_EVENT")
                }
            }
        }
        val intercept = super.onInterceptTouchEvent(ev)
        L.i(TAG, "onInterceptTouchEvent -- ${intercept} >>>>  ${ev?.action} : ")
        return intercept
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        val dispatch = super.dispatchTouchEvent(ev)
        L.i(TAG, "dispatchTouchEvent:  --- $dispatch")
        return dispatch
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        L.i(TAG, "onTouchEvent: --- > ${ev?.action}")
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val x = it.rawX.toInt() - mRootLayout.left
                val y = it.rawY.toInt() - mRootLayout.top
                var view = findClickView(x, y, mRootLayout)
                L.i(TAG, "onTouchEvent: ----------- ${view} ==== $mRootLayout")
                if (view is EditText) {
                    return false
                }
                if (view is ViewGroup && view == mRootLayout) {
                    view = findLastViewGroupEditText(view)
                    targetFocusedEditText(view, "ON_TOUCH_EVENT")
                    return true
                }
            }
        }
        return super.onTouchEvent(ev)
    }


    /**
     * 获取点击的 View
     */
    private fun findClickView(x: Int, y: Int, viewGroup: ViewGroup): View {
        val count = viewGroup.childCount
        L.i(TAG, "findClickView: $viewGroup ==>> [ $x, $y ]")
        for (index in 0 until count) {
            val rect = Rect()
            val childView = viewGroup.getChildAt(index)
            childView.getGlobalVisibleRect(rect)
            L.i(TAG, "findClickView: $childView ==>> $rect")
            if (rect.contains(x, y)) {
                val newX = x - childView.left
                val newY = y - childView.top
                return if (childView is ViewGroup)
                    findClickView(
                        newX,
                        newY,
                        childView
                    )
                else childView
            }
        }
        return viewGroup
    }

    /**
     * 获取点击位置所属的子View .如果 不存在，则获取最后一个View
     */
    private fun childViewClick(x: Int, y: Int, viewGroup: ViewGroup): View {
        val count = viewGroup.childCount
        for (index in 0 until count) {
            val view = viewGroup.getChildAt(index)
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            L.i(TAG, "childViewClick: ${view} === $rect")
            if (rect.contains(x, y)) {
                if (view is ViewGroup) {
                    val nX = x - view.left
                    val nY = y - view.top
                    return childViewClick(nX, nY, view)
                } else {
                    return view
                }
            }
        }
        var lastView: View? = null

        if (count > 0) {
            lastView = viewGroup.getChildAt(count - 1)
        }
        return lastView ?: viewGroup
    }

    private fun findLastViewGroupEditText(viewGroup: ViewGroup): View {
        val count = viewGroup.childCount
        for (index in (count - 1) downTo 0) {
            val child = viewGroup.getChildAt(index)
            when (child) {
                is EditText -> {
                    return child
                }
                is ViewGroup -> {
                    return findLastViewGroupEditText(child)
                }
            }
        }
        if (count > 0) {
            return viewGroup.getChildAt(count - 1)
        } else {
            return viewGroup
        }
    }

    /**
     * 切换显示
     */
    private fun targetFocusedEditText(view: View, tag: String) {
        L.i(TAG, "targetFocusedEditText: ---> $tag :::  $view <> $oldFocusedEditText")
        if (view != oldFocusedEditText) {
            oldFocusedEditText?.clearFocus()
            oldFocusedEditText = null
            val inputMethodManager =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (view is EditText) {
                view.requestFocus()
                oldFocusedEditText = view
                inputMethodManager?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
                L.i(TAG, "targetFocusedEditText: -------------- SHOW ")
            } else {
                inputMethodManager?.hideSoftInputFromWindow(
                    view.windowToken,
                    0
                )
                L.i(TAG, "targetFocusedEditText: -------------- HIDE ")
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

}