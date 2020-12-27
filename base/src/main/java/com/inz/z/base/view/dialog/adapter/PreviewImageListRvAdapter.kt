package com.inz.z.base.view.dialog.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.R
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.entity.BasePreviewImageBean
import com.inz.z.base.entity.Constants
import kotlinx.android.synthetic.main.base_item_preview_img_list.view.*

/**
 *
 * 预览文件列表适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/15 09:24.
 */
class PreviewImageListRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<BasePreviewImageBean, PreviewImageListRvAdapter.PreviewImageListRvHolder>(
        mContext
    ) {
    private val option: RequestOptions = RequestOptions()
        .error(R.drawable.image_load_error)
        .centerCrop()

    private var currentSelectedPosition = 0
    var listener: PreviewImageListRvAdapterListener? = null

    override fun onCreateVH(parent: ViewGroup, viewType: Int): PreviewImageListRvHolder {
        val view = mLayoutInflater.inflate(R.layout.base_item_preview_img_list, parent, false)
        return PreviewImageListRvHolder(view)
    }

    override fun onBindVH(holder: PreviewImageListRvHolder, position: Int) {
        val item = list.get(position)
        if (item.fileType == Constants.FileType.FILE_TYPE_IMAGE) {
            holder.preIv.visibility = View.VISIBLE
            Glide.with(mContext).load(item.filePath).apply(option).into(holder.preIv)
            if (position == currentSelectedPosition) {
                holder.preIv.setBackgroundResource(R.drawable.bg_image_selected)
            } else {
                holder.preIv.setBackgroundResource(R.drawable.bg_image_unselected)
            }
        } else {
            holder.preIv.visibility = View.GONE
            Glide.with(mContext).clear(holder.preIv)
        }
    }

    inner class PreviewImageListRvHolder(itemView: View) : AbsBaseRvViewHolder(itemView),
        View.OnClickListener {
        val preIv = itemView.base_item_pi_iv

        init {
            preIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    R.id.base_item_pi_iv -> {
                        targetSelectedPosition(position)
                        listener?.onImageClick(v, position)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    /**
     * 切换选中项
     */
    fun targetSelectedPosition(position: Int) {
        if (position != currentSelectedPosition) {
            val oldItem = getItemByPosition(currentSelectedPosition)
            oldItem?.isSelectedPreview = false
            notifyItemChanged(currentSelectedPosition)

            val item = getItemByPosition(position)
            item?.isSelectedPreview = true
            notifyItemChanged(position)
            currentSelectedPosition = position
        }

    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    override fun refreshData(list: MutableList<BasePreviewImageBean>?) {
        currentSelectedPosition = 0
        list?.forEachIndexed { index, it ->
            if (it.isSelectedPreview) {
                currentSelectedPosition = index
                return@forEachIndexed
            }
        }
        super.refreshData(list)
    }

    interface PreviewImageListRvAdapterListener {

        /**
         * 图片点击
         */
        fun onImageClick(v: View?, position: Int)
    }
}