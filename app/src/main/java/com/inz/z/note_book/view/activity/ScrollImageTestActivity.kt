package com.inz.z.note_book.view.activity

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.widget.Button
import androidx.core.graphics.toRect
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.widget.layout.ScrollImageView

/**
 *
 * 滑动 Image  测试 界面
 * ====================================================
 * Create by 11654 in 2021/11/1 20:53
 */
class ScrollImageTestActivity : BaseNoteActivity() {
    companion object {
        private const val TAG = "ScrollImageTestActivity"
    }

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.test_activity_scroll_image
    }

    @SuppressLint("MissingPermission")
    override fun initView() {
        val btn: Button = findViewById(R.id.test_scroll_get_rect_btn)
        val scrollIv: ScrollImageView = findViewById(R.id.test_scroll_iv)
        btn.setOnClickListener {
            val rect = scrollIv.getWallpaperRect()
//            val bitmap = scrollIv.getWallpaperBitmap()
            val bitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.img_phone_2)
            L.i(TAG, "initView: rect = $rect ; bitmap = {${bitmap.width}, ${bitmap.height}}")
            val wallpaperManager = WallpaperManager.getInstance(mContext)
//            wallpaperManager.setBitmap(bitmap, rect.toRect(), true)
            wallpaperManager.setBitmap(bitmap, rect.toRect(), true, WallpaperManager.FLAG_SYSTEM.or(WallpaperManager.FLAG_LOCK))
        }
    }

    override fun initData() {
    }

}