package com.inz.z.note_book.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.FragmentContentTypeValue
import com.inz.z.note_book.database.bean.NoteGroup
import com.inz.z.note_book.database.bean.NoteInfo
import com.inz.z.note_book.database.controller.NoteGroupService
import com.inz.z.note_book.database.controller.NoteInfoController
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.view.activity.AddContentActivity
import com.inz.z.note_book.view.activity.MarkDayActivity
import com.inz.z.note_book.view.activity.NoteGroupActivity
import com.inz.z.note_book.view.activity.listener.MainActivityListener
import com.inz.z.note_book.view.adapter.NoteGroupRvAdapter
import com.inz.z.note_book.view.widget.ItemSampleNoteInfoLayout
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.note_nav_hint_layout.*
import kotlinx.android.synthetic.main.note_nav_layout.*
import java.util.*

/**
 * 首页导航页
 * @see R.layout.note_nav_layout
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/25 11:48.
 */
class NoteNavFragment : AbsBaseFragment(), View.OnClickListener {

    companion object {
        private const val TAG = "NoteNavFragment"

        /**
         * 显示笔记信息数量
         */
        private const val SHOW_NOTE_INFO_NUMBER = 5
        private const val NOTE_NAV_GET_NOTE_GROUP = 0x0001
        private const val NOTE_NAV_GET_NOTE_INFO = 0x0002
    }

    private lateinit var mNoteGroupLayoutManager: LinearLayoutManager

    /**
     * noteGroup 适配器
     */
    private var mNoteGroupRvAdapter: NoteGroupRvAdapter? = null

    /**
     * handler
     */
    private lateinit var mNoteNavHandler: Handler

    /**
     * 显示笔记数量
     */
    private var showNoteInfoNumber = SHOW_NOTE_INFO_NUMBER

    /**
     * 笔记组列表
     */
    private var noteGroupList: MutableList<NoteGroup>? = null

    /**
     * 屏幕中间提示。
     */
    private var hintNovDateLl: LinearLayout? = null

