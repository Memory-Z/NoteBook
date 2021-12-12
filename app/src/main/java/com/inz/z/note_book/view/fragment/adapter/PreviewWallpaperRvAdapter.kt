package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.database.bean.DesktopWallpaperInfo
import com.inz.z.note_book.databinding.ItemPreviewWallpaperBinding

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2021/11/21 21:50
 */
class PreviewWallpaperRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<DesktopWallpaperInfo, PreviewWallpaperRvAdapter.BasePreviewWallpaperRvHolder>(
        mContext
    ) {

    companion object {
        private const val TAG = "PreviewWallpaperRvAdapt"
        private val BASE_ITEM_TYPE = 0x0010
        private val MORE_ITME_TYPE = 0x0011
    }

    /**
     * 监听
     */
    var listener: PreviewWallpaperRvAdapterListener? = null


    override fun getItemViewType(position: Int): Int {
        return BASE_ITEM_TYPE
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): BasePreviewWallpaperRvHolder {
        when (viewType) {
            else -> {
                val binding = ItemPreviewWallpaperBinding.inflate(mLayoutInflater, parent, false)
                return PreviewWallpaperRvHolder(binding)
            }
        }
    }

    override fun onBindVH(holder: BasePreviewWallpaperRvHolder, position: Int) {

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        listener = null
    }

    /**
     * 基础 ViewHolder
     */
    abstract class BasePreviewWallpaperRvHolder(itemView: View) : AbsBaseRvViewHolder(itemView)


    /**
     * 预览 ViewHolder.
     */
    inner class PreviewWallpaperRvHolder(binding: ItemPreviewWallpaperBinding) :
        BasePreviewWallpaperRvHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(position, v)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                return listener?.onItemLongClick(position, v) ?: false
            }
            return false
        }
    }

    /**
     * 预览 壁纸适配器 监听
     */
    interface PreviewWallpaperRvAdapterListener {

        /**
         * item 点击
         * @param position 位置
         */
        fun onItemClick(position: Int, v: View?)

        /**
         * Item 长按
         * @param position 位置
         */
        fun onItemLongClick(position: Int, v: View?): Boolean = false
    }

}