package com.inz.z.note_book.view.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.util.ThreadPoolUtils
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityCreateDayImageBinding
import com.inz.z.note_book.service.create_image.CreateDayImageListener
import com.inz.z.note_book.service.create_image.CreateDayImageRunnable
import com.inz.z.note_book.util.BaseUtil
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.FileUtil
import com.inz.z.note_book.util.NoteSPHelper
import com.inz.z.note_book.view.BaseNoteActivity
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2022/12/31 19:01
 */
class CreateDayImageActivity : BaseNoteActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "CreateDayImageActivity"
    }

    override fun initWindow() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_create_day_image
    }

    private var binding: ActivityCreateDayImageBinding? = null

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityCreateDayImageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }
    }

    override fun onDestroyTask() {
//        binding?.let {
//            Glide.with(mContext).clear(it.ivCreateDayImageViewer)
//            Glide.with(mContext).clear(it.ivCreateDayImagePreview)
//        }
        super.onDestroyTask()
        binding = null
        chooseImageLauncher = null
    }


    private var chooseImageLauncher: ActivityResultLauncher<String>? = null

    /**
     * 选择图片地址
     */
    private var chooseImagePath: String = ""
    private var previewing: AtomicBoolean? = null

    override fun initView() {
        binding?.let {
            it.btnCreateDayImageChooseImage.setOnClickListener(this)
            it.btnCreateDayImagePreview.setOnClickListener(this)
            it.btnCreateDayImageSetting.setOnClickListener(this)
        }
    }

    override fun initData() {
        previewing = AtomicBoolean(false)
        chooseImageLauncher = FileUtil.getContentWithResult(
            this
        ) {
            // TODO: 2022/12/31 Get Image Uri.
            it?.let {
                ProviderUtil.queryFilePathByUri(mContext, it)
                    .let { path ->
                        path?.apply {
                            chooseImagePath = this
                            loadImageFromPath(this)
                        }
                    }
            }
        }
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.i(TAG, "onClick: click fast .")
            return
        }
        val vId = v?.id
        binding?.let {
            when (vId) {
                it.btnCreateDayImageChooseImage.id -> {
                    // Choose Image.
                    chooseImageLauncher?.launch("image/*")
                }
                it.btnCreateDayImageSetting.id -> {
                    // Setting.
                    NoteSPHelper.saveCreateDayImagePath(chooseImagePath)
                    NoteSPHelper.saveCreateDayQRContent("亲爱的，新年快乐。♥💕💕💕")
                }
                it.btnCreateDayImagePreview.id -> {
                    val isPreview = previewing?.get() ?: false
                    if (isPreview) {
                        it.ivCreateDayImagePreview.visibility = View.GONE
                        previewing?.set(false)
                    } else {
                        // Preview
                        if (!TextUtils.isEmpty(chooseImagePath)) {
                            previewCreateDayImage(chooseImagePath)
                        } else {
                            // Nothing
                        }
                    }

                }
                else -> {

                }
            }
        }
    }

    private fun loadImageFromPath(path: String) {
        binding?.let {
            Glide.with(mContext).load(path).into(it.ivCreateDayImageViewer)
        }
    }

    private fun previewCreateDayImage(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        ThreadPoolUtils.getWorkThread("${TAG}_preview_create_day_image")
            .execute(
                CreateDayImageRunnable(
                    mContext.applicationContext, bitmap, 1, "张雪，我喜欢你。",
                    object : CreateDayImageListener {
                        override fun onSavedBitmap(bitmap: Bitmap) {
                            val nBitmap = Bitmap.createBitmap(bitmap)
                            binding?.let {
                                it.ivCreateDayImagePreview.post {
//                                    it.ivCreateDayImagePreview.setImageBitmap(bitmap)
                                    Glide.with(mContext).load(nBitmap)
                                        .into(it.ivCreateDayImagePreview)
                                    previewing?.set(true)
                                    it.ivCreateDayImagePreview.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                )
            )
    }
}