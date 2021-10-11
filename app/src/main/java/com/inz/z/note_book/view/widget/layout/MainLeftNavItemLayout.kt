package com.inz.z.note_book.view.widget.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.TintTypedArray
import androidx.core.widget.ImageViewCompat
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

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        this.mContext = context
        initViewStyle(attrs)
        initView()
    }

    /**
     * 初始化 设置参数
     */
    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val array = TintTypedArray.obtainStyledAttributes(mContext,
            attrs,
            R.styleable.MainLeftNavItemLayout,
            0,
            0)
        leftIconDrawable = array.getDrawable(R.styleable.MainLeftNavItemLayout_icon)
        titleStr = array.getString(R.styleable.MainLeftNavItemLayout_title)
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
        leftIconIv?.setImageDrawable(leftIconDrawable)
        contentTv?.text = titleStr
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
        // 获取 当前 View 子View 个数， 如果大于需要显示的 个数，移除其他内容，
        val childSize = rootBaseNavLayout?.childCount ?: 0
        if (childSize > lastShowViewOrder) {
            // 移除不需要显示的布局内容
            rootBaseNavLayout?.removeViews(lastShowViewOrder + 1, childSize - lastShowViewOrder - 1)
        }
    }
}
