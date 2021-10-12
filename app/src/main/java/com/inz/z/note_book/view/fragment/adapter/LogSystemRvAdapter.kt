package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.databinding.ItemLogSystemBinding
import com.inz.z.note_book.view.fragment.bean.LogSystemInfo

/**
 * 系统日志适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 13:52.
 */
class LogSystemRvAdapter(mContext: Context) :
    AbsBaseRvAdapter<LogSystemInfo, LogSystemRvAdapter.BaseLogSystemRvViewHolder>(mContext) {

    companion object {
        private const val TAG = "LogSystemRvAdapter"
    }

    var listener: LogSystemRvAdapterListener? = null


    override fun onCreateVH(parent: ViewGroup, viewType: Int): BaseLogSystemRvViewHolder {
//        val view = mLayoutInflater.inflate(R.layout.item_log_system, parent, false)
        val binding = ItemLogSystemBinding.inflate(mLayoutInflater, parent, false)
        return ItemLogSystemRvViewHolder(binding)
    }

    override fun onBindVH(holder: BaseLogSystemRvViewHolder, position: Int) {
        when (holder) {
            is ItemLogSystemRvViewHolder -> {
                val logSystemInfo = getItemByPosition(position)
                logSystemInfo?.let {
                    holder.itemLogSystemBinding.itemLogSystemTitleTv.text = it.fileName
                    holder.itemLogSystemBinding.itemLogSystemContentTv.text = it.getFileSize()
                }

            }
        }
    }

    open inner class BaseLogSystemRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView)

    inner class ItemLogSystemRvViewHolder(val itemLogSystemBinding: ItemLogSystemBinding) :
        BaseLogSystemRvViewHolder(itemLogSystemBinding.root),
        View.OnClickListener {

        init {
            itemLogSystemBinding.itemLogRightIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemMoreClick(v, position)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 系统日志 适配器 监听
     */
    interface LogSystemRvAdapterListener {
        /**
         * 项 更多操作点击
         */
        fun onItemMoreClick(v: View?, position: Int)
    }
}