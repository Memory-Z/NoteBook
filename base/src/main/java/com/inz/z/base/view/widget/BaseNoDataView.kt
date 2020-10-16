package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.text.SpannableString
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.TintTypedArray
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.inz.z.base.R
import kotlinx.android.synthetic.main.base_no_data_layout.view.*

/**
 *
 * 空界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/09 14:00.
 */
public open class BaseNoDataView : ConstraintLayout {
    companion object {
        private const val TAG = "BaseNoDataView"
    }

    var listener: BaseNoDataListener? = null

    private var mContext: Context? = null
    private var mView: View? = null
    private var title: String? = ""
    private var message: String? = ""
    private var refreshBtnStr: String? = ""

    @DrawableRes
    private var imageRes: Int? = null


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
        initViewStyle(attrs)
        initView()
    }

    private fun initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.base_no_data_layout, this, true)
            mView?.base_no_data_btn?.setOnClickListener {
                listener?.onRefreshButtonClick(it)
            }

            setTitle(title ?: "")
            setMessage(message ?: "")

            if (imageRes != null) {
                setHintImage(imageRes!!)
            }
            setButtonStr(refreshBtnStr ?: "")
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val array =
            TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.BaseNoDataView, 0, 0)
        imageRes = array.getResourceId(
            R.styleable.BaseNoDataView_base_no_data_image_src,
            R.drawable.image_load_error
        )
        title = array.getString(R.styleable.BaseNoDataView_base_no_data_title)
        if (TextUtils.isEmpty(title)) {
            title = ""
        }
        message = array.getString(R.styleable.BaseNoDataView_base_no_data_message)

        refreshBtnStr = array.getString(R.styleable.BaseNoDataView_base_no_data_button_str)

        array.recycle()
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


    fun setTitle(titleStr: String) {
        mView?.base_no_data_title_tv?.text = titleStr
        mView?.base_no_data_title_tv?.visibility =
            if (TextUtils.isEmpty(title)) GONE else VISIBLE
    }

    fun setMessage(message: String) {
        mView?.base_no_data_message_tv?.text = message
    }

    fun setHintImage(@DrawableRes imageRes: Int) {
        mView?.base_no_data_hint_iv?.setImageResource(imageRes)
    }

    fun setButtonStr(buttonStr: String) {
        if (!TextUtils.isEmpty(buttonStr)) {
            mView?.base_no_data_btn?.text = buttonStr
        }
    }

    interface BaseNoDataListener {
        fun onRefreshButtonClick(view: View?)
    }

    /**
     * 开始刷新
     * @param loadingStr 刷新提示内容
     */
    fun startRefresh(loadingStr: String) {
        base_no_data_operation_ll?.apply {
            this.visibility = GONE
        }
        base_no_data_load_tv?.text = loadingStr
        base_no_data_load_ll?.apply {
            this.visibility = VISIBLE
        }
    }

    /**
     * 停止刷新
     */
    fun stopRefresh(hintMessage: String, canRetry: Boolean) {
        stopRefresh(hintMessage, canRetry, false, SpannableString(""))
    }

    /**
     * 停止刷新
     */
    fun stopRefresh(
        hintMessage: String,
        canRetry: Boolean,
        useSpannable: Boolean,
        spannableStr: SpannableString
    ) {
        stopRefresh(hintMessage, canRetry, useSpannable, spannableStr, 0)
    }


    /**
     * 停止刷新
     */
    fun stopRefresh(
        hintMessage: String,
        canRetry: Boolean,
        useSpannable: Boolean,
        spannableStr: SpannableString,
        @DrawableRes hintImage: Int
    ) {
        base_no_data_load_ll?.visibility = GONE
        base_no_data_message_tv?.text = hintMessage
        base_no_data_operation_ll?.visibility = if (canRetry) VISIBLE else GONE
        base_no_data_click_tv?.visibility = if (useSpannable) VISIBLE else GONE
        base_no_data_click_tv?.text = spannableStr
        base_no_data_btn?.visibility = if (useSpannable) GONE else VISIBLE
        mContext?.apply {
            if (hintImage != 0 && base_no_data_hint_iv != null) {
                Glide.with(this).load(hintImage).into(base_no_data_hint_iv!!)
            }
        }

    }

    fun getButton(): Button? {
        return mView?.base_no_data_btn
    }


}