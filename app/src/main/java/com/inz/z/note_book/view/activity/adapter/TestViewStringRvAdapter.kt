package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.item_application_info.*
import kotlinx.android.synthetic.main.item_application_info.view.*

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
        val view = mLayoutInflater.inflate(R.layout.item_application_info, null, false)
        return TestViewStringRvHolder(view)
    }

    override fun onBindVH(holder: TestViewStringRvHolder, position: Int) {
        if (position == list.size) {
            holder.nameTv.text = "Add"
            holder.nameTv.setTextColor(Color.GREEN)
            holder.nameTv.setOnClickListener {
                clickListener?.addClick(it)
            }
        } else {
            holder.nameTv.text = list.get(position)
            holder.nameTv.setTextColor(Color.BLACK)
            holder.nameTv.setOnClickListener(null)
        }

    }

    class TestViewStringRvHolder(itemView: View) : AbsBaseRvViewHolder(itemView) {

        public var nameTv: TextView = itemView.findViewById(R.id.item_app_info_name_tv)

        init {
            itemView.item_app_info_icon_iv.visibility = View.GONE
            itemView.item_app_info_right_ic_iv.visibility = View.GONE
        }
    }

    interface ClickListener {
        fun addClick(view: View)
    }
}