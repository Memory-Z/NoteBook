package com.inz.z.base.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.inz.z.base.R
import com.inz.z.base.util.KeyBoardUtils
import com.inz.z.base.util.L
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 标签布局.
 *
 * ====================================================
 * Create by 11654 in 2021/5/25 20:12
 */
class TagLayout : RelativeLayout, View.OnFocusChangeListener, TextView.OnEditorActionListener,
    View.OnClickListener, View.OnLongClickListener {
    companion object {
        private const val TAG = "TagLayout"
        private const val DEFAULT_TEXT_SIZE = 14F
    }


    private var mView: View? = null
    private var mRootView: LinearLayout? = null
    private var mContext: Context? = null
    private var mEditText: EditText? = null
    private var mTextColor: Int = Color.BLACK
    private var mTextSize: Float = DEFAULT_TEXT_SIZE
    var tagLayoutListener: TagLayoutListener? = null

    /**
     * 关闭 按钮，
     */
    private var closeIv: ImageView? = null

    /**
     * 分割线
     */
    private var lineView: View? = null

    /**
     * 尾部 内容
     */
    private var endContentLl: LinearLayout? = null

    /**
     * 蒙版View .控制点击
     */
    private var maskView: View? = null

    /**
     * 是否可编辑。默认可编辑
     */
    private val editable: AtomicBoolean = AtomicBoolean(true)

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

    @SuppressLint("RestrictedApi")
    private fun initViewStyle(attrs: AttributeSet?) {
        val type =
            TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.TagLayout, 0, 0)
        this.mTextColor = type.getColor(R.styleable.TagLayout_tag_color, Color.BLACK)
        this.mTextSize = type.getDimension(R.styleable.TagLayout_tag_text_size, DEFAULT_TEXT_SIZE)

        type.recycle()
    }

    private fun initView() {
        if (this.mView == null) {
            this.mView = LayoutInflater.from(mContext).inflate(R.layout.base_tag_layout, this, true)
            this.mView?.let {
                this.mRootView = it.findViewById(R.id.base_tag_layout_root_ll)
                this.maskView = it.findViewById(R.id.base_tag_layout_mask_view)
                this.maskView?.apply {
                    this.setOnClickListener(this@TagLayout)
                    this.setOnLongClickListener(this@TagLayout)
                }
                this.mEditText = it.findViewById(R.id.base_tag_layout_et)
                this.mEditText?.apply {
//                    this.setOnClickListener(this@TagLayout)
                    this.onFocusChangeListener = this@TagLayout
                    this.setOnEditorActionListener(this@TagLayout)
                }
                this.endContentLl = it.findViewById(R.id.base_tag_layout_end_ll)
                this.lineView = it.findViewById(R.id.base_tag_layout_line_v)
                this.closeIv = it.findViewById(R.id.base_tag_layout_close_iv)
                this.closeIv?.setOnClickListener(this@TagLayout)
            }
        }

        L.i(TAG, "initView: ")
        targetBackground(editable.get())


    }

    /**
     * 初始化 状态。
     */
    private fun initStatus() {
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        L.i(TAG, "onFocusChange: hasFocus = $hasFocus")
        targetBackground(hasFocus)
    }

    override fun onClick(v: View?) {
        val vId = v?.id
        L.i(TAG, "onClick: ---> $vId >>> ${editable.get()}")
        if (vId == R.id.base_tag_layout_close_iv) {
            L.i(TAG, "onClick: >> close iv")
            // 关闭
            tagLayoutListener?.onClickClose(this@TagLayout)
        } else if (vId == R.id.base_tag_layout_mask_view) {
            L.i(TAG, "onClick: >> mask view. ")
            // 点击且不可编辑时，切换至编辑模式，
            if (!editable.get()) {
                targetEditTextStatus(true)
            }
        } else {
            L.i(TAG, "onClick: >> Other. ")
        }
    }

    override fun onLongClick(v: View?): Boolean {
        L.i(TAG, "onLongClick: >> ")
//        // 长点击时，可获取焦点， 可切换至编辑模式。
//        targetEditTextStatus(true)
        // 长按时，显示关闭按钮。
        if (this.endContentLl?.visibility == GONE) {
            targetCloseStatus(true)
        }
        return true
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE
            || actionId == EditorInfo.IME_ACTION_NEXT
        ) {
            // 失去焦点
            targetBackground(false)
            // 切换显示状态.
            tagLayoutListener?.onKeyDone()
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        this.mEditText?.minimumWidth = height * 2
        this.mEditText?.apply {
            val lp = this.layoutParams as LinearLayout.LayoutParams
            lp.marginStart = height / 2
            lp.marginEnd = height / 2
        }
        L.i(TAG, "onMeasure: ")

    }

    /**
     * 切换EditText状态
     * @param canClick 是否可点击 。
     */
    private fun targetEditTextStatus(canClick: Boolean) {
        L.i(TAG, "targetEditTextStatus: $canClick ")
        if (canClick) {
            editable.set(true)
        }
        this.maskView?.apply {
            this.visibility = if (canClick) GONE else VISIBLE
        }
        this.mEditText?.apply {
//            this.isClickable = canClick
            this.isFocusable = canClick
            this.postDelayed(
                {
                    // 清空 或 请求
                    if (canClick) {
                        this.requestFocus()
                        this.requestFocusFromTouch()
                        KeyBoardUtils.showKeyBoard(this)
//                        this.performClick()
                    } else {
                        this.clearFocus()
                    }
                },
                500
            )

        }

        // 改变 mask 大小
        resetMaskSize()
    }

    /**
     * 重置蒙版大小。
     *
     */
    private fun resetMaskSize() {
        this.maskView?.let {
            var endContentWidth = 0
            this.endContentLl?.let {
                if (it.visibility == VISIBLE) {
                    endContentWidth = it.width
                }
            }
            val lp = it.layoutParams as LayoutParams
            lp.marginEnd = endContentWidth
            it.layoutParams = lp
        }
    }

    /**
     * 切换背景.
     * @param hasFocus 是否拥有焦点.
     */
    private fun targetBackground(hasFocus: Boolean) {
        L.i(TAG, "targetBackground: >> $hasFocus")
        // 设置是否可编辑，失去焦点后无法编辑，
        editable.set(hasFocus)
        // 失去焦点，设置不可在获取焦点
        if (!hasFocus) {
            targetEditTextStatus(false)
            // 隐藏关闭按钮。
            targetCloseStatus(false)
        }
        this.mRootView?.let {
            it.background = resetBackgroundDrawable(it.context, hasFocus)
        }
    }

    /**
     * 切换 关闭按钮状态 .
     * @param show 是否显示。
     */
    private fun targetCloseStatus(show: Boolean) {
        L.i(TAG, "targetCloseStatus: >>> $show")
        this.endContentLl?.apply {
            this.visibility = if (show) VISIBLE else GONE
        }
        // 改变 mask 大小
        resetMaskSize()
    }

    /**
     * 重置背景Drawable.
     */
    private fun resetBackgroundDrawable(context: Context, hasFocus: Boolean): Drawable? {
        val drawable = ContextCompat.getDrawable(
            context,
            if (hasFocus) R.drawable.bg_tag_layout_editview
            else R.drawable.bg_tag_layout_editview_full
        )
//        if (drawable is GradientDrawable) {
//            val viewHeight = height
//            drawable.cornerRadius = viewHeight / 2F
//        }
        return drawable
    }

    interface TagLayoutListener {
        /**
         * 点击完成.
         */
        fun onKeyDone()

        /**
         * 点击关闭。
         */
        fun onClickClose(v: TagLayout?)
    }

    /**
     * 获取显示内容
     * @return 显示内容.
     */
    fun getContent(): String {
        return mEditText?.text.toString()
    }

    /**
     * 是否存在内容。
     * @return 是否存在内容。
     */
    fun haveContent(): Boolean {
        return !TextUtils.isEmpty(getContent())
    }

    /**
     * 设置显示内容。
     */
    fun setContent(content: String) {
        this.mEditText?.apply {
            this.setText(content)
            targetBackground(false)
            this.clearFocus()
        }
    }
}