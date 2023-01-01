package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.note_book.databinding.MainMenuRvItemBinding
import com.inz.z.note_book.view.activity.bean.MainMenuInfo

/**
 * Main Left Menu Recycler View Adapter.
 *
 * ====================================================
 * Create by 11654 in 2023/1/1 15:27
 */
class MainMenuRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<MainMenuInfo, MainMenuRvAdapter.MainMenuRvHolder>(
        mContext
    ) {

    var listener: MainMenuRvAdapterListener? = null

    override fun onCreateVH(parent: ViewGroup, viewType: Int): MainMenuRvHolder {
        val itemBinding = MainMenuRvItemBinding.inflate(mLayoutInflater, parent, false)
        return MainMenuRvHolder(itemBinding)
    }

    override fun onBindVH(holder: MainMenuRvHolder, position: Int) {
        val item = getItemByPosition(position)
        item?.let {
            holder.itemBinding.mlnMainMenuRvItem.setTitle(it.menuTitle)
            holder.itemBinding.mlnMainMenuRvItem.setIconDrawableRes(it.iconId)
        }
    }

    inner class MainMenuRvHolder(val itemBinding: MainMenuRvItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        init {
            itemBinding.mlnMainMenuRvItem.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition
            if (RecyclerView.NO_POSITION != position) {
                listener?.onItemClick(v, position)
            }
        }
    }

    interface MainMenuRvAdapterListener {
        /**
         * Item Click
         * @param v View
         * @param position  Position
         */
        fun onItemClick(v: View?, position: Int)
    }


}