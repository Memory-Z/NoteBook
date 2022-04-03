package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.base.FileType
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ItemNewDynamicImageBinding
import com.inz.z.note_book.databinding.ItemNewDynamicNullBinding

/**
 * 新动态 适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 09:11.
 */
class NewDynamicRvAdapter(mContext: Context) :
    AbsBaseRvAdapter<BaseChooseFileBean, NewDynamicRvAdapter.BaseNewDynamicRvViewHolder>(mContext) {

    companion object {
        private const val TAG = "NewDynamicRvAdapter"

        private const val TYPE_NULL = 0x00F0
        private const val TYPE_IMAGE = 0x00F1
        private const val TYPE_FILE = 0x00F2

    }

    var listener: NewDynamicRvAdapterListener? = null

    private val requestOption = RequestOptions()
        .centerCrop()
        .error(R.drawable.image_load_error)

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (list.size <= position) {
            return TYPE_NULL
        }
        return TYPE_IMAGE
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): BaseNewDynamicRvViewHolder {
        when (viewType) {
            TYPE_IMAGE -> {
                val binding = ItemNewDynamicImageBinding.inflate(mLayoutInflater, parent, false)
                return ImageNewDynamicRvViewHolder(binding)
            }
            else -> {
                val binding = ItemNewDynamicNullBinding.inflate(mLayoutInflater, parent, false)
                return NullNewDynamicRvViewHolder(binding)
            }
        }
    }

    override fun onBindVH(holder: BaseNewDynamicRvViewHolder, position: Int) {
        when (holder) {
            is ImageNewDynamicRvViewHolder -> {
                val item = getItemByPosition(position)
                var filePath = ""
                if (item != null) {
                    item.let {
                        if (it.fileType == FileType.FILE_TYPE_IMAGE) {
                            filePath = it.filePath
                        }
                    }
                }
                Glide.with(mContext).load(filePath).apply(requestOption).into(holder.centerIv)
            }
            is NullNewDynamicRvViewHolder -> {

            }
            else -> {

            }

        }
    }

    /**
     * 标准模板
     */
    open inner class BaseNewDynamicRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView) {

    }

    /**
     * 空
     */
    inner class NullNewDynamicRvViewHolder(binding: ItemNewDynamicNullBinding) :
        BaseNewDynamicRvViewHolder(binding.root),
        View.OnClickListener {
        val addIv = binding.itemNdnIv

        init {
            addIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.addItem(v)
            }
        }
    }

    /**
     * 图片
     */
    inner class ImageNewDynamicRvViewHolder(binding: ItemNewDynamicImageBinding) :
        BaseNewDynamicRvViewHolder(binding.root),
        View.OnClickListener {
        val centerIv = binding.itemNdImageIv
        val closeIv = binding.itemNdImageCloseIv

        init {
            closeIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    R.id.item_nd_image_close_iv -> {
                        listener?.removeItem(v, position)
                    }
                    R.id.item_nd_image_iv -> {

                    }
                    else -> {
                        // TODO: 2020/10/29 Nothings.
                    }
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    interface NewDynamicRvAdapterListener {
        /**
         * 添加项目
         */
        fun addItem(v: View?)

        /**
         * 移除项
         */
        fun removeItem(v: View?, position: Int)
    }

    /**
     * 移除项
     */
    fun removeItem(position: Int) {
        if (position < list.size && position >= 0) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}