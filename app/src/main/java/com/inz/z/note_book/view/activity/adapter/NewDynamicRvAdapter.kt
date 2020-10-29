package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.Constants
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.item_new_dynamic_image.view.*
import kotlinx.android.synthetic.main.item_new_dynamic_null.view.*

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
                val view = mLayoutInflater.inflate(R.layout.item_new_dynamic_image, parent, false)
                return ImageNewDynamicRvViewHolder(view)
            }
            else -> {
                val view = mLayoutInflater.inflate(R.layout.item_new_dynamic_null, parent, false)
                return NullNewDynamicRvViewHolder(view)
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
                        if (it.fileType == Constants.FileType.FILE_TYPE_IMAGE) {
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
    inner class NullNewDynamicRvViewHolder(itemView: View) : BaseNewDynamicRvViewHolder(itemView),
        View.OnClickListener {
        val addIv = itemView.item_ndn_iv

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
    inner class ImageNewDynamicRvViewHolder(itemView: View) : BaseNewDynamicRvViewHolder(itemView),
        View.OnClickListener {
        val centerIv = itemView.item_nd_image_iv
        val closeIv = itemView.item_nd_image_close_iv

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