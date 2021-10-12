package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.KeyBoardUtils
import com.inz.z.base.util.L
import com.inz.z.base.view.widget.BaseNoDataView
import com.inz.z.note_book.R
import com.inz.z.note_book.base.NoteStatus
import com.inz.z.note_book.database.bean.NoteGroup
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.controller.NoteController
import com.inz.z.note_book.database.controller.NoteGroupService
import com.inz.z.note_book.databinding.ActivityGroupLayoutBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.adapter.NoteInfoRecyclerAdapter
import com.inz.z.note_book.view.fragment.NewGroupDialogFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_group_layout.*
import kotlinx.android.synthetic.main.note_info_add_sample_layout.*
import java.util.*

/**
 * 组信息
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/29 11:15.
 */
class NoteGroupActivity : BaseNoteActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "GroupActivity"
        private const val HANDLER_REFRESH_DATA = 0x00A0
    }

    private lateinit var mGroupLayoutBinding: ActivityGroupLayoutBinding
    private var mNoteInfoRecyclerAdapter: NoteInfoRecyclerAdapter? = null

    /**
     * 是否添加新组
     */
    private var isAddNewGroup = false

    /**
     * 当前组Id
     */
    private var currentGroupId = ""

    /**
     * 当前组
     */
    private var noteGroup: NoteGroup? = null

    /**
     * 组内笔记列表
     */
    private var noteInfoList: MutableList<NoteInfo>? = null

    private var groupHandler: Handler? = null

    override fun initWindow() {
    }

    override fun setNavigationBar() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_group_layout
    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)
        group_content_note_info_rv.layoutManager = LinearLayoutManager(mContext)
        mNoteInfoRecyclerAdapter = NoteInfoRecyclerAdapter(mContext)
        mNoteInfoRecyclerAdapter?.setNoteInfoRvAdapterListener(
            object : NoteInfoRecyclerAdapter.NoteInfoRvAdapterListener {
                override fun onItemClickListener(v: View, position: Int) {
                    val noteInfo = noteInfoList?.get(position)
                    val intent = Intent(mContext, NewNoteActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("noteInfoId", noteInfo?.noteInfoId ?: "")
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
        )
        group_content_note_info_rv.adapter = mNoteInfoRecyclerAdapter
        group_top_back_rl.setOnClickListener(this)
        group_content_srl.setOnRefreshListener {
            // 刷新笔记数据
            loadNoteInfoWithDatabase()
            if (group_content_srl.isRefreshing) {
                Toast.makeText(mContext, getString(R.string.fresh_success), Toast.LENGTH_SHORT)
                    .show()
                group_content_srl.isRefreshing = false
            }
        }
        group_add_note_info_fab.setOnClickListener(this)
        initAddNoteView()
        initNoDataView()
        groupHandler = Handler(mainLooper, GroupHandlerCallbackImpl())
    }

    override fun initData() {
        // 通过 Intent 获取传递 的数据信息
        val bundle = intent?.extras
        if (bundle != null) {
            isAddNewGroup = bundle.getBoolean("addNewGroup", false)
            currentGroupId = bundle.getString("groupId", "")
        }
        // 組名
        var groupName: String
        if (isAddNewGroup || currentGroupId.isEmpty()) {
            groupName = getString(R.string.no_title_group).format("")
            L.i(TAG, "get Intent data is Null , $isAddNewGroup and $currentGroupId")
        } else {
            noteGroup = NoteGroupService.findNoteGroupById(currentGroupId)
            groupName =
                noteGroup?.groupName ?: getString(R.string.no_title_group).format("")
//            setNoteInfoListData()
        }

        mGroupLayoutBinding.groupTopTitleTv.text = groupName

    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun setDataBindingView() {
        super.setDataBindingView()
        mGroupLayoutBinding = ActivityGroupLayoutBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (group_bottom_add_note_sample_include.visibility == View.VISIBLE) {
                targetFabView(true)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onResume() {
        super.onResume()
        if (isAddNewGroup || currentGroupId.isEmpty()) {
            // 显示新建弹窗
            showNewGroupDialog()
        } else if (!isAddNewGroup && currentGroupId.isNotEmpty()) {
            loadNoteInfoWithDatabase()
        }
    }

    override fun onDestroyData() {
        super.onDestroyData()
        mNoteInfoRecyclerAdapter?.setNoteInfoRvAdapterListener(null)
        mNoteInfoRecyclerAdapter = null
        groupHandler?.removeCallbacksAndMessages(null)
        groupHandler = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.w(TAG, "onClick: this is fast click, ignore ! ")
            return
        }
        when (v?.id) {
            group_top_back_rl.id -> {
                // 顶部返回按钮
                finish()
            }
            group_add_note_info_fab.id -> {
                // 浮动按钮 添加笔记
                targetFabView(false)
                note_info_add_sample_title_et.requestFocus()
            }
            note_info_add_sample_add_iv.id -> {
                // 笔记添加按钮
                val str = note_info_add_sample_title_et.text.toString()
                if (str.isBlank()) {
                    return
                }
                val added = addNoteInfo(currentGroupId, str)
                if (added) {
                    // 添加数据成功
                    targetAddView(false)
                    targetFabView(true)
                    // 隐藏键盘
                    KeyBoardUtils.hideKeyBoardByWindowToken(v.context, v.windowToken)
                    loadNoteInfoWithDatabase()

                }
            }
            else -> {

            }
        }
    }

    /**
     * 空数据界面初始化
     */
    private fun initNoDataView() {
        group_content_empty_bndv?.let {
            it.listener = NoDataListenerImpl()
            val noDataStr = getString(R.string.no_title_group).format("")
            // 设置提示内容为无
            it.stopRefresh(noDataStr, false)
        }

    }

    /**
     * 空数据接口实现
     */
    private inner class NoDataListenerImpl : BaseNoDataView.BaseNoDataListener {
        override fun onRefreshButtonClick(view: View?) {

        }
    }

    /**
     * 开始加载
     */
    private fun startLoad(message: String) {
        group_content_empty_bndv?.startRefresh(message)
    }

    /**
     * 结束加载
     */
    private fun stopLoad(message: String, retry: Boolean) {
        group_content_empty_bndv?.stopRefresh(message, retry)
    }

    /**
     * 设置笔记列表数据
     */
    private fun loadNoteInfoWithDatabase() {
        startLoad(getString(R.string.loading))
        getWorkThread(TAG + "_loadNoteInfo")
            ?.execute {
                // 加载数据
                noteInfoList =
                    NoteController.findAllNoteInfoByGroupId(currentGroupId).toMutableList()
                groupHandler?.sendEmptyMessage(HANDLER_REFRESH_DATA)
            }
    }

    /**
     * 切换显示内容
     * @param haveData 是否存在数据
     */
    private fun targetContentView(haveData: Boolean) {
        group_content_srl?.visibility =
            if (haveData) View.VISIBLE else View.GONE
        group_content_empty_bndv?.visibility =
            if (haveData) View.GONE else View.VISIBLE
    }

    /* ========================================= 分组相关 ===================================== */

    /**
     * 显示新添加分组弹窗
     */
    private fun showNewGroupDialog() {
        val manager = supportFragmentManager
        var newGroupDialogFragment =
            manager.findFragmentByTag("NewGroupDialogFragment") as NewGroupDialogFragment?
        if (newGroupDialogFragment == null) {
            newGroupDialogFragment =
                NewGroupDialogFragment.getInstance(NewGroupDialogFragmentListenerImpl())
        }
        newGroupDialogFragment.show(manager, "NewGroupDialogFragment")
    }

    /**
     * 隐藏新添加分组弹窗
     */
    private fun hideNewGroupDialog() {
        val manager = supportFragmentManager
        (manager.findFragmentByTag("NewGroupDialogFragment") as NewGroupDialogFragment?)?.dismiss()
    }

    /**
     * 新分组弹窗监听实现
     */
    inner class NewGroupDialogFragmentListenerImpl :
        NewGroupDialogFragment.NewGroupDialogFragmentListener {
        override fun cancelCreate() {
            this@NoteGroupActivity.finish()
        }

        override fun createNewGroup(groupName: String) {
            L.i(TAG, "createNoteGroup name is $groupName !")
            val noteGroup = NoteGroup()
            val noteGroupOrder = NoteGroupService.getLastNoteGroupOrder()
            this@NoteGroupActivity.titleNumber = 0
            val title = getGroupTitle(groupName, this@NoteGroupActivity.titleNumber)
            val currentDate = Date(System.currentTimeMillis())
            noteGroup.apply {
                noteGroupId = UUID.randomUUID().toString()
                setGroupName(title)
                groupOrder = noteGroupOrder + 1
                isCollectValue = 0
                priority = 4
                createDate = currentDate
                updateDate = currentDate
            }
            NoteGroupService.addNoteGroupWithGroupName(noteGroup)
            hideNewGroupDialog()
            // 同步数据
            currentGroupId = noteGroup.noteGroupId
            this@NoteGroupActivity.noteGroup = noteGroup

            mGroupLayoutBinding.groupTopTitleTv.text = title
        }
    }

    /**
     * 标题后缀 序号
     */
    private var titleNumber = 0

    /**
     * 设置标题
     */
    private fun getGroupTitle(suffix: String, order: Int): String {
        var title = suffix
        if (title.isEmpty()) {
            title = getString(R.string.no_title_group).format(suffix)
        }
        if (order > 0) {
            title += " ($order)"
        }
        val noteGroup = NoteGroupService.findNoteGroupByGroupName(title)
        if (noteGroup != null) {
            titleNumber += 1
            return getGroupTitle(suffix, titleNumber)
        } else {
            return title
        }
    }

    /* ========================================= 分组相关 ===================================== */

    /* ---------------------------------------- 新笔记 ------------------------------------- */

    /**
     * 初始化添加笔记布局
     */
    private fun initAddNoteView() {
        note_info_add_sample_title_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                targetAddView(s.isNullOrBlank())
            }
        })

        /**
         * 添加笔记按钮
         */
        note_info_add_sample_add_iv.apply {
            isClickable = false
            setOnClickListener(this@NoteGroupActivity)
        }

    }

    /**
     * 切换添加笔记按钮状态
     * @param isShow  false: 不可点击， true : 可更新
     */
    private fun targetAddView(isShow: Boolean) {
        if (isShow) {
            note_info_add_sample_add_iv.apply {
                isClickable = false
                background = ContextCompat.getDrawable(mContext, R.drawable.bg_card_gray)
            }
        } else {
            note_info_add_sample_add_iv.apply {
                isClickable = true
                background =
                    ContextCompat.getDrawable(mContext, R.drawable.bg_card_main_color)
            }
        }
    }

    /**
     * 切换Float Button
     */
    private fun targetFabView(isShow: Boolean) {
        if (isShow) {
            group_add_note_info_fab.show()
            note_info_add_sample_title_et.setText("")
            group_bottom_add_note_sample_include.visibility = View.GONE
        } else {
            group_add_note_info_fab.hide()
            group_bottom_add_note_sample_include.visibility = View.VISIBLE
        }
    }

    /**
     * 添加笔记
     * @param noteGroupId 组id
     * @param noteTitle 标题
     */
    private fun addNoteInfo(noteGroupId: String, noteTitle: String): Boolean {
        val noteInfo = NoteInfo()
            .apply {
                noteInfoId = BaseTools.getUUID()
                setNoteTitle(noteTitle)
                noteContent = ""
                status = NoteStatus.UNFINISHED
                createDate = Date()
                updateDate = Date()
            }
        return NoteController.addNoteInfo(noteGroupId, noteInfo)
    }

    /* ---------------------------------------- 笔记相关 ------------------------------------- */

    private inner class GroupHandlerCallbackImpl : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                HANDLER_REFRESH_DATA -> {
                    // 切换内容
                    targetContentView(!noteInfoList.isNullOrEmpty())
                    mNoteInfoRecyclerAdapter?.replaceNoteInfoList(noteInfoList ?: arrayListOf())
                    stopLoad(getString(R.string.loading_finish), false)
                }
            }
            return true
        }
    }
}