package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.BaseTools
import com.inz.z.calendar_view.Tools
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.record.RecordInfoStatus
import kotlinx.android.synthetic.main.item_note_record_list_content.view.*
import kotlinx.android.synthetic.main.item_note_record_list_title.view.*
import java.util.*

/**
 *
 * Record list RecyclerView Adapter.
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 11:45.
 */
class RecordListRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<RecordInfoStatus, RecordListRvAdapter.BaseRecordListRvHolder>(mContext) {

    companion object {
        private const val TAG = "RecordListRvAdapter"

        /**
         * 列表内容
         */
        private val VIEW_TYPE_LIST = 0x000F01

        /**
         * 标题
         */
        private val VIEW_TYPE_TITLE = 0x000F02

        /**
         * 空
         */
        private val VIEW_TYPE_EMPTY = 0x000F03

    }

    var listener: RecordListRvAdapterListener? = null

    override fun getItemViewType(position: Int): Int {
        val recordInfoStatus = list.get(position)
        if (recordInfoStatus.isTitle) {
            return VIEW_TYPE_TITLE
        } else {
            return VIEW_TYPE_LIST
        }
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): BaseRecordListRvHolder {
        when (viewType) {
            VIEW_TYPE_LIST -> {
                val view =
                    mLayoutInflater.inflate(R.layout.item_note_record_list_content, parent, false)
                return ContentRecordListRvHolder(view)
            }
            VIEW_TYPE_TITLE -> {
                val view =
                    mLayoutInflater.inflate(R.layout.item_note_record_list_title, parent, false)
                return TitleRecodListRvHolder(view)
            }
            else -> {
                return EmptyRecordListRvHolder(View(mContext))
            }
        }
    }

    override fun onBindVH(holder: BaseRecordListRvHolder, position: Int) {
        val recordInfoStatus = list.get(position)
        if (holder is ContentRecordListRvHolder) {
            val timeStr = BaseTools.getDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(recordInfoStatus.recordDate)
            holder.itemView.item_note_rc_time_tv.text = timeStr
            holder.itemView.item_note_rc_title_tv.text = recordInfoStatus.recordTitle
        } else if (holder is TitleRecodListRvHolder) {
            holder.itemView.item_note_rt_title_tv.text = recordInfoStatus.titleName
        }
    }

    open inner class BaseRecordListRvHolder(itemView: View) : AbsBaseRvViewHolder(itemView) {}

    inner class ContentRecordListRvHolder(itemView: View) : BaseRecordListRvHolder(itemView),
        View.OnClickListener {
        init {
            itemView.item_note_rc_content_rl.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(v, position)
            }
        }
    }

    inner class TitleRecodListRvHolder(itemView: View) : BaseRecordListRvHolder(itemView) {}

    inner class EmptyRecordListRvHolder(itemView: View) : BaseRecordListRvHolder(itemView) {}


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


    /**
     * 记录Adapter 监听
     */
    interface RecordListRvAdapterListener {
        /**
         * 项点击
         */
        fun onItemClick(v: View?, position: Int);

    }

}