package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import com.inz.slide_table.adapter.SlideRowTitleRvAdapter
import com.inz.slide_table.adapter.SlideRowTitleRvViewHolder
import com.inz.slide_table.view.SlideTableListener
import com.inz.z.note_book.R
import com.inz.z.note_book.view.fragment.bean.LogOperationSlideTableBean

/**
 * 操作日志  行标题
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/09 17:22.
 */
class LogOperationRowTitleRvAdapter(context: Context?, val listener: SlideTableListener?) :
    SlideRowTitleRvAdapter<LogOperationSlideTableBean, LogOperationRowTitleRvAdapter.LogOperationRowTitelRvViewHolder>(context) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogOperationRowTitelRvViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_slide_row_title, parent, false)
        return LogOperationRowTitelRvViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogOperationRowTitelRvViewHolder, position: Int) {
        val title = titleList.get(position).rowTitle ?: ""
        holder.orderTv.text = "$position"
        holder.titleTv.text = title
    }

    inner class LogOperationRowTitelRvViewHolder(itemView: View) :
        SlideRowTitleRvViewHolder(itemView) {

        val orderTv: TextView = itemView.findViewById(R.id.item_slide_row_title_order_tv)
        val titleTv: TextView = itemView.findViewById(R.id.item_slide_row_title_name_tv)
        override fun getSlideTableListener(): SlideTableListener? {
            return listener
        }

        override fun onClickRow(v: View?, position: Int) {

        }
    }
}