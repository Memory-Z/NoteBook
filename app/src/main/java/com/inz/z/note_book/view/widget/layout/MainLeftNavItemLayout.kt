package com.inz.z.note_book.view.widget.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.inz.z.base.view.widget.BaseNavLayout
import com.inz.z.note_book.R

/**
 *
 * 主页左侧 导航栏项
 * ====================================================
 * Create by 11654 in 2021/10/11 21:33
 */
class MainLeftNavItemLayout : LinearLayout {

    companion object {
        private const val TAG = "MainLeftNavItemLayout"
    }

    private var mContext: Context? = null
    private var mView: View? = null

    private var rootBaseNavLayout: BaseNavLayout? = null

    /**
     * 左侧图标
     */
    private var leftIconIv: ImageView? = null

    /**
     * 中间显示文字
     */
    private var contentTv: TextView? = null

    /**
     * 右侧显示内容
     */
    private var rightContentLl: LinearLayout? = null


    private var leftIconDrawable: Drawable? = null
    private var titleStr = ""
    private var rightLayoutId = -1

    /**
     * 最后显示的 View 排序
     */
    private var lastShowViewOrder = 2

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
        initViewStyle(attrs)
    }

    /**
     * 初始化 设置参数
     */
    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val array = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.MainLeftNavItemLayout,
            0,
            0
        )
        val drawable = array.getDrawable(R.styleable.MainLeftNavItemLayout_icon)
        drawable?.let {
            setIconDrawable(it)
        }

        val titleStr = array.getString(R.styleable.MainLeftNavItemLayout_title)
        if (!TextUtils.isEmpty(titleStr)) {
            setTitle(titleStr)
        }
        rightLayoutId = array.getResourceId(R.styleable.MainLeftNavItemLayout_right_layout_id, -1)
        array.recycle()
    }

    /**
     * 初始化View
     */
    private fun initView() {
        if (mView == null) {
            mView =
                LayoutInflater.from(mContext).inflate(R.layout.item_main_left_nav_base, this, true)
            mView?.let {
                rootBaseNavLayout = it.findViewById(R.id.item_main_left_nav_base_bnl)
                leftIconIv = it.findViewById(R.id.item_main_left_nav_base_left_iv)
                contentTv = it.findViewById(R.id.item_main_left_nav_base_content_tv)
                rightContentLl = it.findViewById(R.id.item_main_left_nav_base_right_ll)

                lastShowViewOrder = rootBaseNavLayout?.indexOfChild(rightContentLl) ?: 2

            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // 设置右侧布局
        setRightLayout()
        // 移除多余布局
        removeOtherView()
    }

    /**
     * 设置 右侧显示内容
     */
    private fun setRightLayout() {
        // 判断是否 有设置 对应 右侧内容。
        if (rightLayoutId != -1) {
            val rightLayout: View? = mView?.findViewById(rightLayoutId)
            rightLayout?.let {
                rootBaseNavLayout?.removeView(it)
                rightContentLl?.addView(it)
            }
        }
    }

    /**
     * 移除 多余 布局内容
     */
    private fun removeOtherView() {
        // 默认 只有一个子View， 删除多余View
        val count = this.childCount
        if (count > 1) {
            this.removeViews(1, count - 1)
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Set Title.
     */
    fun setTitle(title: String?) {
        this.titleStr = title ?: ""
        contentTv?.text = title
    }

    /**
     * Set Icon Drawable Icon.
     */
    fun setIconDrawableRes(@DrawableRes resId: Int) {
        mContext?.let {
            val drawable = ContextCompat.getDrawable(it, resId)
            drawable?.apply {
                setIconDrawable(this)
            }
        }
    }

    /**
     * Set Icon Drawable.
     */
    fun setIconDrawable(drawable: Drawable) {
        this.leftIconDrawable = drawable
        this.leftIconIv?.setImageDrawable(drawable)
    }
}
