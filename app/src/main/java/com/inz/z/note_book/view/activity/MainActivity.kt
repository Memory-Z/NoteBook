package com.inz.z.note_book.view.activity

import android.content.Intent
import android.graphics.Color
import android.icu.util.LocaleData
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.service.FloatMessageViewService
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.fragment.BaseDialogFragment
import com.inz.z.note_book.view.fragment.LauncherApplicationFragment
import com.inz.z.note_book.view.fragment.LogFragment
import com.inz.z.note_book.view.fragment.NoteNavFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_left_nav_layout.*
import kotlinx.android.synthetic.main.top_search_nav_layout.*
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 主页面
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/17 14:40.
 */
class MainActivity : BaseNoteActivity() {

    private var drawerLayout: DrawerLayout? = null

    /**
     * 右侧更多菜单弹窗
     */
    private var morePopupMenu: PopupMenu? = null

    /**
     * 监听列表
     */
    private val mainListenerMap =
        HashMap<@com.inz.z.note_book.view.activity.MainActivity.ContentViewType Int, MainActivityListener?>()


    companion object {
        const val TAG = "MainActivity"
        private const val REQUEST_OVER_WINDOW_PERMISSION = 0x00FF

        private const val VIEW_TYPE_APPLICATION = 0xA001
        private const val VIEW_TYPE_MAIN = 0xA002
        private const val VIEW_TYPE_LOG = 0xA003
    }

    @IntDef(VIEW_TYPE_APPLICATION, VIEW_TYPE_MAIN, VIEW_TYPE_LOG)
    @Target(
        AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.PROPERTY
    )
    private annotation class ContentViewType {}

    @ContentViewType
    private var viewType: Int = VIEW_TYPE_MAIN

