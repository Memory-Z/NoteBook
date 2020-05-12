package com.inz.z.note_book.view.adapter

import android.content.Context
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.item_application_info.view.*

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
        val view = layoutInflater?.inflate(R.layout.item_application_info, parent, false)
        return ApplicationListRvViewHolder(view!!)
    }

    override fun onBindVH(holder: ApplicationListRvViewHolder, position: Int) {
        val packageInfo = list[position]

        holder.appIconIv.post {
            Glide.with(mContext).load(packageInfo.applicationInfo.loadIcon(mContext.packageManager))
                .into(holder.appIconIv)
        }
        holder.appNameTv.post {
            holder.appNameTv.setText(packageInfo.applicationInfo.loadLabel(mContext.packageManager))
        }
    }

    inner class ApplicationListRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView),
        View.OnClickListener {
        var appNameTv: TextView = itemView.item_app_info_name_tv
        var appIconIv: ImageView = itemView.item_app_info_icon_iv

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