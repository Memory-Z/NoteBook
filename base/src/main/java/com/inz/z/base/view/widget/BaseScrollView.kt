package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.appcompat.widget.TintTypedArray
import androidx.core.widget.NestedScrollView
import com.inz.z.base.R

/**
 * base scroll view. have [#headerLlayout] and [#topFloagLayout]
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/24 14:17.
 */
class BaseScrollView : LinearLayout {

    private var mContext: Context
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
            val locP = intArrayOf(0, 0)
            contentNestedScrollView?.getLocationOnScreen(locP)
            val loc = intArrayOf(0, 0)
            contentTopNavLayoutView?.getLocationOnScreen(loc)
            val nesAtTop = loc[1] - locP[1] <= 0;
            val floatView = nesAtTop && haveTopFloatView
            showTopFloatView(floatView)
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setContentView()
        setHeaderView()
        setTopFloatView()
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
        var view: View? = null
        if (headerView != null) {
            if (headerView!!.parent == null) {
                view = headerView
            }
        } else {
            if (headerViewId != 0) {
                view = findViewById(headerViewId) ?: return
                removeView(view)
            }
        }
        view?.let {
            contentHeaderLayoutView?.removeView(view)
            contentHeaderLayoutView?.addView(view)
            headerView = view
        }

    }

    /**
     * set top nav layout
     */
    private fun setTopFloatView() {
        var view: View? = null
        if (topFloatView != null) {
            if (topFloatView!!.parent == null) {
                view = topFloatView
            }
        } else {
            if (topFloatViewId != 0) {
                view = findViewById(topFloatViewId) ?: return
                removeView(view)
            }
        }
        view?.let {
            topFloatLayoutView?.removeAllViews()
            contentTopNavLayoutView?.removeAllViews()
            contentTopNavLayoutView?.visibility = View.VISIBLE
            contentTopNavLayoutView?.addView(it)

            topFloatLayoutView?.visibility = View.GONE
            topFloatView = it
        }

    }

    /**
     * change top nav visibility
     */
    private fun showTopFloatView(visibility: Boolean) {
        topFloatView?.let {
            topFloatLayoutView?.removeAllViews()
            contentTopNavLayoutView?.removeAllViews()
            if (visibility) {
                contentTopNavLayoutView?.visibility = View.INVISIBLE

                topFloatLayoutView?.visibility = View.VISIBLE
                topFloatLayoutView?.addView(topFloatView)
            } else {
                contentTopNavLayoutView?.visibility = View.VISIBLE
                contentTopNavLayoutView?.addView(topFloatView)

                topFloatLayoutView?.visibility = View.GONE
            }
        }
    }

    /**
     * set content view
     */
    private fun setContentView() {
        var view: View? = null
        if (contentView != null) {
            if (contentView!!.parent == null) {
                view = contentView
            }
        } else {
            if (contentViewId != 0) {
                view = findViewById(contentViewId) ?: return
                removeView(view)
            }
        }
        view?.let {
            contentBodyLayoutView?.removeAllViews()
            contentBodyLayoutView?.addView(view)
            contentView = view
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外接口
    ///////////////////////////////////////////////////////////////////////////

    /**
     * set header view
     */
    fun setHeaderViewId(@NonNull @IdRes headerViewId: Int) {
        this.headerViewId = headerViewId
        setHeaderView()
    }

    /**
     * set header view
     */
    fun setHeaderView(headerView: View) {
        this.headerView = headerView
        setHeaderView()
    }

    /**
     * set top nav view
     */
    fun setTopNavViewId(@NonNull @IdRes topNavLayoutId: Int, showFloatNav: Boolean) {
        this.topFloatViewId = topNavLayoutId;
        this.haveTopFloatView = showFloatNav
        setTopFloatView()
    }

    /**
     * set top nav view
     */
    fun setTopNavView(topNavView: View, showFloatNav: Boolean) {
        this.topFloatView = topNavView
        this.haveTopFloatView = showFloatNav
        setTopFloatView()

    }


    /**
     * set content view
     */
    fun setContentViewId(@NonNull @IdRes contentLayoutId: Int) {
        this.contentViewId = contentLayoutId
        setContentView()
    }

    /**
     * set content view
     */
    fun setContentView(contentView: View) {
        this.contentView = contentView
        setContentView()

    }


}