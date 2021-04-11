package com.inz.z.note_book.view.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentStatePagerAdapter
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.view.activity.listener.MainActivityListener
import com.inz.z.note_book.view.fragment.adapter.LogFragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_log_root.*

/**
 * 日志界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 11:47.
 */
class LogFragment : AbsBaseFragment() {

    companion object {
        private const val TAG = "LogFragment"

        fun getInstant(): LogFragment {
            val fragment = LogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    val mainListener = MainListenerImpl()

    private var tabNameTextArray = arrayListOf<String>()
    private var fragmentAdapter: LogFragmentStateAdapter? = null
//    private var tabLayoutMediator: TabLayoutMediator? = null

    private var logSystemFragment: LogSystemFragment? = null
    private var logOperationFragment: LogOperationFragment? = null


    override fun initWindow() {

    }

    override fun getLayoutId(): Int = R.layout.fragment_log_root

    override fun initView() {
        tabNameTextArray.add(mContext.getString(R.string.system_log))
        tabNameTextArray.add(mContext.getString(R.string.operation_log))
        fm_log_tab_layout?.apply {
            tabNameTextArray.forEach {
                this.addTab(newTab().setText(it))
            }
            setupWithViewPager(fm_log_vp)

        }
        fragmentAdapter = LogFragmentStateAdapter(childFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        fm_log_vp.apply {
            this.adapter = this@LogFragment.fragmentAdapter
        }


//
//        tabLayoutMediator = TabLayoutMediator(fm_log_tab_layout, fm_log_vp2) { tab, position ->
//            tab.badge?.apply {
//                maxCharacterCount = 99
//                number = 2
//            }
//            tab.text = tabNameTextArray.get(position)
//        }
//        tabLayoutMediator?.attach()

        initFragmentList()

    }

    override fun initData() {
    }

    override fun onDetach() {
        super.onDetach()
//        tabLayoutMediator?.detach()
    }


    /* ------------------------------ Fragment List ------------------------------- */

    private fun initFragmentList() {
        val fragmentList = mutableListOf<AbsBaseFragment>()
        logSystemFragment = LogSystemFragment.getInstant()
        fragmentList.add(logSystemFragment!!)
        logOperationFragment = LogOperationFragment.getInstant()
        fragmentList.add(logOperationFragment!!)
        fragmentAdapter?.refreshFragmentList(fragmentList)
        fragmentAdapter?.refreshTitleList(tabNameTextArray)

    }

    /* ------------------------------ Fragment List ------------------------------- */

    /* ====================== MainActivity Listener ====================== */

    /**
     * 主界面监听
     */
    class MainListenerImpl : MainActivityListener {
        override fun onSearchSubmit(search: String?) {
        }

        override fun onSearchChange(search: String?) {
        }
    }

    /* ====================== MainActivity Listener ====================== */


}