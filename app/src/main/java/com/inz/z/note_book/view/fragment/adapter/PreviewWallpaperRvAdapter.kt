package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.database.bean.DesktopWallpaperInfo
import com.inz.z.note_book.databinding.ItemPreviewWallpaperAddBinding
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
        private val ADD_ITEM_TYPE = 0x0012
    }

    init {
        initGlide()
    }

    private fun initGlide() {
        glideRequestOptions = RequestOptions()
            // 使用磁盘缓存
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            // 跳过缓存
            .skipMemoryCache(true)
            // 缩小 图片大小
            .override(200, 200)

    }

    /**
     * 监听
     */
    var listener: PreviewWallpaperRvAdapterListener? = null
    private lateinit var glideRequestOptions: RequestOptions


    override fun getItemViewType(position: Int): Int {
        val data = getItemByPosition(position)
        if (data?.isEmptyData == true) {
            return ADD_ITEM_TYPE
        }
        return BASE_ITEM_TYPE
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): BasePreviewWallpaperRvHolder {
        when (viewType) {
            ADD_ITEM_TYPE -> {
                val binding = ItemPreviewWallpaperAddBinding.inflate(mLayoutInflater, parent, false)
                return AddPreviewWallpaperRvHolder(binding)
            }
            else -> {
                val binding = ItemPreviewWallpaperBinding.inflate(mLayoutInflater, parent, false)
                return PreviewWallpaperRvHolder(binding)
            }
        }
    }

    override fun onBindVH(holder: BasePreviewWallpaperRvHolder, position: Int) {
        val data = getItemByPosition(position)
        if (holder is PreviewWallpaperRvHolder) {
            holder.binding.itemPreviewWallpaperIv.let {
                Glide.with(it).load(data?.wallpaperPath).apply(glideRequestOptions).into(it)
            }
            holder.binding.itemPreviewWallpaperSwitch.isChecked =
                data?.isCurrentWallpaper() ?: false
            var title = "long"
            if (data?.startTime != null) {
                title = BaseTools.getBaseDateFormat().format(data.startTime)
            }
            holder.binding.itemPreviewWallpaperStartTimeTv.text = title

        }
    }

    override fun onViewRecycled(holder: BasePreviewWallpaperRvHolder) {
        super.onViewRecycled(holder)
        if (holder is PreviewWallpaperRvHolder) {
            // 清除 bitmap
            holder.binding.itemPreviewWallpaperIv.let {
                Glide.with(it).clear(it)
            }
        }
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
     * 添加 Item  ViewHolder
     */
    inner class AddPreviewWallpaperRvHolder(val binding: ItemPreviewWallpaperAddBinding) :
        BasePreviewWallpaperRvHolder(binding.root), View.OnClickListener {
        init {
            binding.itemPreviewWallpaperAddContentRl.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onAddItemClick(v)
        }
    }

    /**
     * 预览 ViewHolder.
     */
    inner class PreviewWallpaperRvHolder(val binding: ItemPreviewWallpaperBinding) :
        BasePreviewWallpaperRvHolder(binding.root), View.OnClickListener, View.OnLongClickListener,
        CompoundButton.OnCheckedChangeListener {
        init {
            binding.let {
                it.itemPreviewWallpaperSwitch.setOnClickListener(this)
                it.itemPreviewWallpaperChooseRbtn.setOnCheckedChangeListener(this)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(position, v)
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemChoose(isChecked, buttonView)
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
         * 点击添加 Item
         */
        fun onAddItemClick(v: View?)

        /**
         * item 点击
         * @param position 位置
         */
        fun onItemClick(position: Int, v: View?)

        /**
         * item 选择
         * @param checked 是否选中
         */
        fun onItemChoose(checked: Boolean, v: View?)

        /**
         * Item 长按
         * @param position 位置
         */
        fun onItemLongClick(position: Int, v: View?): Boolean = false
    }

}