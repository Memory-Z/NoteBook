package com.inz.z.note_book.work

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.inz.z.base.util.ImageUtils
import com.inz.z.base.util.L
import com.inz.z.note_book.base.FragmentContentTypeValue

/**
 * 设置桌面壁纸 - worker
 *
 * ====================================================
 * Create by 11654 in 2021/12/7 22:21
 */
open class SetDesktopWallpaperWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    companion object {
        private const val TAG = "SetDesktopWallpaperWork"

        /**
         * 图片地址
         */
        const val IMAGE_PATH_TAG = "imagePath"

        /**
         * 图片显示区域
         */
        const val IMAGE_RECT_TAG = "imageRect"

        /**
         * 图像旋转角度
         */
        const val IMAGE_ROTATE_TAG = "imageRotate"

        /**
         * 设置图片 类型 。 桌面/锁屏
         */
        const val IMAGE_SET_TYPE_TAG = "imageType"
    }

    private var mContext: Context? = context
    private var mWorkerParams: WorkerParameters? = workerParams

    override fun doWork(): Result {
        return if (setWallpaper()) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    /**
     * 设置壁纸
     */
    @SuppressLint("MissingPermission")
    private fun setWallpaper(): Boolean {
        L.i(TAG, "setWallpaper: --->> Start . ")
        var imagePath = ""
        var imageRect: Rect? = null
        var imageRotate = 0
        var wallpaperType = FragmentContentTypeValue.WALLPAPER_FLAG_SYSTEM
        mWorkerParams?.inputData?.let {
            imagePath = it.getString(IMAGE_PATH_TAG) ?: ""
            val imageRectArray = it.getIntArray(IMAGE_RECT_TAG)
            imageRectArray?.let { array ->
                imageRect = Rect(array[0], array[1], array[2], array[3])
            }
            imageRotate = it.getInt(IMAGE_ROTATE_TAG, 0)
            try {
                wallpaperType =
                    it.getInt(IMAGE_SET_TYPE_TAG, FragmentContentTypeValue.WALLPAPER_FLAG_SYSTEM)
            } catch (e: Exception) {
                wallpaperType = FragmentContentTypeValue.WALLPAPER_FLAG_SYSTEM
            }
        }
        // 如果 图片 地址 为空 不处理
        if (TextUtils.isEmpty(imagePath)) {
            L.w(TAG, "setWallpaper: imagePath is null .")
            return false
        }
        val manager = mContext?.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager?
        manager?.let {
            val tempBitmap = BitmapFactory.decodeFile(imagePath)
            // 旋转
            val bitmap = ImageUtils.rotateBitmap(tempBitmap, imageRotate)
            it.setBitmap(bitmap, imageRect, true, wallpaperType)
        }
        return true
    }


}