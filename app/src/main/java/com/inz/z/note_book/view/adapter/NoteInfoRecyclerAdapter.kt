package com.inz.z.note_book.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.base.NoteStatus
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.databinding.ItemNoteLayoutBinding

/**
 * 笔记信息列表 Recycler View 适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/29 11:35.
 */
class NoteInfoRecyclerAdapter(mContext: Context?) :
    AbsBaseRvAdapter<NoteInfo, NoteInfoRecyclerAdapter.NoteInfoRecyclerViewHolder>(mContext) {

    companion object {
        var noteInfoRvAdapterListener: NoteInfoRvAdapterListener? = null
    }

    interface NoteInfoRvAdapterListener {
        fun onItemClickListener(v: View, position: Int)
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): NoteInfoRecyclerViewHolder {
//        val mView = mLayoutInflater.inflate(R.layout.item_note_layout, parent, false)
        val binding = ItemNoteLayoutBinding.inflate(mLayoutInflater, parent, false)
        return NoteInfoRecyclerViewHolder(binding)
    }

    override fun onBindVH(holder: NoteInfoRecyclerViewHolder, position: Int) {
        val noteInfo = list[position]
        holder.mItemNoteLayoutBinding.itemNoteContentTitleTv.text = noteInfo.noteTitle
        holder.mItemNoteLayoutBinding.itemNoteContentCenterDetailTv.text = noteInfo.noteContent
        // 获取笔记状态描述.
        val statusStr = NoteStatus.getStatusStr(noteInfo.status, mContext)
        holder.mItemNoteLayoutBinding.itemNoteContentBottomStartTv.text = statusStr
        holder.mItemNoteLayoutBinding.itemNoteContentBottomEndTv.text =
            BaseTools.getBaseDateFormat().format(noteInfo.updateDate)
    }

    class NoteInfoRecyclerViewHolder(val mItemNoteLayoutBinding: ItemNoteLayoutBinding) :
        AbsBaseRvViewHolder(mItemNoteLayoutBinding.root) {

        init {
            itemView.setOnClickListener {
                noteInfoRvAdapterListener?.onItemClickListener(it, adapterPosition)
            }
        }
    }

    /**
     * 设置笔记信息列表适配器监听
     */
    fun setNoteInfoRvAdapterListener(listener: NoteInfoRvAdapterListener?) {
        noteInfoRvAdapterListener = listener
    }

    /**
     * 添加单项数据
     */
    fun addNoteInfo(noteInfo: NoteInfo) {
        list.add(noteInfo)
        notifyDataSetChanged()
    }

    /**
     * 添加列表数据
     */
    fun addNoteInfoList(noteInfoList: List<NoteInfo>) {
        list.addAll(noteInfoList)
        notifyDataSetChanged()
    }

    /**
     * 替换数据列表
     */
    fun replaceNoteInfoList(noteInfoList: List<NoteInfo>) {
        list.clear()
        list.addAll(noteInfoList)
        notifyDataSetChanged()
    }
}