package com.inz.z.note_book.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.FragmentSetWallpaperPreviewBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.view.fragment.adapter.PreviewWallpaperRvAdapter
import com.inz.z.note_book.viewmodel.DesktopWallpaperViewModel

/**
 * 预览 壁纸 界面
 *
 * ====================================================
 * Create by 11654 in 2021/11/6 14:42
 */
class PreviewWallpaperFragment : AbsBaseFragment(), View.OnClickListener {

    companion object {
        private const val TAG = "PreviewWallpaperFragmen"

        /**
         * 获取实例
         * @param listener 监听
         * @see PreviewWallpaperFragmentListener
         */
        fun getInstances(listener: PreviewWallpaperFragmentListener?): PreviewWallpaperFragment {
            val fragment = PreviewWallpaperFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.previewWallpaperFragmentListener = listener
            return fragment
        }
    }

    /**
     * 界面 内容 监听
     */
    interface PreviewWallpaperFragmentListener {
        /**
         * 添加 壁纸 设置
         */
        fun addWallpaperSetting()

        /**
         * 点击编辑 壁纸
         */
        fun onClickEditWallpaper(wallpaperId: Long)
    }

    /**
     * 监听
     */
    var previewWallpaperFragmentListener: PreviewWallpaperFragmentListener? = null

    /**
     * 桌面 壁纸 ViewModel
     */
    private var desktopWallpaperViewModel: DesktopWallpaperViewModel? = null

    private var binding: FragmentSetWallpaperPreviewBinding? = null

    /**
     * 预览 壁纸界面 适配器
     */
    private var previewWallpaperRvAdapter: PreviewWallpaperRvAdapter? = null

    /**
     * 适配器 监听
     */
    private var previewWallpaperRvAdapterListener: PreviewWallpaperRvAdapter.PreviewWallpaperRvAdapterListener? =
        null

    override fun initWindow() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_set_wallpaper_preview
    }

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun getViewBindingView(): View? {
        binding = FragmentSetWallpaperPreviewBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {

        binding?.fmSetWallpaperPreviewSrl?.setOnRefreshListener {
            // onRefresh ing.
            // 查询数据
            desktopWallpaperViewModel?.findWallpaperList()
            // 停止 刷新 动画
            binding?.fmSetWallpaperPreviewSrl?.isRefreshing = false
        }
        binding?.fmSetWallpaperImgPreviewFab?.setOnClickListener {
            // 添加 壁纸
            previewWallpaperFragmentListener?.addWallpaperSetting()
        }
        initViewModel()

        // 设置 适配器
        previewWallpaperRvAdapterListener = PreviewWallpaperRvAdapterListenerImpl()
        previewWallpaperRvAdapter = PreviewWallpaperRvAdapter(mContext)
            .apply {
                listener = previewWallpaperRvAdapterListener
            }
        binding?.fmSetWallpaperPreviewRv?.apply {
            layoutManager = GridLayoutManager(mContext, 2)
            adapter = previewWallpaperRvAdapter
        }

    }

    /**
     * 初始化 ViewModel
     */
    private fun initViewModel() {
        desktopWallpaperViewModel =
            ViewModelProvider(this).get(DesktopWallpaperViewModel::class.java)

        desktopWallpaperViewModel?.getWallpaperInfoList()
            ?.observe(
                this,
                Observer {
                    // TODO: 2021/11/9 getLst
                    // 停止 界面刷新
                    binding?.fmSetWallpaperPreviewSrl?.isRefreshing = false
                }
            )
        // 查询 全部 数据 。
        desktopWallpaperViewModel?.findWallpaperList()
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        previewWallpaperFragmentListener = null
        desktopWallpaperViewModel = null
        binding = null
        previewWallpaperRvAdapter = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) return
        when (v?.id) {
            // 点击 浮动按钮
            R.id.fm_set_wallpaper_img_preview_fab -> {
                previewWallpaperFragmentListener?.addWallpaperSetting()
            }
        }
    }

    /**
     * 切换显示内容
     * @param haveData 是否有数据信息
     * @param isRefreshing 是否正在刷新中
     * @param refreshMessage 刷新提示内容
     */
    private fun targetContentView(
        haveData: Boolean = false,
        isRefreshing: Boolean = false,
        refreshMessage: String = ""
    ) {
        binding?.let {
            it.fmSetWallpaperPreviewRv.visibility = if (haveData) View.VISIBLE else View.GONE
            it.fmSetWallpaperPreviewBndView.let { view ->
                view.visibility = if (haveData) View.GONE else View.GONE

                // 根据当前状态，切换刷新状态
                if (isRefreshing) {
                    view.startRefresh(refreshMessage)
                } else {
                    view.stopRefresh(refreshMessage, false)
                }

            }

        }
    }

    /**
     * 加载壁纸 图片
     */
    private fun loadWallpaperImage() {

    }


    /**
     * 预览 壁纸适配器 实现
     */
    private inner class PreviewWallpaperRvAdapterListenerImpl :
        PreviewWallpaperRvAdapter.PreviewWallpaperRvAdapterListener {
        override fun onItemClick(position: Int, v: View?) {
            TODO("Not yet implemented")
        }

        override fun onItemLongClick(position: Int, v: View?): Boolean {
            return super.onItemLongClick(position, v)
        }
    }

}