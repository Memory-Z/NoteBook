package com.inz.z.note_book.view.fragment

import com.inz.z.base.view.AbsBaseFragment
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * 系统文件 信息 界面
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 15:20.
 */
abstract class BaseSysFileFragment : AbsBaseFragment() {

    protected val showList = AtomicBoolean(true)

    abstract fun getInstance(): BaseSysFileFragment

    protected open fun loadStart() {

    }

    protected open fun loadFinish() {

    }

    /**
     * 刷新界面
     */
    open fun refreshView() {

    }

    /**
     * 切换显示模式
     */
    open fun targetShowMode(showList: Boolean) {
        this.showList.set(showList)
    }
}