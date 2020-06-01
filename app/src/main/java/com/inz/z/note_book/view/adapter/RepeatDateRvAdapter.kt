package com.inz.z.note_book.view.adapter

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.inside.ScheduleWeekDate
import kotlinx.android.synthetic.main.item_repeat_date.view.*

/**
 * 重复日期选择适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/21 15:47.
 */
class RepeatDateRvAdapter(mContext: Context) :
    AbsBaseRvAdapter<Int, RepeatDateRvAdapter.RepeatDateRvViewHolder>(mContext) {

    companion object {
        private const val TAG = "RepeatDateRvAdapter"
    }

    var repeatDateRvAdapterListener: RepeatDateRvAdapterListener? = null

    override fun onCreateVH(parent: ViewGroup, viewType: Int): RepeatDateRvViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_repeat_date, parent, false)
        return RepeatDateRvViewHolder(view)
    }

    override fun onBindVH(holder: RepeatDateRvViewHolder, position: Int) {
        val d = list.get(position)
        var scheduleWeekDate = ScheduleWeekDate.NEVER
        scheduleWeekDate = scheduleWeekDate.getScheduleWeekDate(position + 1)
        holder.textView.text = scheduleWeekDate.getString(mContext)
        holder.checkBox.isChecked = d == 1
    }


    inner class RepeatDateRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView = itemView.item_repeat_date_tv
        var checkBox: CheckBox = itemView.item_repeat_check_box

        init {
            checkBox.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val isChecked = checkBox.isChecked
                list.set(position, if (isChecked) 1 else 0)
                L.i(TAG, "RepeatDateRvViewHolder is Checked : $isChecked")
                repeatDateRvAdapterListener?.onChangeStatus(isChecked, position)
            }
        }
    }

    interface RepeatDateRvAdapterListener {
        /**
         * 改变状态
         */
        fun onChangeStatus(checked: Boolean, position: Int)
    }

}