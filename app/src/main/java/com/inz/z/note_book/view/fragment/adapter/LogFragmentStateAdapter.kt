package com.inz.z.note_book.view.fragment.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.inz.z.base.view.AbsBaseFragment

/**
 * 日志 界面 适配器
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 13:20.
 */
class LogFragmentStateAdapter(fragment: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fragment, behavior) {
    companion object {
        private const val TAG = "LogFragmentRvAdapter"
    }

    var fragmentList: MutableList<AbsBaseFragment> = mutableListOf()

    var titleArray = mutableListOf<String>()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        if (position < titleArray.size) {
            title = titleArray.get(position)
        }

        return title
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 刷新界面列表
     */
    fun refreshFragmentList(fragmentList: List<AbsBaseFragment>) {
        this.fragmentList.clear()
        this.fragmentList.addAll(fragmentList)
        notifyDataSetChanged()
    }


    fun refreshTitleList(titleList: MutableList<String>) {
        this.titleArray = titleList
        notifyDataSetChanged()
    }


}