package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.appcompat.widget.TintTypedArray
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import com.inz.z.base.R
import com.inz.z.base.util.L

/**
 * base scroll view. have [headerLlayout] and [topFloagLayout]
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/24 14:17.
 */
class BaseScrollView : LinearLayout {

    companion object {
        private const val TAG = "BaseScrollView"
    }

    private lateinit var mContext: Context
    private var mView: View? = null

    /**
     * root view
     */
    private var contentHeaderLayoutView: LinearLayout? = null
    private var topFloatLayoutView: LinearLayout? = null
    private var contentTopNavLayoutView: LinearLayout? = null
    private var nesContentLayout: LinearLayout? = null
    private var contentBodyLayoutView: LinearLayout? = null
    private var haveTopFloatView = false
    private var topFloatView: View? = null
    private var headerView: View? = null
    private var contentView: View? = null

    @IdRes
    private var topFloatViewId: Int = 0

    @IdRes
    private var headerViewId: Int = 0

    @IdRes
    private var contentViewId: Int = 0

    private var contentNestedScrollView: NestedScrollView? = null

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
        val tint =
            TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.BaseScrollView, 0, 0)

        haveTopFloatView =
            tint.getBoolean(R.styleable.BaseScrollView_base_scroll_view_show_top_float_view, false)

        headerViewId =
            tint.getResourceId(R.styleable.BaseScrollView_base_scroll_view_header_view_id, 0)
        topFloatViewId =
            tint.getResourceId(R.styleable.BaseScrollView_base_scroll_view_top_float_view_id, 0)

        contentViewId =
            tint.getResourceId(R.styleable.BaseScrollView_base_scroll_view_content_view_id, 0)

        tint.recycle()

    }

    private fun initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.base_scroll_view, this, true)

            mView!!.let {
                contentHeaderLayoutView = it.findViewById(R.id.base_scroll_view_content_header_ll)
                topFloatLayoutView = it.findViewById(R.id.base_scroll_view_top_float_ll)
                contentTopNavLayoutView = it.findViewById(R.id.base_scroll_view_content_nav_ll)
                contentNestedScrollView = it.findViewById(R.id.base_scroll_view_content_nsv)
                contentNestedScrollView?.apply {
                    setOnScrollChangeListener(NestedScrollChangeImpl())
                }
                nesContentLayout = it.findViewById(R.id.base_scroll_view_nsv_content_ll)
                contentBodyLayoutView = it.findViewById(R.id.base_scroll_view_content_body_ll)
            }
        }
    }


    inner class NestedScrollChangeImpl : NestedScrollView.OnScrollChangeListener {
        override fun onScrollChange(
            v: NestedScrollView?,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            val loc = intArrayOf(0, 0)
            contentTopNavLayoutView?.getLocationOnScreen(loc)
            val locP = intArrayOf(0, 0)
            contentNestedScrollView?.getLocationOnScreen(locP)

            val nesAtTop = loc[1] - locP[1] <= 0;
            L.i(TAG, "scroll change-- top ${locP[1]} x = ${loc[0]} , ${loc[1]} ")
            val floatView = nesAtTop && haveTopFloatView
            setTopFloatView(floatView, "scroll change ")

        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onFinishInflate() {
        L.i(TAG, "onFinishInflate---")
        super.onFinishInflate()
        setContentView()
        setHeaderView()
        setTopFloatView(false, "finish inflate ")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val lp: ViewGroup.LayoutParams? = contentTopNavLayoutView?.layoutParams
        if (lp != null && topFloatView != null) {
            lp.height = topFloatView!!.height
            contentTopNavLayoutView?.layoutParams = lp
        }

    }

    /**
     * add Header view
     */
    private fun setHeaderView() {
        if (headerViewId != 0) {
            val view: View = findViewById(headerViewId) ?: return
            removeView(view)
            contentHeaderLayoutView?.removeView(view)
            contentHeaderLayoutView?.addView(view)
            headerView = view
        }
    }

    /**
     * set top nav layout
     */
    private fun setTopFloatView(floatView: Boolean, tag: String) {
        L.i(TAG, "setTopFloatView: $floatView -- $tag")
        if (topFloatViewId != 0) {
            val view: View = findViewById(topFloatViewId) ?: return
            removeView(view)
            topFloatLayoutView?.removeAllViews()
            contentTopNavLayoutView?.removeAllViews()
            if (floatView) {
                contentTopNavLayoutView?.visibility = View.INVISIBLE

                topFloatLayoutView?.visibility = View.VISIBLE
                topFloatLayoutView?.addView(view)
            } else {
                contentTopNavLayoutView?.visibility = View.VISIBLE
                contentTopNavLayoutView?.addView(view)

                topFloatLayoutView?.visibility = View.GONE
            }
            topFloatView = view
        }
    }

    /**
     * set content view
     */
    private fun setContentView() {
        if (contentViewId != 0) {
            val view: View = findViewById(contentViewId) ?: return
            removeView(view)
            contentBodyLayoutView?.removeAllViews()
            contentBodyLayoutView?.addView(view)
            contentView = view
        }
    }
}