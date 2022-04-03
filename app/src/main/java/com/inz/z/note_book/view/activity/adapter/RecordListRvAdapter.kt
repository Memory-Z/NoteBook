package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.record.RecordInfoStatus
import com.inz.z.note_book.databinding.ItemNoteRecordListContentBinding
import com.inz.z.note_book.databinding.ItemNoteRecordListTitleBinding

/**
 *
 * Record list RecyclerView Adapter.
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 11:45.
 */
class RecordListRvAdapter(mContext: Context) :
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

        /**
         * 默认模式
         */
        const val VIEW_MODE_DEFAULT = 0x000E01

        /**
         * 选择模式
         */
        const val VIEW_MODE_SELECT = 0x000E02
    }

    @IntDef(VIEW_MODE_DEFAULT, VIEW_MODE_SELECT)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RecordListViewMode {

    }

    var listener: RecordListRvAdapterListener? = null

    /**
     * 搜索内容
     */
    private var searchContent: String = ""

    /**
     * 主色
     */
    private var colorPrimary: Int

    init {
        colorPrimary = ContextCompat.getColor(mContext, R.color.colorPrimary)
    }

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
                val binding =
                    ItemNoteRecordListContentBinding.inflate(mLayoutInflater, parent, false)
                return ContentRecordListRvHolder(binding)
            }
            VIEW_TYPE_TITLE -> {
                val binding = ItemNoteRecordListTitleBinding.inflate(mLayoutInflater, parent, false)
                return TitleRecordListRvHolder(binding)
            }
            else -> {
                return EmptyRecordListRvHolder(View(mContext))
            }
        }
    }

    override fun onBindVH(holder: BaseRecordListRvHolder, position: Int) {
        val recordInfoStatus = list.get(position)
        if (holder is ContentRecordListRvHolder) {
            val time = recordInfoStatus.recordDateStr
            val title = recordInfoStatus.recordTitle
            var timeSpan = SpannableStringBuilder(time)
            var titleSpan = SpannableStringBuilder(title)
            if (!TextUtils.isEmpty(searchContent)) {
                timeSpan = setSearchContentColor(time)
                titleSpan = setSearchContentColor(title)
            }
            holder.binding.itemNoteRcTimeTv.text = timeSpan
            holder.binding.itemNoteRcTitleTv.text = titleSpan
        } else if (holder is TitleRecordListRvHolder) {
            holder.binding.itemNoteRtTitleTv.text = recordInfoStatus.titleName
        }
    }

    /**
     * 获取颜色
     */
    private fun setSearchContentColor(content: String): SpannableStringBuilder {
        val sp = SpannableStringBuilder()
        val index = content.indexOf(searchContent)
        if (index != -1) {
            val length = searchContent.length
            val beforeStr = content.subSequence(0, index)
            sp.append(beforeStr)
            val spSpan = SpannableString(searchContent)
            spSpan.setSpan(
                ForegroundColorSpan(colorPrimary),
                0,
                length,
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
            sp.append(spSpan)
            val otherContent = content.substring(index + length)
            val otherSp = setSearchContentColor(otherContent)
            sp.append(otherSp)
        } else {
            sp.append(content)
        }
        return sp
    }

    open inner class BaseRecordListRvHolder(itemView: View) : AbsBaseRvViewHolder(itemView) {}

    inner class ContentRecordListRvHolder(val binding: ItemNoteRecordListContentBinding) :
        BaseRecordListRvHolder(binding.root),
        View.OnClickListener {
        init {
            binding.itemNoteRcContentRl.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(v, position)
            }
        }
    }

    inner class TitleRecordListRvHolder(val binding: ItemNoteRecordListTitleBinding) :
        BaseRecordListRvHolder(binding.root) {}

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


    override fun refreshData(list: MutableList<RecordInfoStatus>?) {
        searchContent = ""
        super.refreshData(list)
    }

    /**
     * 列表刷新，
     * @param searchContent 搜索内容
     */
    fun refreshData(list: MutableList<RecordInfoStatus>?, @NonNull searchContent: String) {
        this.searchContent = searchContent
        super.refreshData(list)
    }
}