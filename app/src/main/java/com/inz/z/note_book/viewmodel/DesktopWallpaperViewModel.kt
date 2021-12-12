package com.inz.z.note_book.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.base.entity.BaseChooseFileBean
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

    /**
     * 全部壁纸信息
     */
    private var wallpaperInfoListLiveData: MutableLiveData<List<DesktopWallpaperInfo>>? = null

    /**
     * 当前选中 壁纸信息
     */
    private var currentPaperInfoLiveData: MutableLiveData<DesktopWallpaperInfo>? = null

    /**
     * 当前 选中的 图片文件信息
     */
    var currentChooseFileBeanLiveData: MutableLiveData<BaseChooseFileBean>? = null

    /**
     * 获取 壁纸信息列表
     */
    fun getWallpaperInfoList(): MutableLiveData<List<DesktopWallpaperInfo>>? {
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
        // 设置 更新
        wallpaperInfoListLiveData?.postValue(list)
    }

    /**
     * 查询壁纸信息
     */
    fun findWallpaperById(infoId: Long) {
        val info = DesktopWallpaperController.findWallpaperInfoById(infoId)
        currentPaperInfoLiveData?.postValue(info)
    }

}