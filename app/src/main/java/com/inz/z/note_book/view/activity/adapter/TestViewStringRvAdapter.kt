package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ItemApplicationInfoBinding

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/27 16:00.
 */
class TestViewStringRvAdapter :
    AbsBaseRvAdapter<String, TestViewStringRvAdapter.TestViewStringRvHolder> {
    constructor(mContext: Context?) : super(mContext)

    companion object {
    }

    var clickListener: ClickListener? = null

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {

        return super.getItemCount() + 1
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): TestViewStringRvHolder {
        val binding = ItemApplicationInfoBinding.inflate(mLayoutInflater, parent, false)
        return TestViewStringRvHolder(binding)
    }

    override fun onBindVH(holder: TestViewStringRvHolder, position: Int) {
        if (position == list.size) {
            holder.binding.itemAppInfoNameTv.text = "Add"
            holder.binding.itemAppInfoNameTv.setTextColor(Color.GREEN)
            holder.binding.itemAppInfoNameTv.setOnClickListener {
                clickListener?.addClick(it)
            }
        } else {
            holder.binding.itemAppInfoNameTv.text = list.get(position)
            holder.binding.itemAppInfoNameTv.setTextColor(Color.BLACK)
            holder.binding.itemAppInfoNameTv.setOnClickListener(null)
        }

    }

    class TestViewStringRvHolder(val binding: ItemApplicationInfoBinding) :
        AbsBaseRvViewHolder(binding.root) {

        init {
            binding.itemAppInfoIconIv.visibility = View.GONE
            binding.itemAppInfoRightIcIv.visibility = View.GONE
        }
    }

    interface ClickListener {
        fun addClick(view: View)
    }
}