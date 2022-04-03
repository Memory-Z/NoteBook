package com.inz.z.note_book.view.dialog.adapter

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.SearchContentInfo
import com.inz.z.note_book.databinding.ItemSearchRecordBinding

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
        val binding = ItemSearchRecordBinding.inflate(mLayoutInflater, parent, false)
        return RecordSearchRvViewHolder(binding)
    }

    override fun onBindVH(holder: RecordSearchRvViewHolder, position: Int) {
        val searchContentInfo = list.get(position)
        holder.binding.itemSearchContentTv.text =
            setSearchContentColor(searchContentInfo.searchContent)
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

    inner class RecordSearchRvViewHolder(val binding: ItemSearchRecordBinding) :
        AbsBaseRvViewHolder(binding.root),
        View.OnClickListener {

        init {
            binding.itemSearchContentTv.setOnClickListener(this)
            binding.itemSearchContentCloseIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            binding.let {
                if (position != RecyclerView.NO_POSITION) {
                    when (v?.id) {
                        it.itemSearchContentTv.id -> {
                            recordSearchRvAdapterListener?.onItemClick(v, position)
                        }
                        it.itemSearchContentCloseIv.id -> {
                            recordSearchRvAdapterListener?.onDeleteClick(v, position)
                        }
                        else -> {

                        }
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