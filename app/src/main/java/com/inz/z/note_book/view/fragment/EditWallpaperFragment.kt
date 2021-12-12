package com.inz.z.note_book.view.fragment

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.util.L
import com.inz.z.base.util.ToastUtil
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.DesktopWallpaperInfo
import com.inz.z.note_book.databinding.FragmentSetWallpaperImageEditBinding
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.viewmodel.DesktopWallpaperViewModel
import com.inz.z.note_book.work.SetDesktopWallpaperWorker

/**
 * 设置 壁纸 界面
 *
 * ====================================================
 * Create by 11654 in 2021/11/4 20:41
 */
class EditWallpaperFragment private constructor() : AbsBaseFragment(), View.OnClickListener {

    companion object {
        private const val TAG = "EditWallpaperFragment"

        /**
         * 获取界面实例
         * @param wallpaperId 壁纸信息ID
         * @see DesktopWallpaperInfo
         * @param listener 监听信息
         */
        fun getInstances(
            wallpaperId: Long? = -1L,
            listener: EditWallpaperFragmentListener?
        ): EditWallpaperFragment {
            val fragment = EditWallpaperFragment()
            val bundle = Bundle()
            bundle.putLong(Constants.WallpaperParams.PARAMS_TAG_WALLPAPER_ID, wallpaperId ?: -1L)
            fragment.arguments = bundle
            fragment.listener = listener
            return fragment
        }
    }

    /**
     * 编辑 壁纸 界面 监听
     */
    interface EditWallpaperFragmentListener {
        /**
         * 选择 文件
         */
        fun pickImage()

        /**
         * 保存 壁纸
         */
        fun saveWallpaper()

        /**
         * 切換至全屏显示
         * @param full 是否全屏
         */
        fun targetFullScreen(full: Boolean)
    }

    private var binding: FragmentSetWallpaperImageEditBinding? = null

    var listener: EditWallpaperFragmentListener? = null

    /**
     * 壁纸信息
     */
    private var wallpaperInfo: DesktopWallpaperInfo? = null

    /**
     * ViewModel
     */
    private var wallpaperViewModel: DesktopWallpaperViewModel? = null

    /**
     * 当前壁纸 ID
     */
    private var currentWallpaperInfoId: Long = -1L

