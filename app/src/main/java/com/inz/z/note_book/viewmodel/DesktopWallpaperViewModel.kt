package com.inz.z.note_book.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.util.L
import com.inz.z.note_book.base.BaseLifecycleObserver
import com.inz.z.note_book.database.bean.DesktopWallpaperInfo
import com.inz.z.note_book.database.controller.DesktopWallpaperController

/**
 * 桌面 壁纸 ViewModel
 * # 并监听生命周期
 *
 * ====================================================
 * Create by 11654 in 2021/11/9 21:19
 */
class DesktopWallpaperViewModel : ViewModel(), BaseLifecycleObserver {
    companion object {
        private const val TAG = "DesktopWallpaperViewModel"
    }

    /**
     * 全部壁纸信息
     */
    private var wallpaperInfoListLiveData: MutableLiveData<MutableList<DesktopWallpaperInfo>>? =
        null

    /**
     * 当前选中 壁纸信息
     */
    private var currentPaperInfoLiveData: MutableLiveData<DesktopWallpaperInfo>? = null

    /**
     * 当前 选中的 图片文件信息
     */
    private var currentChooseFileBeanLiveData: MutableLiveData<BaseChooseFileBean>? = null

    /**
     * 获取 壁纸信息列表
     */
    fun getWallpaperInfoList(): MutableLiveData<MutableList<DesktopWallpaperInfo>>? {
        if (wallpaperInfoListLiveData == null) {
            wallpaperInfoListLiveData = MutableLiveData()
        }
        return wallpaperInfoListLiveData
    }

    /**
     * 获取当前 壁纸信息
     */
    fun getCurrentWallpaperInfo(): MutableLiveData<DesktopWallpaperInfo>? {
        if (currentPaperInfoLiveData == null) {
            currentPaperInfoLiveData = MutableLiveData()
        }
        return currentPaperInfoLiveData
    }

    /**
     * 获取当前文件信息
     */
    fun getCurrentFileBean(): MutableLiveData<BaseChooseFileBean>? {
        if (currentChooseFileBeanLiveData == null) {
            currentChooseFileBeanLiveData = MutableLiveData()
        }
        return currentChooseFileBeanLiveData
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {

            }
            Lifecycle.Event.ON_PAUSE -> {

            }
        }
    }

    /**
     * 查询 壁纸 列表
     */
    fun findWallpaperList() {
        // 查询 数据库。
        val list = DesktopWallpaperController.findAllWallpaper()
        L.d(TAG, "findWallpaperList: list = $list")
        // 设置 更新
        wallpaperInfoListLiveData?.postValue(list?.toMutableList())
    }

    /**
     * 查询壁纸信息
     */
    fun findWallpaperById(infoId: Long) {
        val info = DesktopWallpaperController.findWallpaperInfoById(infoId)
        L.d(TAG, "findWallpaperById: info = $info")
        currentPaperInfoLiveData?.postValue(info)
    }

    /**
     * 更新当前选择文件
     * @param bean 文件信息
     */
    fun updateCurrentFileBean(bean: BaseChooseFileBean?) {
        L.d(TAG, "updateCurrentFileBean: bean = $bean")
        if (currentChooseFileBeanLiveData == null) {
            currentChooseFileBeanLiveData = MutableLiveData()
        }
        currentChooseFileBeanLiveData?.postValue(bean)
    }

    /**
     * 更新当前 壁纸信息
     * @param info 壁纸信息
     */
    fun updateCurrentWallpaperInfo(info: DesktopWallpaperInfo?) {
        L.d(TAG, "updateCurrentWallpaperInfo: info = $info")
        currentPaperInfoLiveData?.postValue(info)
        // 判断数组中是否存在 新 壁纸信息
        val list = wallpaperInfoListLiveData?.value

        // 如果 信息 不为空， 更新 或 添加 信息
        info?.let { info2 ->
            val oldWallpaperInfo = list?.find {
                it.wallpaperId == info2.wallpaperId
            }
            // 判断壁纸信息 是否存在 ，存在更新，否则添加
            if (oldWallpaperInfo != null) {
                val index = list.indexOf(oldWallpaperInfo)
                list[index] = info2
            } else {
                list?.add(info2)
            }
            wallpaperInfoListLiveData?.postValue(list)

            // 更新数据库
            DesktopWallpaperController.updateWallpaperInfo(info)
        }

    }
}