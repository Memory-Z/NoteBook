package com.inz.z.note_book.view.dialog

import android.graphics.Point
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.base_dialog_layout.*
import org.jetbrains.annotations.Contract
import kotlin.String as String1

/**
 * 弹窗
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/12 13:15.
 */
class BaseDialogFragment private constructor() : AbsBaseDialogFragment() {

    companion object {
        const val TAG = "BaseDialogFragment"

        fun getInstant(builder: Builder): BaseDialogFragment {
            val dialogFragment = BaseDialogFragment()
            dialogFragment.builder = builder
            return dialogFragment
        }
    }

    private var builder: Builder? = null

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteBookAppTheme_Dialog)
    }

    override fun getLayoutId(): Int {
        return R.layout.base_dialog_layout
    }

    override fun initView() {
        if (builder != null) {
            loadBuildLayout(builder!!)
        }
    }

    override fun initData() {
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.apply {
            val point = Point()
            windowManager.defaultDisplay.getRealSize(point)
            val lp = attributes
            lp.width = (point.x * .8F).toInt()
            lp.gravity = Gravity.CENTER
            attributes = lp
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        isCancelable = true
    }

    /**
     * 创建
     */
    class Builder() {

        var title = ""
        var message: SpannableStringBuilder = SpannableStringBuilder("")
        var leftBtn = ""
        var leftListener: View.OnClickListener? = null
        var centerListener: View.OnClickListener? = null
        var rightListener: View.OnClickListener? = null

        var centerBtn = ""
        var rightBtn = ""
        var showIcon = false
        var drawableRes: Int? = null
        var centerView: View? = null

        /**
         * 标题
         */
        fun setTitle(title: String1): Builder {
            this.title = title
            return this@Builder
        }

        /**
         * 中间布局 与 {@link #setCenterView(message)} 冲突，高优先级
         * @see setContentMessage
         */
        fun setCenterView(centerView: View): Builder {
            this.centerView = centerView
            return this@Builder
        }

        /**
         * 中间提示消息 与  {@link #setCenterView(View)} 冲突
         * @see setCenterView
         */
        fun setCenterMessage(message: SpannableStringBuilder): Builder {
            this.message = message
            return this@Builder
        }

        fun setCenterMessage(message: String1): Builder {
            this.message = SpannableStringBuilder(message)
            return this@Builder
        }

        fun setLeftButton(leftBtn: String1, clickListener: View.OnClickListener?): Builder {
            this.leftBtn = leftBtn
            this.leftListener = clickListener
            return this@Builder
        }

        fun setRightButton(rightBtn: String1, clickListener: View.OnClickListener?): Builder {
            this.rightBtn = rightBtn
            this.rightListener = clickListener
            return this@Builder
        }

        fun setCenterButton(centerBtn: String1, clickListener: View.OnClickListener?): Builder {
            this.centerBtn = centerBtn
            this.centerListener = clickListener
            return this@Builder
        }

        fun setTitleIcon(resId: Int): Builder {
            this.drawableRes = resId
            this.showIcon = true
            return this@Builder
        }

        fun build(): BaseDialogFragment {
            return getInstant(this@Builder)
        }
    }

    private fun loadBuildLayout(builder: Builder) {
        if (builder.showIcon) {
            setTitleIcon(builder.showIcon, builder.drawableRes)
        }
        setTitle(if (builder.title.isEmpty()) getString(R.string._tips) else builder.title)
        setContentMessage(builder.message)
        if (builder.centerView != null) {
            setContentView(builder.centerView!!)
        }
        if (!builder.leftBtn.isEmpty()) {
            setLeftBtn(builder.leftBtn, builder.leftListener)
        }
        if (!builder.centerBtn.isEmpty()) {
            setCenterBtn(builder.centerBtn, builder.centerListener)
        }
        if (!builder.rightBtn.isEmpty()) {
            setRightBtn(builder.rightBtn, builder.rightListener)
        }
    }

    /**
     * 设置中间提示
     */
    private fun setContentMessage(message: SpannableStringBuilder) {
        base_dialog_content_message_tv?.apply {
            gravity = View.VISIBLE
            text = message
        }
    }

    /**
     * 设置提示在中间布局
     */
    private fun setContentView(@NonNull contentView: View) {
        if (contentView.parent != null) {
            L.w(TAG, "setContentView: this $contentView is already added parent view. ")
            return
        }
        base_dialog_content_ll?.addView(contentView)
        base_dialog_content_message_tv?.visibility = View.GONE
    }

    /**
     * 设置标题
     */
    private fun setTitle(@NonNull title: String1) {
        base_dialog_title_tv?.text = title
    }

    @Contract(value = "Null -> false")
    private fun setTitleIcon(@NonNull show: Boolean, @DrawableRes iconRes: Int?) {
        base_dialog_title_icon_iv?.visibility = if (show) View.VISIBLE else View.GONE
        if (show && mContext != null && iconRes != null) {
            base_dialog_title_icon_iv?.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    iconRes
                )
            )
        }
    }

    /**
     * 设置左侧按钮
     */
    private fun setLeftBtn(btn: String1, clickListener: View.OnClickListener?) {
        base_dialog_left_btn?.apply {
            visibility = View.VISIBLE
            text = btn
            setOnClickListener(clickListener)
        }
    }

    /**
     * 设置中间按钮
     */
    private fun setCenterBtn(btn: String1, clickListener: View.OnClickListener?) {
        base_dialog_center_btn?.apply {
            visibility = View.VISIBLE
            text = btn
            setOnClickListener(clickListener)
        }
    }

    /**
     * 设置右侧按钮
     */
    private fun setRightBtn(btn: String1, clickListener: View.OnClickListener?) {
        base_dialog_right_btn?.apply {
            visibility = View.VISIBLE
            text = btn
            setOnClickListener(clickListener)
        }
    }
}