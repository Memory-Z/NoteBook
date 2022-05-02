package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.KeyBoardUtils
import com.inz.z.base.util.L
import com.inz.z.base.util.ToastUtil
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
import com.inz.z.note_book.view.dialog.AddNoteInfoDialog
import com.inz.z.note_book.view.fragment.CreateNewGroupDialogFragment
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

    private var binding: ActivityGroupLayoutBinding? = null

    //    private var noteInfoAddBinding: NoteInfoAddSampleLayoutBinding? = null
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
//        QMUIStatusBarHelper.setStatusBarLightMode(this)

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
        binding?.let {
            setSupportActionBar(it.groupTopToolbar)

            it.groupContentNoteInfoRv.layoutManager = LinearLayoutManager(mContext)
            it.groupContentNoteInfoRv.adapter = mNoteInfoRecyclerAdapter
            it.groupContentSrl.setOnRefreshListener {
                // 刷新笔记数据
                loadNoteInfoWithDatabase()
                if (it.groupContentSrl.isRefreshing) {
                    // 刷新成功
                    ToastUtil.showToast(mContext, getString(R.string.fresh_success))
                    it.groupContentSrl.isRefreshing = false
                }
            }
            it.groupAddNoteInfoFab.setOnClickListener(this)
        }
        // 初始化空数据
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
        val groupName: String
        if (isAddNewGroup || currentGroupId.isEmpty()) {
            groupName = getString(R.string.no_title_group).format("")
            L.i(TAG, "get Intent data is Null , $isAddNewGroup and $currentGroupId")
        } else {
            noteGroup = NoteGroupService.findNoteGroupById(currentGroupId)
            groupName =
                noteGroup?.groupName ?: getString(R.string.no_title_group).format("")
//            setNoteInfoListData()
        }
        // 设置标题
        binding?.groupTopToolbar?.title = groupName

    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun setDataBindingView() {
        super.setDataBindingView()
        binding = ActivityGroupLayoutBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果新建分组存在，返回上级
            if (isShowNewGroupDialog()) {
                finish()
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
            // 加载数据
            loadNoteInfoWithDatabase()
        }
    }

    override fun onPause() {
        super.onPause()
        // 如果显示 添加框，则隐藏
        if (getAddNoteInfoIsShow()) {
            targetFabView(false)
        }
    }

    override fun onDestroyData() {
        super.onDestroyData()
        mNoteInfoRecyclerAdapter?.setNoteInfoRvAdapterListener(null)
        mNoteInfoRecyclerAdapter = null
        groupHandler?.removeCallbacksAndMessages(null)
        groupHandler = null
        binding = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.w(TAG, "onClick: this is fast click, ignore ! ")
            return
        }
        when (v?.id) {
            // 浮动按钮 点击
            binding?.groupAddNoteInfoFab?.id -> {
                // 切换显示状态
                targetFabView(false)
//                // 请求焦点
//                noteInfoAddBinding?.noteInfoAddSampleTitleEt?.requestFocus()
            }
            else -> {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 返回
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 空数据界面初始化
     */
    private fun initNoDataView() {
        binding?.groupContentEmptyBndv?.let {
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
        binding?.groupContentEmptyBndv?.startRefresh(message)
    }

    /**
     * 结束加载
     */
    private fun stopLoad(message: String, retry: Boolean = false) {
        binding?.groupContentEmptyBndv?.stopRefresh(message, retry)
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
        binding?.let {
            it.groupContentNoteInfoRv.visibility =
                if (haveData) View.VISIBLE else View.GONE
            it.groupContentEmptyBndv.visibility =
                if (haveData) View.GONE else View.VISIBLE
        }
    }

    /* ========================================= 分组相关 ===================================== */

    /**
     * 显示新添加分组弹窗
     */
    private fun showNewGroupDialog() {
        val manager = supportFragmentManager
        var newGroupDialogFragment =
            manager.findFragmentByTag("NewGroupDialogFragment") as CreateNewGroupDialogFragment?
        if (newGroupDialogFragment == null) {
            newGroupDialogFragment =
                CreateNewGroupDialogFragment.getInstance(NewGroupDialogFragmentListenerImpl())
        }
        newGroupDialogFragment.show(manager, "NewGroupDialogFragment")
    }

    /**
     * 隐藏新添加分组弹窗
     */
    private fun hideNewGroupDialog() {
        val manager = supportFragmentManager
        (manager.findFragmentByTag("NewGroupDialogFragment") as CreateNewGroupDialogFragment?)?.dismiss()
    }

    /**
     * 是否显示 新建分组弹窗
     * @return 是否显示弹窗
     */
    private fun isShowNewGroupDialog(): Boolean {
        if (mContext == null) {
            return false
        }
        val newGroupDialogFragment =
            supportFragmentManager.findFragmentByTag("NewGroupDialogFragment") as CreateNewGroupDialogFragment?
        return newGroupDialogFragment?.isVisible ?: false
    }

    /**
     * 新分组弹窗监听实现
     */
    inner class NewGroupDialogFragmentListenerImpl :
        CreateNewGroupDialogFragment.NewGroupDialogFragmentListener {
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
            // 同步数据
            currentGroupId = noteGroup.noteGroupId
            this@NoteGroupActivity.noteGroup = noteGroup
            // 设置标题
            hideNewGroupDialog()
            binding?.groupTopToolbar?.title = title
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
        return if (noteGroup != null) {
            titleNumber += 1
            getGroupTitle(suffix, titleNumber)
        } else {
            title
        }
    }

    /* ========================================= 分组相关 ===================================== */

    /* ---------------------------------------- 新笔记 ------------------------------------- */

    /**
     * 获取添加内容显示状态。
     * 如果显示切换按钮，表示未显示新建笔记
     */
    private fun getAddNoteInfoIsShow() = binding?.groupAddNoteInfoFab?.isShown == false

    /**
     * 切换Float Button
     */
    private fun targetFabView(isShow: Boolean) {
        binding?.let {
            if (isShow) {
                it.groupAddNoteInfoFab.show()
                it.root.postDelayed({
                    // 隐藏键盘
                    KeyBoardUtils.hideKeyBoardByWindowToken(it.root.context, it.root.windowToken)
                }, 500)

            } else {
                it.groupAddNoteInfoFab.hide()
            }
        }
        // 切换弹窗显示状态
        targetAddNoteInfoDialog(!isShow)
    }

    /**
     * 添加笔记  createNoteInfo
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

    private var addNoteInfoDialog: AddNoteInfoDialog? = null

    /**
     * 切换添加弹窗显示状态。
     * @param show 是否需要显示，。
     */
    private fun targetAddNoteInfoDialog(show: Boolean) {
        L.i(TAG, "targetAddNoteInfoDialog: ---->> show = $show ")
        if (addNoteInfoDialog != null) {
            if (addNoteInfoDialog?.isShowing == true && show) {
                return
            }
            if (!show) {
                addNoteInfoDialog?.hide()
                addNoteInfoDialog = null
                return
            }
        }
        if (!show) {
            return
        }
        addNoteInfoDialog = AddNoteInfoDialog(mContext)
        addNoteInfoDialog?.let {
            it.setOnDismissListener {
                // 显示 底部 添加 按钮
                targetFabView(show)
            }
            // 设置监听。
            it.addNoteInfoListener = AddNoteInfoDialogListenerImpl()
            it.show()
        }
        L.i(TAG, "targetAddNoteInfoDialog: add note info dialog is showing .")
    }

    /**
     * 弹窗监听。
     */
    private inner class AddNoteInfoDialogListenerImpl :
        AddNoteInfoDialog.Companion.AddNoteInfoListener {
        override fun onSubmitClick(v: View?, message: String?) {
            L.i(TAG, "onSubmitClick: message = $message")
            // 获取新 笔记标题内容
            if (message.isNullOrBlank()) {
                L.i(TAG, "onSubmitClick: WARNING title is empty ! ")
                return
            }
            // 添加笔记
            val added = addNoteInfo(currentGroupId, message)
            if (added) {
//                // 添加数据成功
//                targetAddView(false)
                targetFabView(true)
                // 加载笔记信息
                loadNoteInfoWithDatabase()
            }
        }

    }

    /* ---------------------------------------- 笔记相关 ------------------------------------- */

    private inner class GroupHandlerCallbackImpl : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                HANDLER_REFRESH_DATA -> {
                    // 切换内容
                    targetContentView(!noteInfoList.isNullOrEmpty())
                    mNoteInfoRecyclerAdapter?.replaceNoteInfoList(noteInfoList ?: arrayListOf())
                    stopLoad(getString(R.string.loading_finish))
                }
            }
            return true
        }
    }
}