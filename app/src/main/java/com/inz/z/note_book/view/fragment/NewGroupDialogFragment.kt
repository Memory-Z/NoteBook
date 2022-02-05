package com.inz.z.note_book.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.DialogAddGroupBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.viewmodel.NoteGroupViewModel

/**
 * 新分组弹窗
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/29 15:46.
 */
class NewGroupDialogFragment : AbsBaseDialogFragment(), View.OnClickListener {

    companion object {

        private const val TAG = "NewGroupDialogFragment"

        /**
         * 获取弹窗实例。
         * @param listener 监听
         */
        fun getInstance(listener: NewGroupDialogFragmentListener): NewGroupDialogFragment {
            return getInstance(null, listener)
        }

        /**
         * 获取弹窗 实例
         * @param 分组 ID
         * @param listener 监听
         */
        fun getInstance(
            groupId: String?,
            listener: NewGroupDialogFragmentListener
        ): NewGroupDialogFragment {
            val mNewGroupDialogFragment = NewGroupDialogFragment()
            val bundle = Bundle()
            bundle.putString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, groupId)
            mNewGroupDialogFragment.arguments = bundle
            mNewGroupDialogFragment.mNewGroupDialogFragmentListener = listener
            return mNewGroupDialogFragment
        }
    }

    /**
     * 新分组状态监听
     */
    interface NewGroupDialogFragmentListener {
        /**
         * 取消创建
         */
        fun cancelCreate()

        /**
         * 创建新分组
         */
        fun createNewGroup(groupName: String)
    }

    private var mNewGroupDialogFragmentListener: NewGroupDialogFragmentListener? = null
    private var binding: DialogAddGroupBinding? = null

    private var noteGroupViewModel: NoteGroupViewModel? = null

    /**
     * 笔记分组 ID
     */
    private var noteGroupId: String? = null


    override fun initWindow() {
        val style = R.style.NoteBookAppTheme_Dialog
        setStyle(DialogFragment.STYLE_NO_FRAME, style)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_add_group
    }

    override fun useViewBinding(): Boolean = true

    override fun getViewBindingView(): View? {
        binding = DialogAddGroupBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {
        binding?.let {
            it.dialogAddGroupTitleEt.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s != null) {
                            if (s.isNotBlank()) {
                                setCreateGroupTextViewStatus(true)
                                return
                            }
                        }
                        setCreateGroupTextViewStatus(false)

                    }
                }
            )
            it.dialogAddGroupBottomCancelTv.setOnClickListener(this)
            it.dialogAddGroupBottomCreateGroupTv.setOnClickListener(this)
        }
        // 初始化 ViewModel
        initViewModel()
    }

    override fun initData() {
        // 获取笔记分组ID
        noteGroupId = arguments?.getString(Constants.NoteBookParams.NOTE_GROUP_ID_TAG, null)

        // 查询笔记分组信息
        noteGroupViewModel?.findNoteGroupById(noteGroupId)
    }

    /**
     * 初始化ViewModel
     */
    private fun initViewModel() {
        noteGroupViewModel = ViewModelProvider(this).get(NoteGroupViewModel::class.java)
        noteGroupViewModel?.getNoteGroup()?.observe(
            this,
            Observer {
                L.i(TAG, "initViewModel: noteGroup = $it ")
                // 获取 分组标题并设置
                val title = it?.groupName ?: ""
                binding?.dialogAddGroupTitleEt?.setText(title)

            }
        )
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        if (window != null) {
            val lp = window.attributes
            lp.gravity = Gravity.CENTER
            window.attributes = lp
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        isCancelable = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mNewGroupDialogFragmentListener = null
        noteGroupViewModel = null
        binding = null
        noteGroupId = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            return
        }
        binding?.let { binding ->
            when (v?.id) {
                // 取消
                binding.dialogAddGroupBottomCancelTv.id -> {
                    if (binding.dialogAddGroupTitleEt.text.isNullOrEmpty()) {
                        return
                    }
                    mNewGroupDialogFragmentListener?.createNewGroup(binding.dialogAddGroupTitleEt.text.toString())
                }
                // 创建 分组
                binding.dialogAddGroupBottomCreateGroupTv.id -> {
                    mNewGroupDialogFragmentListener?.cancelCreate()
                }
                else -> {

                }
            }
        }
    }

    /**
     * 设置 【创建分组】状态
     * @param enable 是否用
     */
    private fun setCreateGroupTextViewStatus(enable: Boolean) {
        binding?.dialogAddGroupBottomCreateGroupTv?.let {
            if (enable) {
                it.apply {
                    setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.colorPrimary
                        )
                    )
                    isFocusable = true
                    isClickable = true
                }
            } else {
                it.apply {
                    setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.textColor50
                        )
                    )
                    isFocusable = false
                    isClickable = false
                }
            }
        }
    }
}