package com.inz.z.note_book.view.activity

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.databinding.ActivityMainBinding
import com.inz.z.note_book.databinding.MainLeftNavFragmentLayoutBinding
import com.inz.z.note_book.databinding.TopSearchNavLayoutBinding
import com.inz.z.note_book.service.CreateLovePanelService
import com.inz.z.note_book.service.FloatMessageViewService
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.activity.listener.MainActivityListener
import com.inz.z.note_book.view.dialog.BaseDialogFragment
import com.inz.z.note_book.view.fragment.LauncherApplicationFragment
import com.inz.z.note_book.view.fragment.LogFragment
import com.inz.z.note_book.view.fragment.NoteNavFragment
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

    private var mainBinding: ActivityMainBinding? = null
    private var leftBinding: MainLeftNavFragmentLayoutBinding? = null
    private var topBinding: TopSearchNavLayoutBinding? = null

    /**
     * 监听列表
     */
    private val mainListenerMap =
        HashMap<Int, MainActivityListener?>()


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
//        QMUIStatusBarHelper.setStatusBarLightMode(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
            .apply {
                setContentView(root)
                leftBinding = noteMainLeftInc
                topBinding = mainNoteNavTopInclude
            }
    }

    override fun initView() {
        L.i(TAG, "initView: ")
        drawerLayout = mainBinding?.mainNoteDrawerLayout
        initLeftNavView()
        initContentView()
        targetMainFragment(VIEW_TYPE_MAIN)


        topBinding?.topSearchNavContentRl?.setOnClickListener {
            topBinding?.let {
                it.topSearchNavSearchView.performClick()
                it.topSearchNavSearchView.isIconified = false
            }
        }

        topBinding?.topSearchNavSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
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
//        showFloatWindowTintDialog()
    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        mainBinding = null
        leftBinding = null
        topBinding = null
    }

    override fun needCheckVersion(): Boolean {
        return false
    }

    /**
     * 中间布局
     */
    private fun initContentView() {
        // 点击头像，展开左侧栏
        topBinding?.topSearchNavScanIv?.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
        topBinding?.topSearcheNavEndMoreIv?.setOnClickListener {
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
        leftBinding?.let {
            it.mainLeftNavBottomSettingLl.setOnClickListener {
                val intent = Intent(mContext, SettingActivity::class.java)
                startActivity(intent)
            }
            it.mln0Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mln1Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mln2Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mln3Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mln4Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mln5Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mln6Bnl.setOnClickListener(leftMenuViewClickListener)
            it.mlnSetWallpaperMlnil.setOnClickListener(leftMenuViewClickListener)
        }
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
            noteNavFragment = NoteNavFragment.getInstance(NoteNavFragmentListenerImpl())
            mainListenerMap[VIEW_TYPE_MAIN] = noteNavFragment.mainListener
        }
        val fragmentTransient = manager.beginTransaction()
        if (!noteNavFragment.isAdded) {
            fragmentTransient.replace(R.id.note_main_fl, noteNavFragment, "NoteNavFragment")
        }
        fragmentTransient.show(noteNavFragment)
        fragmentTransient.commitAllowingStateLoss()
    }

    inner class NoteNavFragmentListenerImpl : NoteNavFragment.NoteNavFragmentListener {
        override fun gotoNoteGroup(intent: Intent) {
            startActivity(intent)
        }
    }

    /**
     * 显示更多内容布局
     */
    private fun showMoreMenuView() {
        if (morePopupMenu == null) {
            morePopupMenu = PopupMenu(mContext, topBinding?.topSearchNavEndRl)
            menuInflater.inflate(R.menu.menu_main_more, morePopupMenu!!.menu)
            morePopupMenu?.setOnMenuItemClickListener {
                when (it.itemId) {
                    // 手动备份。
                    R.id.main_more_backup_item -> {
                        GreenDaoHelper.getInstance().backupDatabase()
                    }
                    // 分享
                    R.id.main_more_share_item -> {

                    }
                    // 生成 LovePanel
                    R.id.main_more_create_love_panel_item -> {
                        val intent = Intent(mContext, CreateLovePanelService::class.java)
                        startService(intent)
                    }
                    // 跳转至设置壁纸
                    R.id.main_more_set_wallpaper_item -> {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SET_WALLPAPER
                        intent.data =
                            Uri.parse("content://media/external_primary/images/media/91886")
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.`package` = packageName
                        intent.flags =
                            Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_RECEIVER_FOREGROUND)
                        val componentName = ComponentName(
                            "com.oplus.wallpapers",
                            "com.oplus.wallpapers.wallpaperpreview.WallpaperPreviewActivity"
                        )
                        intent.component = componentName
                        startActivity(intent)
                    }
                    else -> {

                    }
                }
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
     * 显示应用界面
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
                L.w(TAG, "LeftMenuViewClickListenerImpl: onClick -> mContext is null .  ")
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
                R.id.mln_6_bnl -> {
                    startActivity(Intent(mContext, SystemFileActivity::class.java))
                }
                // 设置 系统壁纸
                R.id.mln_set_wallpaper_mlnil -> {
                    startActivity(Intent(mContext, SetWallpaperActivity::class.java))
                }
                else -> {
                    L.w(TAG, "LeftMenuViewClickListenerImpl: onClick -> not find click view. ")
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