    override fun initWindow() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.main_layout
    }

    override fun initView() {
        L.i(TAG, "initView: ")
        drawerLayout = main_note_drawer_layout
        initLeftNavView()
        initContentView()
        targetMainFragment(VIEW_TYPE_MAIN)


        top_search_nav_content_rl.setOnClickListener {
            top_search_nav_search_view.performClick()
            top_search_nav_search_view.isIconified = false
        }

        top_search_nav_search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val keys = mainListenerMap.keys
                for (type in keys) {
                    if (type == viewType) {
                        mainListenerMap.get(type)?.onSearchSubmit(query)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val keys = mainListenerMap.keys
                for (type in keys) {
                    if (type == viewType) {
                        mainListenerMap.get(type)?.onSearchChange(newText)
                    }
                }
                return true
            }
        })
    }

    override fun initData() {
        showFloatWindowTintDialog()
    }

    override fun needCheckVersion(): Boolean {
        return false
    }

    /**
     * 中间布局
     */
    private fun initContentView() {
        // 点击头像，展开左侧栏
        top_search_nav_scan_iv?.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
        top_searche_nav_end_more_iv?.setOnClickListener {
            showMoreMenuView()
        }
//        top_search_nav_search_view.setIconifiedByDefault(false)
//        top_search_nav_search_view.isIconified = false
    }

    /**
     * 初始化左侧导航视图
     */
    private fun initLeftNavView() {
        leftMenuViewClickListener = LeftMenuViewClickListenerImpl()
        main_left_nav_bottom_setting_ll.setOnClickListener {
            val intent = Intent(mContext, SettingActivity::class.java)
            startActivity(intent)
        }
        mln_0_bnl.setOnClickListener(leftMenuViewClickListener)
        mln_1_bnl.setOnClickListener(leftMenuViewClickListener)
        mln_2_bnl.setOnClickListener(leftMenuViewClickListener)
        mln_3_bnl.setOnClickListener(leftMenuViewClickListener)
        mln_4_bnl.setOnClickListener(leftMenuViewClickListener)
        mln_5_bnl.setOnClickListener(leftMenuViewClickListener)
    }

    /**
     * 显示主页导航页
     */
    private fun showNoteNavFragment() {
        L.i(TAG, "showNoteNavFragment: ")
        if (mContext == null) {
            L.w(TAG, "showNoteNavFragment: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var noteNavFragment = manager.findFragmentByTag("NoteNavFragment") as NoteNavFragment?
        if (noteNavFragment == null) {
            noteNavFragment = NoteNavFragment()
            mainListenerMap.put(VIEW_TYPE_MAIN, noteNavFragment.mainListener)
        }
        val fragmentTransient = manager.beginTransaction()
        if (!noteNavFragment.isAdded) {
            fragmentTransient.replace(R.id.note_main_fl, noteNavFragment, "NoteNavFragment")
        }
        fragmentTransient.show(noteNavFragment)
        fragmentTransient.commitAllowingStateLoss()
    }

    /**
     * 显示更多内容布局
     */
    private fun showMoreMenuView() {
        if (morePopupMenu == null) {
            morePopupMenu = PopupMenu(mContext, top_search_nav_end_rl)
            menuInflater.inflate(R.menu.menu_main_more, morePopupMenu!!.menu)
            morePopupMenu?.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener true
            }
        }
        morePopupMenu?.show()
//        } else {
//            morePopupMenu?.dismiss()
//        }
    }

    /**
     * 切换主界面显示内容
     * @param viewType 布局内容
     */
    private fun targetMainFragment(@NonNull viewType: Int = VIEW_TYPE_MAIN) {
        when (viewType) {
            VIEW_TYPE_MAIN -> {
                showNoteNavFragment()
            }
            VIEW_TYPE_APPLICATION -> {
                showApplicationListFragment()
            }
            VIEW_TYPE_LOG -> {
                showLogFragment()
            }
        }
        this.viewType = viewType
    }

    /**
     * 显示中间界面
     */
    private fun showApplicationListFragment() {
        L.i(TAG, "showApplicationListFragment: ")
        if (mContext == null) {
            L.w(TAG, "showApplicationListFragment: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var launcherFragment =
            manager.findFragmentByTag("LauncherApplicationFragment") as LauncherApplicationFragment?
        if (launcherFragment == null) {
            launcherFragment = LauncherApplicationFragment.getInstant()
            mainListenerMap[VIEW_TYPE_APPLICATION] = launcherFragment.mainListener
        }
        val transaction = manager.beginTransaction()
        if (!launcherFragment.isAdded) {
            transaction.replace(R.id.note_main_fl, launcherFragment, "LauncherApplicationFragment")
        }
        transaction.show(launcherFragment)
        transaction.commitAllowingStateLoss()
    }

    /**
     * 显示日志界面
     */
    private fun showLogFragment() {
        L.i(TAG, "showLogFragment: ")
        if (mContext == null) {
            L.w(TAG, "showLogFragment: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var logFragment = manager.findFragmentByTag("LogFragment") as LogFragment?
        if (logFragment == null) {
            logFragment = LogFragment.getInstant()
            mainListenerMap.put(VIEW_TYPE_LOG, logFragment.mainListener)
        }
        val transaction = manager.beginTransaction()
        if (!logFragment.isAdded) {
            transaction.replace(R.id.note_main_fl, logFragment, "LogFragment")
        }
        transaction.show(logFragment)
        transaction.commitAllowingStateLoss()
    }

    private var leftMenuViewClickListener: LeftMenuViewClickListenerImpl? = null

    /**
     * 左侧 菜单栏 点击 监听实现
     */
    private inner class LeftMenuViewClickListenerImpl : View.OnClickListener {
        override fun onClick(v: View?) {
            if (mContext == null) {
                L.w(TAG, "LeftMenuViewClickLisntenerImpl: onClick -> mContext is null .  ")
                return
            }
            val id = v?.id
            drawerLayout?.closeDrawer(GravityCompat.START)
            when (id) {
                R.id.mln_0_bnl -> {
                    targetMainFragment(VIEW_TYPE_MAIN)
                }
                R.id.mln_1_bnl -> {
                    val intent = Intent(mContext, ScheduleActivity::class.java)
                    val bundle = Bundle()
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                R.id.mln_2_bnl -> {
                    targetMainFragment(VIEW_TYPE_APPLICATION)
                }
                R.id.mln_3_bnl -> {
                    startActivity(Intent(mContext, RecordActivity::class.java))
                }
                R.id.mln_4_bnl -> {
                    startActivity(Intent(mContext, NewDynamicActivity::class.java))
                }
                R.id.mln_5_bnl -> {
                    targetMainFragment(VIEW_TYPE_LOG)
                }
                else -> {
                    L.w(TAG, "LeftMenuViewClickLisntenerImpl: onClick -> not find click view. ")
                }
            }
        }
    }


    /**
     * 是否显示消息浮窗
     */
    private val isShowMessageWindow = AtomicBoolean(false)

    /**
     * 显示浮窗消息窗口
     */
    private fun showFloatMessageWindow() {
        isShowMessageWindow.set(true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                startActivityForResult(intent, REQUEST_OVER_WINDOW_PERMISSION)
            } else {
                startService(Intent(mContext, FloatMessageViewService::class.java))
            }
        } else {
            startService(Intent(mContext, FloatMessageViewService::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVER_WINDOW_PERMISSION) {
            if (resultCode == RESULT_OK) {
                if (isShowMessageWindow.get()) {
                    startService(Intent(mContext, FloatMessageViewService::class.java))
                }
            }
        }
    }

    /**
     * 显示浮窗提示弹窗
     */
    private fun showFloatWindowTintDialog() {
        if (mContext == null) {
            L.w(TAG, "showFloatWindowTintDialog: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var showFloatDialog =
            manager.findFragmentByTag("ShowFloatTintDialog") as BaseDialogFragment?
        if (showFloatDialog == null) {
            BaseDialogFragment
            val builder = BaseDialogFragment.Builder()
            builder.apply {
                setTitle(mContext.getString(R.string._tips))
                setCenterMessage(mContext.getString(R.string.show_float_window_about_countdown))
                setLeftButton(
                    mContext.getString(R.string.cancel),
                    View.OnClickListener {
                        hideFloatWindowTintDialog()
                    }
                )
                setRightButton(
                    mContext.getString(R.string._show),
                    View.OnClickListener {
                        showFloatMessageWindow()
                        hideFloatWindowTintDialog()
                    }
                )
            }
            showFloatDialog = builder.build()
        }
        if (!showFloatDialog.isAdded && !showFloatDialog.isVisible) {
            showFloatDialog.show(manager, "ShowFloatTintDialog")
        }
    }

    /**
     * 隐藏浮窗提示
     */
    private fun hideFloatWindowTintDialog() {
        if (mContext == null) {
            L.w(TAG, "hideFloatWindowTintDialog: mContext is null. ")
            return
        }
        val showFloatDialog =
            supportFragmentManager.findFragmentByTag("ShowFloatTintDialog") as BaseDialogFragment?
        showFloatDialog?.dismissAllowingStateLoss()
    }

}