    val mainListener = MainListenerImpl()

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.note_nav_layout
    }

    override fun initView() {
        hintNovDateLl = note_nav_hint_data_center_ll
        hintNovDateLl?.apply {
            this.setOnClickListener(this@NoteNavFragment)
        }
        mNoteGroupRvAdapter = NoteGroupRvAdapter(mContext)
            .apply {
                setAdapterListener(object : NoteGroupRvAdapter.NoteGroupItemListener {
                    override fun onItemClick(v: View, position: Int) {
                        val noteGroup = noteGroupList?.get(position)
                        L.i(TAG, "noteGroupRvAdapter $position is Click , noteGroup = $noteGroup")
                        // 跳转至 Note 组界面
                        if (noteGroup != null) {
                            gotoGroupActivity(false, noteGroup.noteGroupId)
                        }
                    }
                })
            }

        mNoteGroupLayoutManager = LinearLayoutManager(mContext)
            .apply {
                orientation = RecyclerView.VERTICAL
            }
        note_nav_group_rv.apply {
            layoutManager = mNoteGroupLayoutManager
            adapter = mNoteGroupRvAdapter
            isNestedScrollingEnabled = false
        }

        note_nav_content_nsv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
            if (v.scrollY == 0) {
                note_nav_add_fab.show()
            } else {
                note_nav_add_fab.hide()
            }
        })
        // 最近 五条 记录 右侧按钮 点击，
        note_nav_near_five_nav_right_iv.setOnClickListener(this)
        // 设置笔记导航栏组 右侧 箭头 点击
        note_nav_group_right_iv.setOnClickListener(this)
    }

    override fun initData() {
        mNoteNavHandler = Handler(NoteNavHandlerCallback())

        val date = Calendar.getInstance(Locale.getDefault()).time
        setDateText(date)


        note_nav_hint_ibtn.setOnClickListener {
            val packageName = "com.netease.cloudmusic"
            try {
                LauncherHelper.launcherPackageName(mContext, packageName)
            } catch (e: Exception) {
                if (mContext != null) {
                    Toast.makeText(
                        mContext,
                        getString(R.string._launcher_failure_format).format(packageName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

//        note_nav_add_fab.setOnClickListener {
//            val tv = TextView(mContext)
//            val str = "notification . ! ${System.currentTimeMillis()}"
//            tv.text = str
//            tv.setPadding(4, 4, 4, 4)
//            note_nav_content_base_rl.showHeaderNotification(tv, -1)
//
//        }

    }

    override fun onResume() {
        super.onResume()
        setNoteInfoListView()
//        setNoteGroupListView()
        noteGroupList = NoteGroupService.findAll() as MutableList<NoteGroup>
        mNoteGroupRvAdapter?.replaceNoteGroupList(noteGroupList!!)
        // 启动时检测，确认当前时间
        checkDateText(0)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (checkDataRunnable != null) {
            checkDataRunnable = null
        }
        mNoteNavHandler.removeCallbacksAndMessages(null)
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.w(TAG, "onClick: this is fast click. ignore !  ")
            return
        }
        when (v?.id) {
            hintNovDateLl?.id -> {
                //  点击 时间。跳转至纪念日界面，
                val intent = Intent(v?.context, MarkDayActivity::class.java)
                startActivity(intent)
            }
            note_nav_group_right_iv.id -> {
                // 点击 nav 右侧按钮，跳转至组
                L.i(TAG, "note_nav_group_right_iv  is Click ! ")
                gotoGroupActivity(true, "")
            }
            note_nav_near_five_nav_right_iv.id -> {
                // 最近五条记录 右侧按钮点击 ，跳转至添加
                val intent = Intent(mContext, AddContentActivity::class.java)
                intent.putExtras(AddContentActivity.getInstanceBundle(FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_NOTE_TAG))
                startActivity(intent)
            }
            else -> {

            }
        }
    }

    /**
     * 检测日期
     */
    private fun checkDateText(delay: Long) {
        L.i(TAG, "checkDateText: ---- ${delay}. ")
        if (this.isHidden || this.isRemoving || this.isDetached || !this.isVisible) {
            checkDataRunnable = null
            return
        }
        if (checkDataRunnable == null) {
            checkDataRunnable = CheckDataRunnable()
        }
        note_nav_hint_data_tv.postDelayed(checkDataRunnable, delay)
    }

    /**
     * 设置日期
     */
    private fun setDateText(date: Date) {
        note_nav_hint_year_tv.text = getString(R.string.base_format_year_month).format(date)
        note_nav_hint_data_tv.text = getString(R.string.base_format_day).format(date)
        note_nav_hint_week_tv.text = getString(R.string.base_format_week).format(date)
    }

    /**
     * 检测时间线程
     */
    private var checkDataRunnable: Runnable? = null


    /**
     * 检查数据线程
     */
    private inner class CheckDataRunnable : Runnable {

        override fun run() {
            if (mContext == null) {
                return
            }
            var hour: Int
            var minute: Int
            var seconds: Int
            val date = Calendar.getInstance(Locale.getDefault())
                .apply {
                    hour = get(Calendar.HOUR_OF_DAY)
                    minute = get(Calendar.MINUTE)
                    seconds = get(Calendar.SECOND)
                }.time
            when {
                hour < 22 -> // 小于 22 点。 每两小时检测一次
                    checkDateText(2 * 60 * 60 * 1000)
                hour < 23 -> // 小于 23 点。每一小时检测一次
                    checkDateText(60 * 60 * 1000)
                minute < 50 -> // 小于 23点50 分，每 10 分检测一次
                    checkDateText(10 * 60 * 1000)
                minute < 55 -> // 小于 23 点55 分每5分执行这一次
                    checkDateText(5 * 60 * 1000)
                minute < 59 -> // 小于 23 点59 分，每 1 分钟执行一次
                    checkDateText(60 * 1000)
                seconds < 50 -> // 小于 23 点 59 分 50s 每 10s 执行一次
                    checkDateText(10 * 1000)
                else -> {
                    setDateText(date)
                    // 否则，每秒执行一次
                    checkDateText(1000)
                }
            }
        }
    }

    /**
     * 设置笔记信息列表
     */
    private fun setNoteInfoListView() {
        if (activity == null) {
            return
        }
        Observable
            .create(ObservableOnSubscribe<List<NoteInfo>?> {
                val noteInfoList =
                    NoteInfoController.findAllNoteInfoListWithLimit(showNoteInfoNumber)
                it.onNext(noteInfoList)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : DefaultObserver<List<NoteInfo>?>() {
                override fun onComplete() {
                }

                override fun onNext(t: List<NoteInfo>) {
                    note_nav_near_five_content_ll.removeAllViews()
                    if (t.isNotEmpty()) {
                        val lp = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        for (noteInfo in t) {
                            if (mContext != null) {
                                val itemSampleNoteInfoLayout = ItemSampleNoteInfoLayout(mContext)
                                itemSampleNoteInfoLayout.setSampleNoteInfo(noteInfo)
                                itemSampleNoteInfoLayout.setSampleOnClickListener(View.OnClickListener {
                                    L.i(TAG, "itemSampleNoteInfoLayout is click. ")
                                })
                                note_nav_near_five_content_ll.addView(itemSampleNoteInfoLayout, lp)
                            }
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }
            })

    }
//
//    /**
//     * 组名后缀号
//     */
//    private var groupNumberCount = 0
//
//    /**
//     * 设置组信息列表 View
//     */
//    @Deprecated(" Use RecyclerView replace this one by one to add. ")
//    private fun setNoteGroupListView() {
//        note_nav_group_content_ll.removeAllViews()
//        val noteGroupList = getAllNoteGroup()
//        if (noteGroupList.isNotEmpty()) {
//            val lp = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            lp.height = BaseTools.dp2px(mContext, 48F)
//
//            for (noteGroup in noteGroupList) {
//                groupNumberCount += 1
//                if (mContext != null) {
//                    val itemNoteGroupLayout = ItemNoteGroupLayout(mContext)
//                    itemNoteGroupLayout.apply {
//                        setGroupData(noteGroup)
//                        setGroupOnClickListener(View.OnClickListener {
//                            L.i(TAG, "itemNoteGroupLayout is click ! ")
//                            gotoGroupActivity(false, noteGroup.noteGroupId)
//                        })
//                        background = ContextCompat.getDrawable(mContext, R.drawable.bg_layout_click)
//                        isClickable = true
//                        isFocusable = true
//                    }
//                    note_nav_group_content_ll.addView(itemNoteGroupLayout, lp)
//                }
//            }
//        }
//    }
//
//    /**
//     * 查询全部分组
//     */
//    private fun getAllNoteGroup(): List<NoteGroup> {
//        val application = activity?.applicationContext as NoteBookApplication
//        val noteGroupDao = application.getDaoSession()?.noteGroupDao
//        if (noteGroupDao != null) {
//            return noteGroupDao.loadAll()
//        }
//        return emptyList()
//    }

    /**
     * 跳转至组界面
     * @param addNewGroup 是否为新增组
     * @param groupId 组ID
     */
    private fun gotoGroupActivity(addNewGroup: Boolean, groupId: String) {
        val intent = Intent(mContext, NoteGroupActivity::class.java)
        val bundle = Bundle()
        bundle.apply {
            putBoolean("addNewGroup", addNewGroup)
            putString("groupId", groupId)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }


    /**
     * 导航 Handler callback
     */
    private inner class NoteNavHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (mContext == null) {
                return true
            }
            when (msg.what) {
                NOTE_NAV_GET_NOTE_INFO -> {

                }
                NOTE_NAV_GET_NOTE_GROUP -> {

                }
                else -> {

                }
            }
            return true
        }
    }

    /**
     * MainActivityListener implement
     */
    inner class MainListenerImpl : MainActivityListener {
        override fun onSearchSubmit(search: String?) {

        }

        override fun onSearchChange(search: String?) {

        }
    }

}