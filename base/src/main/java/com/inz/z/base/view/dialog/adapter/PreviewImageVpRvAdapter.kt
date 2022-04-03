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
import com.inz.z.base.databinding.BaseItemPreviceImageBinding
import com.inz.z.base.entity.BasePreviewImageBean

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/15 10:59.
 */
class PreviewImageVpRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<BasePreviewImageBean, PreviewImageVpRvAdapter.PreviewImageVpRvHolder>(mContext) {

    private val options: RequestOptions = RequestOptions()
        .centerInside()
        .error(R.drawable.image_load_error)

    var listener: PreviewImageVpRvAdapterListener? = null

    override fun onCreateVH(parent: ViewGroup, viewType: Int): PreviewImageVpRvHolder {
        val binding = BaseItemPreviceImageBinding.inflate(mLayoutInflater, parent, false)
        return PreviewImageVpRvHolder(binding)
    }

    override fun onBindVH(holder: PreviewImageVpRvHolder, position: Int) {
        val bean = getItemByPosition(position)
        bean?.apply {
            Glide.with(mContext).load(this.filePath).apply(options)
                .into(holder.contentIv)
        }
    }

    inner class PreviewImageVpRvHolder(binding: BaseItemPreviceImageBinding) :
        AbsBaseRvViewHolder(binding.root),
        View.OnClickListener {
        val contentIv = binding.baseItemPiContentIv

        init {
            contentIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onImageClick(v, position)
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    interface PreviewImageVpRvAdapterListener {
        fun onImageClick(v: View?, position: Int)
    }

    /**
     * 刷新数据状态
     */
    fun refreshItemOnPosition(position: Int, bean: BasePreviewImageBean) {
        val basePreviewImageBean = getItemByPosition(position)
        if (basePreviewImageBean != null) {
            this.list.set(position, bean)
        }
    }

}