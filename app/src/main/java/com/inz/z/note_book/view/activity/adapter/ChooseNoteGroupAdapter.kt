package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.graphics.ColorSpace
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ItemChooseTextLayoutBinding
import com.inz.z.note_book.view.activity.bean.ChooseNoteGroupBean

/**
 * 选择 笔记组 适配器
 *
 * ====================================================
 * Create by 11654 in 2022/6/2 17:02
 */
class ChooseNoteGroupAdapter(mContext: Context?) :
    AbsBaseRvAdapter<ChooseNoteGroupBean, ChooseNoteGroupAdapter.ChooseNoteGroupRvHolder>(mContext) {

    /**
     * 当前选中项
     */
    private var currentCheckPosition = 0

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ChooseNoteGroupRvHolder {
        val binding = ItemChooseTextLayoutBinding.inflate(mLayoutInflater, parent, false)
        return ChooseNoteGroupRvHolder(binding)
    }

    override fun onBindVH(holder: ChooseNoteGroupRvHolder, position: Int) {
        val data = getItemByPosition(position)
        data?.let {
            holder.binding.itemChooseTextContentTv.text = it.group.groupName
            holder.binding.itemChooseTextCheckBox.isChecked = it.checked
            holder.binding.root.setBackgroundResource(
                if (it.checked)
                    R.color.colorPrimary
                else
                    android.R.color.transparent
            )
        }
    }

    override fun refreshData(list: MutableList<ChooseNoteGroupBean>?) {
        super.refreshData(list)
        currentCheckPosition = list?.indexOfFirst {
            it.checked
        } ?: 0
    }

    inner class ChooseNoteGroupRvHolder(val binding: ItemChooseTextLayoutBinding) :
        AbsBaseRvViewHolder(binding.root), View.OnClickListener {

        init {
            binding.itemChooseTextCheckBox.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (RecyclerView.NO_POSITION != bindingAdapterPosition) {
                val clickItem = getItemByPosition(position)
                if (clickItem != null) {
                    // 更新 旧选中数据
                    getItemByPosition(currentCheckPosition)
                        ?.also {
                            it.checked = false
                        }
                        ?.let {
                            updateItemByPosition(it, currentCheckPosition)
                        }
                    // 更新新数据
                    clickItem.checked = true
                    currentCheckPosition = position
                    updateItemByPosition(clickItem, position)

                }
            }
        }
    }

    /**
     * 获取 当前选中数数据项
     */
    fun getSelectedPosition(): Int = currentCheckPosition

}