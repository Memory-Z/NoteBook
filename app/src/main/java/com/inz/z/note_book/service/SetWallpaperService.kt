package com.inz.z.note_book.service

import android.app.Service
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.IBinder
import android.text.TextUtils
import com.inz.z.base.util.L
import com.inz.z.note_book.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 设置 壁纸 服务
 *
 * ====================================================
 * Create by 11654 in 2021/11/4 21:37
 */
class SetWallpaperService : Service() {


    companion object {
        private const val TAG = "SetWallpaperService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        L.d(TAG, "onCreate: >> ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L.i(TAG, "onStartCommand: ----> START >>> ")
        intent?.also {
            initData(it)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i(TAG, "onDestroy: ----> END  >>> ")
    }

    /**
     * 初始化 数据
     * @param intent 传入 数据
     */
    private fun initData(intent: Intent) {
        // 获取 显示区域
        val rectArray =
            intent.getIntArrayExtra(Constants.WallpaperParams.PARAMS_TAG_WALLPAPER_RECT_ARRAY)
        val rect = Rect()
        rectArray?.let {
            rect.set(it[0], it[1], it[2], it[3])
        }
        // 获取 文件 地址
        val filePath =
            intent.getStringExtra(Constants.WallpaperParams.PARAMS_TAG_WALLPAPER_FILE_PATH)
        L.i(TAG, "initData: -->> rect = $rect ; filePath = $filePath .")

        // 是否需要 关闭
        var needClose = !TextUtils.isEmpty(filePath)

        filePath?.let {
            CoroutineScope(Dispatchers.Main).launch {
                // 获取 壁纸 bitmap
                val bitmap = getWallpaperBitmap(it)
                if (bitmap != null) {
                    setWallpaper(rect, bitmap)
                } else {
                    needClose = true
                }
            }
        }
        if (needClose) {
            L.w(TAG, "initData: this set wallpaper error, have null data. ")
            stopSelf()
        }
    }

    /**
     * 根据 文件 地址 获取 相应 bitmap
     * @param filePath 文件 地址
     */
    private suspend fun getWallpaperBitmap(filePath: String): Bitmap? {
        var bitmap: Bitmap? = null
        withContext(Dispatchers.IO) {
            try {
                bitmap = BitmapFactory.decodeFile(filePath)
            } catch (ignore: IllegalArgumentException) {
            }
        }
        return bitmap
    }

    /**
     * 设置 壁纸 。
     * @param rect 壁纸显示 区域
     * @param bitmap 壁纸 图像
     */
    private fun setWallpaper(rect: Rect?, bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        wallpaperManager?.setBitmap(
            bitmap,
            rect,
            true,
            WallpaperManager.FLAG_SYSTEM.or(WallpaperManager.FLAG_LOCK)
        )
        // 结束 自己
        stopSelf()
    }

}