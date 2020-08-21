package com.inz.z.base.view.activity.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.R
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.entity.BaseChooseFileNavBean
import kotlinx.android.synthetic.main.base_item_choose_file_nav.view.*

/**
 * 选择文件 导航适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/21 14:22.
 */
class ChooseFileNavRvAdapter(mContext: Context) :
    AbsBaseRvAdapter<BaseChooseFileNavBean, ChooseFileNavRvAdapter.ChooseFileNavRvHolder>(mContext) {

    private var blackCsl =
        ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.text_black_base_color))
    private var grayCsl =
        ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.text_black_70_color))

    var listener: ChooseFileNavRvAdapterListener? = null

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ChooseFileNavRvHolder {
        val view = mLayoutInflater.inflate(R.layout.base_item_choose_file_nav, parent, false)
        return ChooseFileNavRvHolder(view)
    }

    override fun onBindVH(holder: ChooseFileNavRvHolder, position: Int) {
        val bean = list[position]
        holder.titleNameTv.setText(bean.title)
        val isLast = position == (list.size - 1)
        holder.rightArrowIv.visibility = if (isLast) View.GONE else View.VISIBLE
        holder.titleNameTv.setTextColor(
            if (isLast)
                blackCsl
            else
                grayCsl
        )
    }

    open inner class ChooseFileNavRvHolder(itemView: View) : AbsBaseRvViewHolder(itemView),
        View.OnClickListener {
        var titleNameTv = itemView.base_item_cfn_tv
        var rightArrowIv = itemView.base_item_cfn_iv

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    else -> {
                        listener?.onNavClick(adapterPosition, v)
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 选择文件导航栏点击 监听
     */
    interface ChooseFileNavRvAdapterListener {
        /**
         * 导航 点击
         */
        fun onNavClick(position: Int, v: View?)
    }

    fun addChooseFileNav(bean: BaseChooseFileNavBean) {
        this.list.add(bean)
        notifyDataSetChanged()
    }

    /**
     * 选择导航
     */
    fun chooseNav(position: Int) {
        if (this.list.size >= position) {
            this.list.removeAll(
                this.list.subList(position, this.list.size)
            )
        }
        notifyDataSetChanged()
    }
}