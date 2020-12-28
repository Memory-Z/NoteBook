package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.local.LocalImageInfo
import kotlinx.android.synthetic.main.item_image_base.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*

/**
 * 系统文件 适配器
 *
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 16:27.
 */
class SysFileImageRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<LocalImageInfo, SysFileImageRvAdapter.SysFileImageBaseRvViewHolder>(mContext) {
    companion object {
        private const val VIEW_TYPE_ITEM = 0x01A0
        private const val VIEW_TYPE_MORE = 0x01A1

        private const val VIEW_SHOW_TYPE_LIST = 0x01B0
        private const val VIEW_SHOW_TYPE_GROUP = 0x01B1
    }

    private val requestOption = RequestOptions()
        .centerCrop()

    /**
     * 监听
     */
    var listener: SysFileImageRvAdapterListener? = null

    var isMoreData: Boolean = true

    private var showList = true

    override fun getItemCount(): Int {
        return if (list.size == 0) {
            0
        } else {
            if (isMoreData) {
                list.size + 1
            } else {
                list.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= list.size) {
            VIEW_TYPE_MORE
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): SysFileImageBaseRvViewHolder {
        when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = mLayoutInflater.inflate(R.layout.item_image_base, parent, false)
                return SysFileImageItemRvViewHolder(view)
            }
            else -> {
                val view = mLayoutInflater.inflate(R.layout.item_load_more, parent, false)
                return SysFileImageMoreRvViewHolder(view, listener)
            }
        }
    }

    override fun onBindVH(holder: SysFileImageBaseRvViewHolder, position: Int) {
        val info = getItemByPosition(position)
        when (holder) {
            is SysFileImageItemRvViewHolder -> {
                holder.nameTv.text = info?.localImageName
                holder.contentLl.visibility = if (this.showList) View.VISIBLE else View.GONE
                Glide.with(mContext).load(info?.localImagePath).apply(requestOption)
                    .into(holder.shortIv)
                holder.dateTv.text = info?.localImageDate
            }
            is SysFileImageMoreRvViewHolder -> {
                holder.listener = listener
            }
            else -> {

            }
        }
    }

    open class SysFileImageBaseRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView) {}

    class SysFileImageItemRvViewHolder(itemView: View) : SysFileImageBaseRvViewHolder(itemView) {

        val nameTv = itemView.item_image_base_content_title_tv
        val dateTv = itemView.item_image_base_content_date_tv
        val shortIv = itemView.item_image_base_short_iv
        val contentLl = itemView.item_image_base_content_ll
    }

    /**
     * 加载更多
     */
    class SysFileImageMoreRvViewHolder(
        itemView: View,
        var listener: SysFileImageRvAdapterListener?
    ) : SysFileImageBaseRvViewHolder(itemView),
        View.OnClickListener {

        val loadMoreTv = itemView.item_load_more_hint_tv

        init {
            loadMoreTv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onLoadMore(v, position)
            }
        }

    }

    override fun loadMoreData(list: MutableList<LocalImageInfo>?) {
        super.loadMoreData(list)
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 适配器监听
     */
    interface SysFileImageRvAdapterListener {
        fun onLoadMore(v: View?, position: Int)
    }

    /**
     * 切换显示模式类型
     * @param listShow 是否为列表
     */
    fun targetViewShowType(listShow: Boolean) {
        this.showList = listShow
        notifyDataSetChanged()
    }
}