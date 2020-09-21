package com.inz.z.note_book.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.TintTypedArray
import androidx.constraintlayout.widget.ConstraintLayout
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.float_view_message_base.view.*
import java.util.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/18 09:39.
 */
class FloatMessageBaseView : ConstraintLayout {
    companion object {
        private const val TAG = "FloatMessageBaseView"
    }

    private var mContext: Context? = null
    private var mView: View? = null

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

    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val tint = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.FloatMessageBaseView,
            0,
            0
        )
        tint.recycle()
    }

    private fun initView() {
        if (mView == null) {
            mView =
                LayoutInflater.from(mContext).inflate(R.layout.float_view_message_base, this, true)
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar[Calendar.HOUR_OF_DAY] = 18
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            float_message_base_crv.start(
                CountdownRingView.MODE_COUNT_TIME_FIXED,
                calendar.timeInMillis
            )
        }
    }

    interface FloatMessageBaseViewListener {

    }

    var floatMessageBaseViewListener: FloatMessageBaseViewListener? = null
}