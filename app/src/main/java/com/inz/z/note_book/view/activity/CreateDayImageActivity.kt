package com.inz.z.note_book.view.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.util.ThreadPoolUtils
import com.inz.z.base.util.ToastUtil
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityCreateDayImageBinding
import com.inz.z.note_book.service.create_image.CreateDayImageListener
import com.inz.z.note_book.service.create_image.CreateDayImageRunnable
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
    private var qrContentEdit: AtomicBoolean? = null
    private var qrContentExpand: AtomicBoolean? = null
    private var overContentEdit: AtomicBoolean? = null
    private var overContentExpand: AtomicBoolean? = null
    private var leftContentEdit: AtomicBoolean? = null
    private var leftContentExpand: AtomicBoolean? = null

    override fun initView() {
        binding?.let {
            it.btnCreateDayImageChooseImage.setOnClickListener(this)
            it.btnCreateDayImagePreview.setOnClickListener(this)
            it.btnCreateDayImageSetting.setOnClickListener(this)
            it.ivCreateDayImageQrContentShowMore.setOnClickListener(this)
            it.bnlCreateDayImageQrLabel.setOnClickListener(this)
            it.ivCreateDayImageQrContentEdit.setOnClickListener(this)
            it.ivCreateDayImageOverContentShowMore.setOnClickListener(this)
            it.bnlCreateDayImageOverLabel.setOnClickListener(this)
            it.ivCreateDayImageOverContentEdit.setOnClickListener(this)
            it.ivCreateDayImageLeftContentShowMore.setOnClickListener(this)
            it.bnlCreateDayImageLeftLabel.setOnClickListener(this)
            it.ivCreateDayImageLeftContentEdit.setOnClickListener(this)
            it.ivCreateDayImagePreviewClose.setOnClickListener(this)
        }
    }

    override fun initData() {
        previewing = AtomicBoolean(false)
        qrContentEdit = AtomicBoolean(false)
        qrContentExpand = AtomicBoolean(true)
        overContentEdit = AtomicBoolean(false)
        overContentExpand = AtomicBoolean(true)
        leftContentEdit = AtomicBoolean(false)
        leftContentExpand = AtomicBoolean(true)
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

        val oldImagePath = NoteSPHelper.getCreateDayImagePath()
        if (!TextUtils.isEmpty(oldImagePath)) {
            chooseImagePath = oldImagePath
            loadImageFromPath(oldImagePath)
        }

        // 设置Qr 内容
        val oldQrContent = NoteSPHelper.getCreateDayQRContent()
        binding?.etCreateDayImageQrContent?.setText(oldQrContent)
        targetQrContentView(qrContentExpand?.get() ?: true)
        targetQrContentEditStatus(qrContentEdit?.get() ?: false)


    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            L.i(TAG, "onClick: click fast .")
            return
        }
        val vId = v?.id
        binding?.let {
            when (vId) {
                // 选择图片
                it.btnCreateDayImageChooseImage.id -> {
                    // Choose Image.
                    chooseImageLauncher?.launch("image/*")
                }
                // 设置
                it.btnCreateDayImageSetting.id -> {
                    // Setting.
                    NoteSPHelper.saveCreateDayImagePath(chooseImagePath)
                    val qrContent = it.etCreateDayImageQrContent.text.toString()
//                    NoteSPHelper.saveCreateDayQRContent("只要你愿意，当你失落失意的时候，最需要一个肩膀的时候，告诉我，我会立即出现。")
                    NoteSPHelper.saveCreateDayQRContent(qrContent)

                    ToastUtil.showToast(
                        mContext,
                        getString(R.string.day_panel_setting_save_success)
                    )
                }
                // 预览
                it.ivCreateDayImagePreviewClose.id,
                it.btnCreateDayImagePreview.id -> {
                    val isPreview = previewing?.get() ?: false
                    if (isPreview) {
                        it.rlCreateDayImagePreviewContent.visibility = View.GONE
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
                // 切换 Qr 显示 ， 展开/收起
                it.bnlCreateDayImageQrLabel.id,
                it.ivCreateDayImageQrContentShowMore.id -> {
                    val expand = qrContentExpand?.get() ?: true
                    qrContentExpand?.set(!expand)
                    targetQrContentView(!expand)
                }
                // 编辑 QR
                it.ivCreateDayImageQrContentEdit.id -> {
                    val edit = qrContentEdit?.get() ?: false
                    qrContentEdit?.set(!edit)
                    targetQrContentEditStatus(!edit)
                }

                // 切换 Over 显示 ， 展开/收起
                it.bnlCreateDayImageOverLabel.id,
                it.ivCreateDayImageOverContentShowMore.id -> {
                    val expand = overContentExpand?.get() ?: true
                    overContentExpand?.set(!expand)
                    targetOverContentView(!expand)
                }
                // 编辑 Over
                it.ivCreateDayImageOverContentEdit.id -> {
                    val edit = overContentEdit?.get() ?: false
                    overContentEdit?.set(!edit)
                    targetOverContentEditStatus(!edit)
                }

                // 切换 Left 显示 ， 展开/收起
                it.bnlCreateDayImageLeftLabel.id,
                it.ivCreateDayImageLeftContentShowMore.id -> {
                    val expand = leftContentExpand?.get() ?: true
                    leftContentExpand?.set(!expand)
                    targetLeftContentView(!expand)
                }
                // 编辑 Left
                it.ivCreateDayImageLeftContentEdit.id -> {
                    val edit = leftContentEdit?.get() ?: false
                    leftContentEdit?.set(!edit)
                    targetLeftContentEditStatus(!edit)
                }
                else -> {

                }
            }
        }
    }

    /**
     * 加载图片
     */
    private fun loadImageFromPath(path: String) {
        binding?.let {
            Glide.with(mContext).load(path).into(it.ivCreateDayImageViewer)
        }
    }

    /**
     * 预览
     */
    private fun previewCreateDayImage(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        ThreadPoolUtils.getWorkThread("${TAG}_preview_create_day_image")
            .execute(
                CreateDayImageRunnable(
                    mContext.applicationContext, bitmap, 1, "张雪，我喜欢你。",
                    object : CreateDayImageListener {
                        override fun onSavedBitmap(bitmap: Bitmap) {
                            val nBitmap = Bitmap.createBitmap(bitmap)
                            ThreadPoolUtils.getUiThread("${TAG}_update_preview").execute {

                                binding?.let {
//                                    it.ivCreateDayImagePreview.setImageBitmap(bitmap)
                                    Glide.with(mContext).load(nBitmap)
                                        .into(it.ivCreateDayImagePreview)
                                    previewing?.set(true)
                                    it.rlCreateDayImagePreviewContent.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                )
            )
    }

    /**
     * 切换QrView
     */
    private fun targetQrContentView(expand: Boolean) {
        binding?.let {
            it.ivCreateDayImageQrContentShowMore.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    if (expand)
                        R.drawable.ic_expand_less_black_24dp
                    else
                        R.drawable.ic_expand_more_black_24dp
                )
            )
            it.etCreateDayImageQrContent.visibility =
                if (expand) View.VISIBLE else View.GONE
            it.ivCreateDayImageQrContentEdit.visibility =
                if (expand) View.VISIBLE else View.GONE
            it.bnlCreateDayImageQrLabel.background =
                ContextCompat.getDrawable(
                    mContext,
                    if (expand)
                        R.drawable.bg_content_label_radius_base_top_label_less
                    else
                        R.drawable.bg_content_label_radius_base_top_label_more
                )
        }
    }

    /**
     * 切换 Qr View 内容状态
     */
    private fun targetQrContentEditStatus(edit: Boolean) {
        binding?.let {
            it.etCreateDayImageQrContent.apply {
                this.isEnabled = edit
                this.isFocusableInTouchMode = edit
                this.isFocusable = edit
                if (edit) {
                    this.requestFocus()
                }
            }
        }
    }


    /**
     * 切换 图片上 View
     */
    private fun targetOverContentView(expand: Boolean) {
        binding?.let {
            it.ivCreateDayImageOverContentShowMore.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    if (expand)
                        R.drawable.ic_expand_less_black_24dp
                    else
                        R.drawable.ic_expand_more_black_24dp
                )
            )
            it.etCreateDayImageOverContent.visibility =
                if (expand) View.VISIBLE else View.GONE
            it.ivCreateDayImageOverContentEdit.visibility =
                if (expand) View.VISIBLE else View.GONE
            it.bnlCreateDayImageOverLabel.background =
                ContextCompat.getDrawable(
                    mContext,
                    if (expand)
                        R.drawable.bg_content_label_radius_base_top_label_less
                    else
                        R.drawable.bg_content_label_radius_base_top_label_more
                )
        }
    }

    /**
     * 切换 图片上 View 内容状态
     */
    private fun targetOverContentEditStatus(edit: Boolean) {
        binding?.let {
            it.etCreateDayImageOverContent.apply {
                this.isEnabled = edit
                this.isFocusableInTouchMode = edit
                this.isFocusable = edit
                if (edit) {
                    this.requestFocus()
                }
            }
        }
    }


    /**
     * 切换Left Text View
     */
    private fun targetLeftContentView(expand: Boolean) {
        binding?.let {
            it.ivCreateDayImageLeftContentShowMore.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    if (expand)
                        R.drawable.ic_expand_less_black_24dp
                    else
                        R.drawable.ic_expand_more_black_24dp
                )
            )
            it.etCreateDayImageLeftContent.visibility =
                if (expand) View.VISIBLE else View.GONE
            it.ivCreateDayImageLeftContentEdit.visibility =
                if (expand) View.VISIBLE else View.GONE
            it.bnlCreateDayImageLeftLabel.background =
                ContextCompat.getDrawable(
                    mContext,
                    if (expand)
                        R.drawable.bg_content_label_radius_base_top_label_less
                    else
                        R.drawable.bg_content_label_radius_base_top_label_more
                )
        }
    }

    /**
     * 切换 Left 内容状态
     */
    private fun targetLeftContentEditStatus(edit: Boolean) {
        binding?.let {
            it.etCreateDayImageLeftContent.apply {
                this.isEnabled = edit
                this.isFocusableInTouchMode = edit
                this.isFocusable = edit
                if (edit) {
                    this.requestFocus()
                }
            }
        }
    }

}