package com.inz.z.note_book.view.widget.layout

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.TintTypedArray
import com.bumptech.glide.Glide
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ItemLoadMoreBinding
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
    private var binding: ItemLoadMoreBinding? = null

    private val loading = AtomicBoolean(false)

    /**
     * 提示信息
     */
    private var hintTv: TextView? = null

    /**
     * 加载进度
     */
    private var loadingIv: ImageView? = null

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
            binding = ItemLoadMoreBinding.inflate(LayoutInflater.from(mContext), this, true)
            mView = binding?.root
        }
        mView?.let {
            loadingIv = binding?.itemLoadMoreLoadingIv
            hintTv = binding?.itemLoadMoreHintTv
            it.setOnClickListener {
                if (!loading.get()) {
                    startLoad()
                }
            }
        }
        loadingIv?.let {
            Glide.with(it).asGif().load(R.drawable.load_hor_gif).into(it)
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
        loadingIv?.visibility = if (loading.get()) VISIBLE else GONE
        // 是否加载 中。
        val str = if (loading.get())
            loadingStr
        else
        // 是否可以加载
            if (canLoadMore) hintStr else loadFinish
        hintTv?.text = str
        hintTv?.visibility = if (loading.get()) GONE else VISIBLE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loadingIv?.let {
            Glide.with(it).clear(it)
        }
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