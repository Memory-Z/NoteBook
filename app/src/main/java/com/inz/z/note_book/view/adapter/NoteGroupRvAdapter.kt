package com.inz.z.note_book.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
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
        fun onItemClick(v: View?, position: Int)
    }

    private var noteGroupItemListener: NoteGroupItemListener? = null

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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        // +bug, 11654, 2022/5/14 , modify, fix memory leak.
        this.noteGroupItemListener = null
    }

    inner class NoteGroupRvHolder(val mItemNoteGroupLayoutBinding: ItemNoteGroupLayoutBinding) :
        AbsBaseRvViewHolder(mItemNoteGroupLayoutBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (RecyclerView.NO_POSITION != position) {
                noteGroupItemListener?.onItemClick(v, bindingAdapterPosition)
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