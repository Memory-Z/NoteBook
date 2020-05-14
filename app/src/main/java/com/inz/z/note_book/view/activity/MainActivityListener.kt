package com.inz.z.note_book.view.activity

/**
 * 主界面 - 监听
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/14 09:35.
 */
interface MainActivityListener {
    /**
     * 搜索
     */
    fun onSearchSubmit(search: String?)

    /**
     * 搜索改变
     */
    fun onSearchChange(search: String?)

}