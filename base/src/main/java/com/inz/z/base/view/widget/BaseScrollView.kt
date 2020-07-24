package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
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
    private var rootView: NestedScrollView? = null
    private var headerLayoutView: RelativeLayout? = null
    private var topFloatLayoutView: LinearLayout? = null
    private var topNavLayoutView: LinearLayout? = null
    private var nesContentLayout: LinearLayout? = null
    private var rootLayoutView: ConstraintLayout? = null
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
                rootView = it.findViewById(R.id.base_scroll_view_root_nsv)
                rootLayoutView = it.findViewById(R.id.base_scroll_view_parent_content_cl);
                headerLayoutView = it.findViewById(R.id.base_scroll_view_top_header_rl)
                topFloatLayoutView = it.findViewById(R.id.base_scroll_view_top_float_ll)
                topNavLayoutView = it.findViewById(R.id.base_scroll_view_top_nav_ll)
                contentNestedScrollView = it.findViewById(R.id.base_scroll_view_content_nsv)
                nesContentLayout = it.findViewById(R.id.base_scroll_view_nsv_content_ll)
            }
        }
    }


    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val contentNsvTopX = contentNestedScrollView?.top ?: 0
        val nesAtTop = contentNsvTopX == 0;
        val floatView = !nesAtTop && haveTopFloatView
        setTopFloatView(floatView)
        return !nesAtTop
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onFinishInflate() {
        L.i(TAG, "onFinishInflate---")
        super.onFinishInflate()
        setHeaderView()
        setTopFloatView(false)
        setContentView()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        L.i(TAG, "onLayout --- ")
        val layoutHeight = rootView?.height ?: (b - t)
        val lp: ViewGroup.LayoutParams? = rootLayoutView?.layoutParams
        if (lp != null) {
            lp.height = layoutHeight
            rootLayoutView?.layoutParams = lp
        }




    }

    /**
     * add Header view
     */
    private fun setHeaderView() {
        if (headerViewId != 0) {
            val view: View = findViewById(headerViewId) ?: return
            removeView(view)
            headerLayoutView?.removeView(view)
            headerLayoutView?.addView(view)
            headerView = view
        }
    }


    /**
     * set top nav layout
     */
    private fun setTopFloatView(floatView: Boolean) {
        if (topFloatViewId != 0) {
            val view: View = findViewById(topFloatViewId) ?: return
            removeView(view)
            topFloatLayoutView?.removeAllViews()
            topNavLayoutView?.removeAllViews()
            if (floatView) {
                topNavLayoutView?.visibility = View.GONE

                topFloatLayoutView?.visibility = View.VISIBLE
                topFloatLayoutView?.addView(view)
            } else {
                topNavLayoutView?.visibility = View.VISIBLE
                topNavLayoutView?.addView(view)

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
            nesContentLayout?.removeAllViews()
            nesContentLayout?.addView(view)
            contentView = view
        }
    }
}