package com.inz.z.note_book.view.widget.layout

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.item_load_more.view.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * 加载更多布局
 *
 * ====================================================
 * Create by 11654 in 2020/12/30 20:54
 */
class LoadMoreLayout : LinearLayout {
    companion object {
        private const val TAG = "LoadMoreLayout"
    }

    private var mContext: Context? = null
    private var mView: View? = null

    private val loading = AtomicBoolean(false)

    /**
     * 提示信息
     */
    private var hintTv: TextView? = null

    /**
     * 加载进度
     */
    private var progressBar: ProgressBar? = null

    /**
     * 默认提示语
     */
    var hintStr = ""
    var loadingStr = ""
    var loadFinish = ""

    /**
     * 是否可以加载更多
     */
    var canLoadMore = true

    /**
     * 加载更多监听
     */
    var loadMoreListener: LoadMoreLayoutListener? = null

    constructor(context: Context?) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
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
        val typeArray = TintTypedArray.obtainStyledAttributes(
            mContext,
            attrs,
            R.styleable.LoadMoreLayout,
            0,
            0
        )
        typeArray.recycle()
    }

    private fun initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.item_load_more, this, true)
        }
        mView?.let {
            progressBar = it.item_load_more_pbar
            hintTv = it.item_load_more_hint_tv
            hintTv?.setOnClickListener {
                if (!loading.get()) {
                    startLoad()
                }
            }
        }
        loading.set(false)
        initDate()
        targetLoadState()
    }

    private fun initDate() {
        if (TextUtils.isEmpty(hintStr)) {
            hintStr = mContext?.getString(R.string.load_more) ?: "加载更多"
        }
        if (TextUtils.isEmpty(loadingStr)) {
            loadingStr = mContext?.getString(R.string.loading) ?: "加载中..."
        }
        if (TextUtils.isEmpty(loadFinish)) {
            loadFinish = mContext?.getString(R.string.loading_finish) ?: "加载完毕"
        }
    }

    /**
     * 切换显示状态
     */
    private fun targetLoadState() {
        progressBar?.visibility = if (loading.get()) VISIBLE else INVISIBLE
        // 是否加载 中。
        val str = if (loading.get())
            loadingStr
        else
        // 是否可以加载
            if (canLoadMore) hintStr else loadFinish
        hintTv?.text = str
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


    /**
     * 加载更多监听
     */
    interface LoadMoreLayoutListener {
        /**
         * 加载更多
         */
        fun loadMore(v: View?)
    }

    /**
     * 开始加载
     */
    fun startLoad() {
        if (canLoadMore) {
            loadMoreListener?.let {
                it.loadMore(mView)
                loading.set(true)
                targetLoadState()
            }
        }
    }

    /**
     * 停止加载
     */
    fun stopLoad() {
        stopLoad(false)
    }

    /**
     * 停止加载//
     * @param canLoadMore 是否可以加载更多
     */
    fun stopLoad(canLoadMore: Boolean) {
        loading.set(false)
        this.canLoadMore = canLoadMore
        targetLoadState()
    }

}