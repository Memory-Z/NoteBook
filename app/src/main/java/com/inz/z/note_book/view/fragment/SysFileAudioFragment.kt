package com.inz.z.note_book.view.fragment

import com.inz.z.note_book.R

/**
 * 系统 音频文件
 *
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 15:42.
 */
class SysFileAudioFragment : BaseSysFileFragment() {
    override fun initWindow() {

    }

    override fun getInstance(): BaseSysFileFragment {
        val fragment = SysFileAudioFragment()
        return fragment
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sys_file_image
    }

    override fun initView() {

    }

    override fun initData() {

    }
}