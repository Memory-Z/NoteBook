package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.NonNull
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.fragment.LauncherApplicationFragment
import com.inz.z.note_book.view.fragment.NoteNavFragment
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_left_nav_layout.*
import kotlinx.android.synthetic.main.top_search_nav_layout.*

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
    private var contentViewType = ContentViewType.MAIN


    companion object {
        const val TAG = "MainActivity"
    }

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.main_layout
    }

    override fun initView() {
        L.i(TAG, "initView: ")
        drawerLayout = main_note_drawer_layout
        initLeftNavView()
        initContentView()
        targetMainFragment(ContentViewType.MAIN)


        top_search_nav_content_rl.setOnClickListener {
            top_search_nav_search_view.performClick()
            top_search_nav_search_view.isIconified = false
        }
    }

    override fun initData() {

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
        leftMenuViewClickLisntener = LeftMenuViewClickLisntenerImpl()
        main_left_nav_bottom_setting_ll.setOnClickListener {
            val intent = Intent(mContext, SettingActivity::class.java)
            startActivity(intent)
        }
        mln_0_bnl.setOnClickListener(leftMenuViewClickLisntener)
        mln_1_bnl.setOnClickListener(leftMenuViewClickLisntener)
        mln_2_bnl.setOnClickListener(leftMenuViewClickLisntener)
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
            menuInflater.inflate(R.menu.main_more, morePopupMenu!!.menu)
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
     * 主界面布局内容
     */
    enum class ContentViewType {
        MAIN,
        APPLICATION
    }

    /**
     * 切换主界面显示内容
     * @param type 布局内容
     */
    private fun targetMainFragment(@NonNull type: ContentViewType) {
        when (type) {
            ContentViewType.MAIN -> {
                showNoteNavFragment()
            }
            ContentViewType.APPLICATION -> {
                showApplicationListFragment()
            }
            else -> {
                L.w(TAG, "targetMainFragment: no find this type: $type")
            }
        }
        this.contentViewType = type
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
        val manager = supportFragmentManager;
        var launcherFragment =
            manager.findFragmentByTag("LauncherApplicationFragment") as LauncherApplicationFragment?
        if (launcherFragment == null) {
            launcherFragment = LauncherApplicationFragment.getInstant()
        }
        val transaction = manager.beginTransaction()
        if (!launcherFragment.isAdded) {
            transaction.replace(R.id.note_main_fl, launcherFragment, "LauncherApplicationFragment")
        }
        transaction.show(launcherFragment)
        transaction.commitAllowingStateLoss()
    }

    private var leftMenuViewClickLisntener: LeftMenuViewClickLisntenerImpl? = null

    /**
     * 左侧 菜单栏 点击 监听实现
     */
    private inner class LeftMenuViewClickLisntenerImpl : View.OnClickListener {
        override fun onClick(v: View?) {
            if (mContext == null) {
                L.w(TAG, "LeftMenuViewClickLisntenerImpl: onClick -> mContext is null .  ")
                return
            }
            val id = v?.id
            drawerLayout?.closeDrawer(GravityCompat.START)
            when (id) {
                R.id.mln_0_bnl -> {
                    targetMainFragment(ContentViewType.MAIN)
                }
                R.id.mln_1_bnl -> {
                    val intent = Intent(mContext, ScheduleActivity::class.java)
                    val bundle = Bundle()
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                R.id.mln_2_bnl -> {
                    targetMainFragment(ContentViewType.APPLICATION)
                }
                else -> {
                    L.w(TAG, "LeftMenuViewClickLisntenerImpl: onClick -> not find click view. ")
                }
            }
        }
    }


}