    private var currentFileBean: BaseChooseFileBean? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_set_wallpaper_image_edit
    }

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun getViewBindingView(): View? {
        binding = FragmentSetWallpaperImageEditBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {
        initViewModel()
        binding?.fmSetWallpaperImgAddFab?.setOnClickListener(this)
        binding?.fmSetWallpaperImgBottomSetBtn?.setOnClickListener(this)
    }

    override fun initData() {
        arguments?.apply {
            currentWallpaperInfoId = getLong(Constants.WallpaperParams.PARAMS_TAG_WALLPAPER_ID, -1L)
        }
        // 切换显示内容 : 无内容
        targetContentView(false)
        // 查询数据
        wallpaperViewModel?.findWallpaperById(currentWallpaperInfoId)
    }

    /**
     * 初始化 ViewModel
     */
    private fun initViewModel() {
        L.i(TAG, "initViewModel: ")
        wallpaperViewModel =
            ViewModelProvider(requireActivity()).get(DesktopWallpaperViewModel::class.java)
        wallpaperViewModel?.getCurrentWallpaperInfo()?.observe(
            this,
            Observer {

            }
        )
        // 获取当前 选中图片
        wallpaperViewModel?.getCurrentFileBean()?.observe(
            this,
            Observer {
                L.i(TAG, "initViewModel: fileBean = $it ")
                currentFileBean = it
                // 切换显示内容
                val filePath = it.filePath
                // 切换显示内容 。
                targetContentView(true)
            }

        )
    }

    override fun onDestroy() {
        super.onDestroy()
        wallpaperViewModel = null
        binding = null
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.d(TAG, "onClick: this is fast click. ")
            return
        }
        binding?.let {
            when (v?.id) {

                // 选择 图片
                it.fmSetWallpaperImgAddFab.id -> {
                    listener?.pickImage()
                }
                // 编辑图片
//                it.fmSetWallpaperImgSettingFab.id -> {
//                    targetShowMoreIcon(!it.fmSetWallpaperImgAddFab.isShown)
//                    // TODO: 2021/11/30 show add and edit .
//                }
                // 设置 图片
                it.fmSetWallpaperImgBottomSetBtn.id -> {
                    // 设置壁纸。
                    setDesktopWallpaper()

                }
                else -> {

                }
            }

        }
    }

    /**
     * 切换显示内容
     * @param haveData 是否存在数据信息
     */
    private fun targetContentView(haveData: Boolean) {
        binding?.let {
            // 是否存在数据
            var haveDataTmp = haveData
            // 如果存在数据，查询相应数据详情，进行二次校验
            if (haveData) {
                val fileBean = wallpaperViewModel?.currentChooseFileBeanLiveData?.value
                if (fileBean != null) {
                    val filePath = fileBean.filePath
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    it.fmSetWallpaperImgSiv.setImageBitmap(bitmap)
                } else {
                    haveDataTmp = false
                }
            }
            // 切换显示内容
            it.fmSetWallpaperImgSiv.visibility = if (haveDataTmp) View.VISIBLE else View.GONE
            it.fmSetWallpaperImgContentNullRl.visibility =
                if (haveDataTmp) View.GONE else View.VISIBLE
            // 切换显示 图标
            targetFloatButtonIcon(haveData)
        }
        listener?.targetFullScreen(haveData)

    }

    /**
     * 切换浮动按钮图标
     * @param haveData 是否存在数据
     */
    private fun targetFloatButtonIcon(haveData: Boolean) {
        binding?.let {
            if (haveData) {
                it.fmSetWallpaperImgAddFab.hide()
                // 切换至全屏显示
            } else {
                it.fmSetWallpaperImgAddFab.show()
            }
            it.fmSetWallpaperImgBottomSetLl.visibility = if (haveData) View.VISIBLE else View.GONE
        }
    }


    /**
     * 设置桌面壁纸
     */
    private fun setDesktopWallpaper() {
        binding?.fmSetWallpaperImgSiv?.let {
            L.i(TAG, "setDesktopWallpaper: SETTING -----> ")
            val bitmap = it.getWallpaperBitmap()
            val rectF = it.getWallpaperRect()
            val rectArray = intArrayOf(
                rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()
            )
            // 设置参数
            val data = Data.Builder()
                .putIntArray(SetDesktopWallpaperWorker.IMAGE_RECT_TAG, rectArray)
                .putString(
                    SetDesktopWallpaperWorker.IMAGE_PATH_TAG,
                    wallpaperInfo?.wallpaperPath ?: currentFileBean?.filePath
                )
                .build()
            // 请求
            val request = OneTimeWorkRequestBuilder<SetDesktopWallpaperWorker>()
                .setInputData(data)
                .build()
            // 执行
            val workManager = WorkManager.getInstance(it.context)
            workManager.enqueue(request)
            workManager.getWorkInfoByIdLiveData(request.id)
                .observe(
                    this,
                    Observer { info ->
                        when (info.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                L.i(TAG, "setDesktopWallpaper: Setting wallpaper success! ")
                            }
                            WorkInfo.State.FAILED -> {
                                L.i(TAG, "setDesktopWallpaper: Setting wallpaper FAILURE! ")
                            }
                            WorkInfo.State.RUNNING -> {
                                L.i(TAG, "setDesktopWallpaper: Running ... ")
                            }
                            else -> {
                                L.i(TAG, "setDesktopWallpaper: state = ${info.state}")
                            }
                        }
                    }
                )
        } ?: ToastUtil.showToast("No")
    }
}