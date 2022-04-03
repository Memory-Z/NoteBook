package com.inz.z.note_book.view.adapter

import android.content.Context
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.databinding.ItemApplicationInfoBinding

/**
 *
 * 应用 列表适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/12 09:33.
 */
class ApplicationListRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<PackageInfo, ApplicationListRvAdapter.ApplicationListRvViewHolder>(mContext) {

    companion object {
    }

    public var listener: ApplicationListRvAdapterListener? = null
    private var layoutInflater: LayoutInflater? = null

    init {
        layoutInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ApplicationListRvViewHolder {
        val binding = ItemApplicationInfoBinding.inflate(mLayoutInflater, parent, false)
        return ApplicationListRvViewHolder(binding)
    }

    override fun onBindVH(holder: ApplicationListRvViewHolder, position: Int) {
        val packageInfo = list[position]

        holder.binding.itemAppInfoIconIv.post {
            Glide.with(mContext).load(packageInfo.applicationInfo.loadIcon(mContext.packageManager))
                .into(holder.binding.itemAppInfoIconIv)
        }
        holder.binding.itemAppInfoNameTv.post {
            holder.binding.itemAppInfoNameTv.setText(packageInfo.applicationInfo.loadLabel(mContext.packageManager))
        }
    }

    inner class ApplicationListRvViewHolder(val binding: ItemApplicationInfoBinding) :
        AbsBaseRvViewHolder(binding.root),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition;
            if (position > -1) {
                listener?.onItemClick(v, position)
            }
        }
    }

    interface ApplicationListRvAdapterListener {
        /**
         * 点击事件
         */
        fun onItemClick(v: View?, position: Int)
    }
}