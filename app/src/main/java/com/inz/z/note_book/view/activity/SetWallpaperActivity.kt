package com.inz.z.note_book.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.util.ToastUtil
import com.inz.z.note_book.R
import com.inz.z.note_book.base.BaseLifecycleObserver
import com.inz.z.note_book.base.FragmentContentTypeValue
import com.inz.z.note_book.base.SetWallpaperFragmentContentType
import com.inz.z.note_book.databinding.ActivitySetWallpaperLayoutBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.fragment.EditWallpaperFragment
import com.inz.z.note_book.view.fragment.PreviewWallpaperFragment
import com.inz.z.note_book.viewmodel.DesktopWallpaperViewModel
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 设置 壁纸 界面
 * <ul>
 *     <li>设置壁纸</li>
 *     <li>管理壁纸</li>
 * </ul>
 * ====================================================
 * Create by 11654 in 2021/10/31 20:18
 */
class SetWallpaperActivity : BaseNoteActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "SetWallpaperActivity"

        /**
         * 碎片 前缀
         */
        private const val FRAGMENT_TAG_PREFIX = "FragmentSetWallpaper_"

    }

    /**
     * 设置壁纸权限
     */
    private val REQUEST_SET_WALLPAPER_PERMISSTION = arrayOf(
        Manifest.permission.SET_WALLPAPER
    )

    private var binding: ActivitySetWallpaperLayoutBinding? = null

    /**
     * 桌面壁纸 ViewModel
     */
    private var desktopWallpaperViewModel: DesktopWallpaperViewModel? = null

    /**
     * 当前是否正前往选择图片
     */
    private var currentIsToPickImage: AtomicBoolean? = null

    override fun getLifecycleObserver(): List<BaseLifecycleObserver> {
        return listOf(DesktopWallpaperViewModel())
    }

    override fun initWindow() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_set_wallpaper_layout
    }

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivitySetWallpaperLayoutBinding.inflate(layoutInflater)
            .also {
                setContentView(it.root)
            }
    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)

        setSupportActionBar(binding?.setWallpaperTopToolbar)
        binding?.let {
            it.setWallpaperTopBackIbtn.setOnClickListener(this)
        }

        // 设置 显示 内容 。 预览
        targetContentView(FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_PREVIEW)
        // 初始化 ViewModel
        initViewModel()
    }

    private fun initViewModel() {
        desktopWallpaperViewModel =
            ViewModelProvider(this).get(DesktopWallpaperViewModel::class.java)

        desktopWallpaperViewModel?.getWallpaperInfoList()?.observe(
            this,
            Observer {
                // TODO: 2021/11/9 change list info .
            }
        )
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        // 权限 检查
        checkHaveSetWallpaperPermission()
    }

    override fun onDestroyData() {
        super.onDestroyData()
        binding = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            return
        }
        binding?.let { binding ->
            when (v?.id) {
                // 点击 返回按钮
                binding.setWallpaperTopBackIbtn.id -> {
                    backToPreview()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 顶部 返回 按钮 。
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // 选择图片文件
            Constants.WallpaperParams.CHOOSE_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    L.i(TAG, "onActivityResult: ${data?.data}")
                    data?.data?.let {
                        val list = ProviderUtil.queryImageFileByUri(this, it)
                        L.i(TAG, "onActivityResult: list = ${list.toString()}")
                        // 获取选中项中的第一个
                        val currentChooseFileBean = list?.firstOrNull()
                        // 判断 当前状态 是否前往选择图片
                        if (currentIsToPickImage?.get() == true) {
                            // 更新选中图片至 ViewModel 中;
                            desktopWallpaperViewModel?.currentChooseFileBeanLiveData?.postValue(
                                currentChooseFileBean
                            )
                        }
                    }

                }
            }

            else -> {

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.WallpaperParams.SET_WALLPAPER_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限请求成功
            } else {
                // 仍无权限 .
                // TODO: 2021/12/11 Toast/Snake
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 点击返回按钮 。
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 判断是否显示 编辑界面
            if (isShowEditFragment()) {
                // 切换到预览
                backToPreview()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        L.i(TAG, "onBackPressed: ")
//    }

    /**
     * 切换显示 内容 。
     * @param contentType 显示 内容
     */
    private fun targetContentView(
        @SetWallpaperFragmentContentType contentType: Int = FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_PREVIEW,
        wallpaperId: Long = -1L
    ) {
        L.i(TAG, "targetContentView: contentType = $contentType")
        if (mContext == null) {
            L.w(TAG, "targetContentView: mContext is null. ")
            return
        }
        val fragmentTag = FRAGMENT_TAG_PREFIX + contentType
        val manager = supportFragmentManager

        var fragment = manager.findFragmentByTag(fragmentTag)
        if (fragment == null) {
            when (contentType) {
                // 编辑
                FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_EDIT -> {
                    editWallpaperFragmentListener = EditWallpaperFragmentListenerImpl()
                    fragment =
                        EditWallpaperFragment.getInstances(
                            wallpaperId,
                            editWallpaperFragmentListener
                        )
                }
                // 预览
                // FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_PREVIEW
                else -> {
                    previewWallpaperFragmentListener = PreviewSetWallpaperFragmentListenerImpl()
                    fragment =
                        PreviewWallpaperFragment.getInstances(previewWallpaperFragmentListener)
                }
            }
        }
        // 如果 界面 未显示，显示 界面
        if (!fragment.isAdded && !fragment.isVisible) {
            val transaction = manager.beginTransaction()
            transaction.add(R.id.set_wallpaper_content_fragment, fragment, fragmentTag)
            transaction.commitAllowingStateLoss()
        }
    }

    /**
     * 移除 显示  内容
     */
    private fun removeContentView(@SetWallpaperFragmentContentType contentType: Int) {
        L.i(TAG, "removeContentView: contentType = $contentType")
        if (mContext == null) {
            L.w(TAG, "removeContentView: mContext is null. ")
            return
        }
        val fragmentTag = FRAGMENT_TAG_PREFIX + contentType
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        fragment?.let {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(it)
            transaction.commitAllowingStateLoss()
        }
    }

    /**
     * 是否 显示编辑界面
     * @return 是否显示 ，默认未显示
     */
    private fun isShowEditFragment(): Boolean {
        if (mContext == null) {
            return false
        }
        val fragmentTag =
            FRAGMENT_TAG_PREFIX + FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_EDIT
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        return fragment?.isAdded ?: false
    }

    /**
     * 切换至全屏显示
     * @param isFull 是否全屏显示
     */
    private fun targetFullScreenShow(isFull: Boolean) {
        L.i(TAG, "targetFullScreenShow: --->> isFull -- $isFull ")
        // 隐藏状态栏
        binding?.setWallpaperTopLl?.visibility = if (isFull) View.GONE else View.VISIBLE
        if (isFull) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }

        if (isFull) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.decorView.windowInsetsController?.hide(
                    WindowInsets.Type.statusBars()
                )
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.decorView.windowInsetsController?.show(
                    WindowInsets.Type.statusBars()
                )
            }
        }
        // 显示 返回按钮 。
        binding?.setWallpaperTopBackLl?.visibility = if (isFull) View.VISIBLE else View.GONE
    }


    /**
     * 返回预览
     */
    private fun backToPreview() {
        removeContentView(FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_EDIT)
//        // 设置 显示 内容 。 预览
//        targetContentView(FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_PREVIEW)
        // 显示 非全屏
        targetFullScreenShow(false)
    }

    /**
     * 检查是否拥有 权限，并进行请求
     */
    private fun checkHaveSetWallpaperPermission() {
        if (checkSelfPermission(Manifest.permission.SET_WALLPAPER) == PackageManager.PERMISSION_GRANTED) {
            // 有权限，不处理
            ToastUtil.showToast("已有设置壁纸权限")
        } else {
            // 无权限 请求权限
            requestPermissions(
                REQUEST_SET_WALLPAPER_PERMISSTION,
                Constants.WallpaperParams.SET_WALLPAPER_REQUEST_CODE
            )
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 预览 -----------
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 监听
     */
    private var previewWallpaperFragmentListener: PreviewSetWallpaperFragmentListenerImpl? = null

    /**
     * 预览 界面监听 实现
     */
    private inner class PreviewSetWallpaperFragmentListenerImpl :
        PreviewWallpaperFragment.PreviewWallpaperFragmentListener {
        override fun addWallpaperSetting() {
            L.i(TAG, "addWallpaperSetting: ")
            targetContentView(FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_EDIT)
        }

        override fun onClickEditWallpaper(wallpaperId: Long) {
            // 切换 显示内容
            L.d(TAG, "onClickEditWallpaper: wallpaperId = $wallpaperId ")
            targetContentView(
                FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_SET_WALLPAPER_EDIT,
                wallpaperId
            )
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 编辑 -------
    ///////////////////////////////////////////////////////////////////////////

    private var editWallpaperFragmentListener: EditWallpaperFragmentListenerImpl? = null

    /**
     * 编辑 壁纸监听 实现
     */
    private inner class EditWallpaperFragmentListenerImpl :
        EditWallpaperFragment.EditWallpaperFragmentListener {
        override fun pickImage() {
            // 设置 当前 状态 。
            if (currentIsToPickImage == null) {
                currentIsToPickImage = AtomicBoolean(true)
            }
            // 跳转选择图片
            val intent = Intent()
                .apply {
                    action = Intent.ACTION_PICK
                    data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    type = "image/*"
                }
            this@SetWallpaperActivity.startActivityForResult(
                intent,
                Constants.WallpaperParams.CHOOSE_IMAGE_REQUEST_CODE
            )
        }

        override fun saveWallpaper() {

        }

        override fun targetFullScreen(full: Boolean) {
            // 切换全屏显示
            targetFullScreenShow(full)
        }
    }
}