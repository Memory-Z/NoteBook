package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.slide_table.adapter.SlideRvAdapter
import com.inz.slide_table.adapter.SlideRvViewHolder
import com.inz.slide_table.view.SlideRowItemView
import com.inz.z.note_book.R
import com.inz.z.note_book.view.fragment.bean.LogOperationSlideTableBean

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 14:02.
 */
class LogOperationRvAdapter(mContext: Context) :
    SlideRvAdapter<LogOperationSlideTableBean, LogOperationRvAdapter.BaseLogOperationRvViewHolder>(
        mContext
    ) {

    companion object {
        private const val TAG = "LogOperationRvAdapter"
    }

    private val itemTextViewSet = SlideRowItemView.ItemTextViewSet.Builder(mContext)
        .setItemWidthDp(162, 192)
        .build()

    override fun onCreateVH(parent: ViewGroup, viewType: Int): BaseLogOperationRvViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_slide_column, parent, false)
        return ItemLogOperationRvViewHolder(view, itemTextViewSet)
    }

    override fun onBindVH(holder: BaseLogOperationRvViewHolder, position: Int) {
        val bean = getItemByPosition(position)
        bean?.apply {
            if (this.isHeaderRow) {
                holder.slideRowItemView.setItemList(this.toDataHeaderList())
            } else {
                holder.slideRowItemView.setItemList(this.toDataColumnList())
            }
        }
    }

    abstract class BaseLogOperationRvViewHolder(
        itemView: View,
        itemTextViewSet: SlideRowItemView.ItemTextViewSet?
    ) : SlideRvViewHolder(itemView, itemTextViewSet) {

    }

    class ItemLogOperationRvViewHolder(
        itemView: View,
        itemTextViewSet: SlideRowItemView.ItemTextViewSet?
    ) : BaseLogOperationRvViewHolder(itemView, itemTextViewSet),
        View.OnClickListener {

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {

            }
        }

        override fun getSlideRowItemView(itemView: View): SlideRowItemView {
            return itemView.findViewById(R.id.item_slide_col_sriv)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    interface LogOperationRvAdapterListener {

    }
}