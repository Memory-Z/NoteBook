package com.inz.z.note_book.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.database.bean.TaskSchedule
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*

/**
 * 计划项 适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 10:17.
 */
class ScheduleRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<TaskSchedule, ScheduleRvAdapter.ScheduleRvViewHolder>(mContext) {
    companion object {
        const val TAG = "ScheduleRvAdapter"
    }

    var listener: ScheduleRvAdapterListener? = null

    init {

    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ScheduleRvViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_schedule, parent, false)
        return ScheduleRvViewHolder(view)
    }

    override fun onBindVH(holder: ScheduleRvViewHolder, position: Int) {
        val schedule = list[position]
        val repeat = schedule.scheduleRepeat
        holder.scheduleRepeatSwitch.isChecked = schedule.scheduleStatue == ScheduleStatus.DOING
        val time = BaseTools.getDateFormat(
            mContext.getString(R.string._date_time_format_h_m),
            Locale.getDefault()
        ).format(schedule.scheduleTime)
        holder.scheduleTimeTv.text = time
        holder.scheduleRepeatDateTv.text = schedule.getScheduleRepeatTimeJsonStr(mContext)
        holder.scheduleTagTv.text = schedule.scheduleTag

    }

    /**
     * item  View
     */
    inner class ScheduleRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView),
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        var scheduleTimeTv: TextView
        var scheduleTagTv: TextView
        var scheduleRepeatSwitch: Switch
        var scheduleRepeatDateTv: TextView

        init {
            scheduleTimeTv = itemView.item_schedule_time_tv
            scheduleTagTv = itemView.item_schedule_type_tv
            scheduleRepeatSwitch = itemView.item_schedule_switch
            scheduleRepeatDateTv = itemView.item_schedule_content_tv
            itemView.setOnClickListener(this)
            scheduleRepeatSwitch.setOnCheckedChangeListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.itemClick(v, position)
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.itemCheckedChanged(buttonView, isChecked, position)
            }
        }
    }

    interface ScheduleRvAdapterListener {
        fun itemClick(v: View?, position: Int)

        fun itemCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean, position: Int)
    }
}