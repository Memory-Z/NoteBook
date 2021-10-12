package com.inz.z.note_book.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.NoteGroup
import com.inz.z.note_book.database.controller.NoteGroupWithInfoService
import com.inz.z.note_book.databinding.ItemNoteGroupLayoutBinding

/**
 * 组 Recycler View 适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/30 14:13.
 */
class NoteGroupRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<NoteGroup, NoteGroupRvAdapter.NoteGroupRvHolder>(mContext) {

    interface NoteGroupItemListener {
        fun onItemClick(v: View, position: Int)
    }

    companion object {
        private var noteGroupItemListener: NoteGroupItemListener? = null

    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): NoteGroupRvHolder {
//        val view = mLayoutInflater.inflate(R.layout.item_note_group_layout, parent, false)
        val binding = ItemNoteGroupLayoutBinding.inflate(mLayoutInflater, parent, false)
        return NoteGroupRvHolder(binding)
    }

    override fun onBindVH(holder: NoteGroupRvHolder, position: Int) {
        val noteGroup = list[position]
        holder.mItemNoteGroupLayoutBinding.itemNoteGroupTitleTv.text = noteGroup.groupName
        val groupSize = NoteGroupWithInfoService.getGroupChildCountByGroupId(noteGroup.noteGroupId)
        holder.mItemNoteGroupLayoutBinding.itemNoteGroupChildNumberTv.text =
            if (groupSize == 0L) "" else groupSize.toString()

    }


    class NoteGroupRvHolder(val mItemNoteGroupLayoutBinding: ItemNoteGroupLayoutBinding) :
        AbsBaseRvViewHolder(mItemNoteGroupLayoutBinding.root) {

        init {
            itemView.setOnClickListener {
                noteGroupItemListener?.onItemClick(it, adapterPosition)
            }
        }
    }

    /**
     * 设置适配器监听
     */
    fun setAdapterListener(listener: NoteGroupItemListener) {
        noteGroupItemListener = listener
    }


    /**
     * 添加一条数据
     */
    fun addNoteGroup(noteGroup: NoteGroup) {
        list.add(noteGroup)
        notifyItemInserted(list.size - 1)
    }

    /**
     * 添加多条数据
     */
    fun addNoteGroupList(noteGroupList: MutableList<NoteGroup>) {
        list.addAll(noteGroupList)
        notifyDataSetChanged()
    }

    /**
     * 替换全部数据
     */
    fun replaceNoteGroupList(noteGroupList: MutableList<NoteGroup>) {
        list.clear()
        list.addAll(noteGroupList)
        notifyDataSetChanged()
    }

}