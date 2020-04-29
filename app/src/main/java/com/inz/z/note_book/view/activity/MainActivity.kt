package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
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

    private var noteNavFragment: NoteNavFragment? = null
    private var drawerLayout: DrawerLayout? = null

    /**
     * 右侧更多菜单弹窗
     */
    private var morePopupMenu: PopupMenu? = null


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
        showNoteNavFragment()


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
        top_search_nav_scan_iv.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
        top_searche_nav_end_more_iv.setOnClickListener {
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
    }

    /**
     * 显示主页导航页
     */
    private fun showNoteNavFragment() {
        L.i(TAG, "showNoteNavFragment: ")
        val manager = supportFragmentManager
        val fragmentTransient = manager.beginTransaction()
        noteNavFragment = manager.findFragmentByTag("NoteNavFragment") as NoteNavFragment?
        if (noteNavFragment == null) {
            noteNavFragment = NoteNavFragment()
            fragmentTransient.add(R.id.note_main_fl, noteNavFragment!!, "NoteNavFragment")
        }
        fragmentTransient.show(noteNavFragment!!)
        fragmentTransient.commit()
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

    private var leftMenuViewClickLisntener: LeftMenuViewClickLisntenerImpl? = null

    private inner class LeftMenuViewClickLisntenerImpl : View.OnClickListener {
        override fun onClick(v: View?) {
            if (mContext == null) {
                L.w(TAG, "LeftMenuViewClickLisntenerImpl: onClick -> mContext is null .  ")
                return
            }
            val id = v?.id
            when (id) {
                R.id.mln_0_bnl -> {

                }
                R.id.mln_1_bnl -> {
                    val intent = Intent(mContext, ScheduleActivity::class.java)
                    val bundle = Bundle()
                    intent.putExtras(bundle)
                    startActivity(intent)
                    drawerLayout?.closeDrawer(GravityCompat.START)
                }
            }
        }
    }


}