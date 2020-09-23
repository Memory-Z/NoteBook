package com.inz.z.note_book.view.dialog.adapter

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.SearchContentInfo
import kotlinx.android.synthetic.main.item_search_record.view.*

/**
 * 记录搜索弹窗 适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/15 11:41.
 */
class RecordSearchRvAdapter(mContext: Context) :
    AbsBaseRvAdapter<SearchContentInfo, RecordSearchRvAdapter.RecordSearchRvViewHolder>(mContext) {

    private var searchContent = ""

    /**
     * 主色
     */
    private var colorPrimary: Int

    init {
        colorPrimary = ContextCompat.getColor(mContext, R.color.colorPrimary)
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): RecordSearchRvViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_search_record, parent, false)
        return RecordSearchRvViewHolder(view)
    }

    override fun onBindVH(holder: RecordSearchRvViewHolder, position: Int) {
        val searchContentInfo = list.get(position)
        holder.contextTv.text = setSearchContentColor(searchContentInfo.searchContent)
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

    inner class RecordSearchRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView),
        View.OnClickListener {

        val contextTv: TextView = itemView.item_search_content_tv
        val deleteIv = itemView.item_search_content_close_iv

        init {
            contextTv.setOnClickListener(this)
            deleteIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    contextTv.id -> {
                        recordSearchRvAdapterListener?.onItemClick(v, position)
                    }
                    deleteIv.id -> {
                        recordSearchRvAdapterListener?.onDeleteClick(v, position)
                    }
                    else -> {

                    }
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    override fun refreshData(list: MutableList<SearchContentInfo>?) {
        this.searchContent = ""
        super.refreshData(list)
    }

    fun refreshData(list: MutableList<SearchContentInfo>?, searchContent: String) {
        this.searchContent = searchContent
        super.refreshData(list)
    }

    interface RecordSearchRvAdapterListener {
        /**
         * 点击删除
         */
        fun onDeleteClick(v: View?, position: Int)

        /**
         * item View 点击
         */
        fun onItemClick(v: View?, position: Int)
    }

    var recordSearchRvAdapterListener: RecordSearchRvAdapterListener? = null
}