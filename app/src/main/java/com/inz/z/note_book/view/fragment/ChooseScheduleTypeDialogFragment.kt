package com.inz.z.note_book.view.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.ScheduleType
import com.inz.z.note_book.base.ScheduleTypeValue
import com.inz.z.note_book.databinding.DialogChooseScheduleTypeBinding
import com.inz.z.note_book.util.ClickUtil
import kotlinx.android.synthetic.main.dialog_choose_schedule_type.*

/**
 * 选择 计划类型弹窗
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/19 13:40.
 */
class ChooseScheduleTypeDialogFragment private constructor() : AbsBaseDialogFragment(),
    View.OnClickListener {
    companion object {
        const val TAG = "ChooseScheduleTypeDialogFragment"
        private const val DATA_TYPE_TAG = "type"

        fun getInstant(
            type: Int,
            listener: ChooseScheduleTypeDialogListener
        ): ChooseScheduleTypeDialogFragment {
            val fragment = ChooseScheduleTypeDialogFragment()
            fragment.listener = listener
            val bundle = Bundle()
            bundle.putInt(DATA_TYPE_TAG, type)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var binding: DialogChooseScheduleTypeBinding? = null

    var listener: ChooseScheduleTypeDialogListener? = null

    /**
     * 当前 选中 类型, 默认： 无
     */
    @ScheduleType
    private var currentType: Int = ScheduleTypeValue.NONE


    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteBookAppTheme_Dialog_BottomToTop)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_choose_schedule_type
    }

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun getViewBindingView(): View? {
        binding = DialogChooseScheduleTypeBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {
        binding?.dialogChooseScheduleTypeCancelTv?.setOnClickListener(this)
        binding?.dialogChooseScheduleTypeClockTv?.setOnClickListener(this)
        binding?.dialogChooseScheduleTypeHintTv?.setOnClickListener(this)
        binding?.dialogChooseScheduleTypeLauncherTv?.setOnClickListener(this)
        binding?.dialogChooseScheduleTypeNoneTv?.setOnClickListener(this)
    }

    override fun initData() {
        arguments?.let {
            currentType = it.getInt(DATA_TYPE_TAG, 0)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val lp = attributes
            lp.gravity = Gravity.BOTTOM
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes = lp
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        isCancelable = true
    }

    override fun onClick(v: View?) {
        binding?.let { binding ->
            // 判断是否为 快速 点击
            if (ClickUtil.isFastClick(v)) return
            var type = 0
            when (v?.id) {
                // 取消按钮
                binding.dialogChooseScheduleTypeCancelTv.id -> {
                    type = currentType
                }

                // 弱提示
                binding.dialogChooseScheduleTypeHintTv.id -> {
                    type = ScheduleTypeValue.HINT
                }
                // 启动应用
                binding.dialogChooseScheduleTypeLauncherTv.id -> {
                    type = ScheduleTypeValue.LAUNCHER
                }
                // 闹钟
                binding.dialogChooseScheduleTypeClockTv.id -> {
                    type = ScheduleTypeValue.ALARM
                }
                // 无
                binding.dialogChooseScheduleTypeNoneTv.id -> {
                    type = ScheduleTypeValue.NONE
                }
                else -> {
                    type = currentType
                }
            }
            // 设置选中类型
            listener?.chooseType(type)
        }
    }

    interface ChooseScheduleTypeDialogListener {
        fun chooseType(@ScheduleType type: Int)
    }
}