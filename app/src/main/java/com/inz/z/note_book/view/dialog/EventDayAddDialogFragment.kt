package com.inz.z.note_book.view.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.EventDayFragmentAddSimpleBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.Constants
import java.util.Calendar

/**
 * Event Day Add Content Simple. Fragment.
 *
 * ====================================================
 * Create by 11654 in 2022/8/13 07:27
 */
class EventDayAddDialogFragment private constructor() : AbsBaseDialogFragment(),
    View.OnClickListener {

    companion object {
        private const val TAG = "EventDayAddFragment"

        fun getInstance(
            calendar: Calendar,
            listener: EventDayAddFmListener
        ): EventDayAddDialogFragment {
            val fragment = EventDayAddDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.FragmentParams.EVENT_DAY_CALENDAR_TAG, calendar)
            fragment.arguments = bundle;
            fragment.listener = listener
            return fragment
        }

    }

    interface EventDayAddFmListener {
        /**
         * 保存事件 。
         */
        fun onSaveEvent()
    }

    private var listener: EventDayAddFmListener? = null
    private var binding: EventDayFragmentAddSimpleBinding? = null
    private var selectedCalendar: Calendar? = null

    override fun initWindow() {
    }

    override fun useViewBinding(): Boolean = true

    override fun getViewBindingView(): View? {
        binding = EventDayFragmentAddSimpleBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun getLayoutId(): Int = R.layout.event_day_fragment_add_simple

    override fun initView() {
        binding?.let {
            it.tvEdfAddSave.setOnClickListener(this)
            it.ivEdfAddContentLoop.setOnClickListener(this)
            it.ivEdfAddContentHappenDay.setOnClickListener(this)
            it.ivEdfAddContentSchedule.setOnClickListener(this)
        }
    }

    override fun initData() {
        arguments?.let {
            selectedCalendar =
                it.getSerializable(Constants.FragmentParams.EVENT_DAY_CALENDAR_TAG) as Calendar?
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
            val lp = it.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.BOTTOM
            it.attributes = lp
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.d(TAG, "onClick: this is fast click .")
            return
        }
        val vId = v?.id
        binding?.let { bind ->
            when (vId) {
                // Save
                bind.tvEdfAddSave.id -> {

                }
            }
        }
    }


}