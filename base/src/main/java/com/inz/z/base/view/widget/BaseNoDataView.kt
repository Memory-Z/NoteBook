package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.TintTypedArray
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.inz.z.base.R
import com.inz.z.base.databinding.BaseNoDataLayoutBinding

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
    private var showRefreshBtn: Boolean = false
    private var refreshBtnStr: String? = ""
    private var binding: BaseNoDataLayoutBinding? = null

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
            binding = BaseNoDataLayoutBinding.inflate(LayoutInflater.from(mContext), this, true)
            mView = binding?.root
            binding?.baseNoDataBtn?.setOnClickListener {
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
        showRefreshBtn =
            array.getBoolean(R.styleable.BaseNoDataView_base_no_data_show_refresh_btn, false)
        refreshBtnStr = array.getString(R.styleable.BaseNoDataView_base_no_data_button_str)

        array.recycle()
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


    fun setTitle(titleStr: String) {
        binding?.baseNoDataTitleTv?.text = titleStr
        binding?.baseNoDataTitleTv?.visibility =
            if (TextUtils.isEmpty(title)) GONE else VISIBLE
    }

    fun setMessage(message: String) {
        binding?.baseNoDataMessageTv?.text = message
    }

    fun setHintImage(@DrawableRes imageRes: Int) {
        binding?.baseNoDataHintIv?.setImageResource(imageRes)
    }

    /**
     * 是否显示刷新按钮
     */
    fun showRefreshBtn(show: Boolean) {
        binding?.baseNoDataBtn?.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setButtonStr(buttonStr: String) {
        if (!TextUtils.isEmpty(buttonStr)) {
            binding?.baseNoDataBtn?.text = buttonStr
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
        binding?.baseNoDataOperationLl?.apply {
            this.visibility = GONE
        }
        binding?.baseNoDataLoadTv?.text = loadingStr
        binding?.baseNoDataLoadLl?.apply {
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
        binding?.baseNoDataLoadLl?.visibility = GONE
        binding?.baseNoDataMessageTv?.text = hintMessage
        binding?.baseNoDataOperationLl?.visibility = if (canRetry) VISIBLE else GONE
        binding?.baseNoDataClickTv?.visibility = if (useSpannable) VISIBLE else GONE
        binding?.baseNoDataClickTv?.text = spannableStr
        binding?.baseNoDataBtn?.visibility = if (useSpannable) GONE else VISIBLE
        mContext?.apply {
            if (hintImage != 0 && binding?.baseNoDataHintIv != null) {
                Glide.with(this).load(hintImage).into(binding?.baseNoDataHintIv!!)
            }
        }

    }

    fun getButton(): Button? {
        return binding?.baseNoDataBtn
    }


}