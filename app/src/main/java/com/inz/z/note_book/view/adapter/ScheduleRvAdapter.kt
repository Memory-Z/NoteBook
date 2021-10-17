package com.inz.z.note_book.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.inside.ScheduleStatus
import com.inz.z.note_book.database.bean.TaskSchedule
import com.inz.z.note_book.databinding.ItemScheduleBinding
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
        private const val TAG = "ScheduleRvAdapter"
    }

    /**
     * 颜色状态
     */
    private var colorStateListDoing: ColorStateList? = null
    private var colorStateListUndo: ColorStateList? = null
    private var colorStateListDone: ColorStateList? = null

    /**
     * 监听
     */
    var listener: ScheduleRvAdapterListener? = null

    init {
        initColorStateList()
    }

    private fun initColorStateList() {
        mContext?.let {
            colorStateListDoing = ContextCompat.getColorStateList(it, R.color.colorAccent)
            colorStateListUndo = ContextCompat.getColorStateList(it, R.color.colorPrimary)
            colorStateListDone = ContextCompat.getColorStateList(it, R.color.threadColor)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        L.w(TAG, "onDetachedFromRecyclerView: --->> END ")
        colorStateListDone = null
        colorStateListUndo = null
        colorStateListDoing = null
        mContext = null
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ScheduleRvViewHolder {
//        val view = mLayoutInflater.inflate(R.layout.item_schedule, parent, false)
        val binding = ItemScheduleBinding.inflate(mLayoutInflater, parent, false)
        return ScheduleRvViewHolder(binding)
    }

    override fun onBindVH(holder: ScheduleRvViewHolder, position: Int) {
        val schedule = getItemByPosition(position)
        schedule?.let {
            // 计划是否 重复
            val repeat = it.scheduleRepeat
            val color: ColorStateList?
            val stateStrId: Int?
            when (it.scheduleStatus) {
                ScheduleStatus.DOING -> {
                    color = colorStateListDoing
                    stateStrId = R.string.schedule_state_doing
                }
                ScheduleStatus.NOT_STARTED -> {
                    color = colorStateListUndo
                    stateStrId = R.string.schedule_state_undo
                }
                ScheduleStatus.FINISHED -> {
                    color = colorStateListDone
                    stateStrId = R.string.schedule_state_done
                }
                else -> {
                    color = colorStateListDone
                    stateStrId = R.string.schedule_state_done
                }
            }
            holder.scheduleStateView.backgroundTintList = color
            holder.scheduleStateTv.let {
                it.text = it.context.getString(stateStrId)
                it.setTextColor(color)
            }

            // 获取时间
            holder.scheduleTimeTv.let { tv ->
                val time = getTimeStr(it.scheduleTime, tv.context)
                tv.text = time
            }
            // 设置 重复时间
            holder.scheduleRepeatDateTv.let { tv ->
                tv.text = it.getScheduleRepeatTimeJsonStr(tv.context)
            }
            // 计划类型。
            holder.scheduleTagTv.text = it.scheduleTag

        }
    }

    /**
     * 获取时间 字符串
     * @param date 日期
     */
    private fun getTimeStr(date: Date, context: Context): String =
        BaseTools.getDateFormat(
            context.getString(R.string._date_time_format_h_m),
            Locale.getDefault()
        ).format(date)

    /**
     * item  View
     */
    inner class ScheduleRvViewHolder(binding: ItemScheduleBinding) :
        AbsBaseRvViewHolder(binding.root),
        View.OnClickListener {
        var scheduleTimeTv: TextView = binding.itemScheduleTimeTv
        var scheduleTagTv: TextView = binding.itemScheduleTypeTv
        var scheduleRepeatDateTv: TextView = binding.itemScheduleContentTv
        var scheduleStateView: View = binding.itemScheduleContentRootCardLeftView
        var scheduleStateTv: TextView = binding.itemScheduleContentStateTv

        init {
            binding.itemScheduleContentRootCardView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.itemClick(v, position)
            }
        }

    }

    interface ScheduleRvAdapterListener {
        fun itemClick(v: View?, position: Int)
    }
}