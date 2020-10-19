package com.inz.z.base.view.dialog

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.inz.z.base.R
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.BasePreviewImageBean
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.ImageUtils
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.base.view.dialog.adapter.PreviewImageListRvAdapter
import com.inz.z.base.view.dialog.adapter.PreviewImageVpRvAdapter
import kotlinx.android.synthetic.main.base_dialog_preview_image.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/14 15:40.
 */
class PreviewImageFragmentDialog private constructor() : AbsBaseDialogFragment() {

    companion object {
        private const val TAG = "PreviewImageFragmentDialog"

        fun getInstant(currentImageSrc: String): PreviewImageFragmentDialog {
            return getInstant(currentImageSrc, null)

        }

        fun getInstant(
            currentImageSrc: String,
            fileList: ArrayList<BaseChooseFileBean>?
        ): PreviewImageFragmentDialog {
            val dialog = PreviewImageFragmentDialog()
            val bundle = Bundle()
            bundle.putString("currentImage", currentImageSrc)
            bundle.putParcelableArrayList("fileImageList", fileList)
            dialog.arguments = bundle
            return dialog
        }
    }

    private var selectedImageSrc: String? = ""
    private var selectedFileList: ArrayList<BaseChooseFileBean>? = null

    private var previewImageListRvAdapter: PreviewImageListRvAdapter? = null
    private var previewImageVpRvAdapter: PreviewImageVpRvAdapter? = null

    override fun initWindow() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_Dialog)
    }

    override fun getLayoutId(): Int {
        return R.layout.base_dialog_preview_image
    }

    override fun initView() {

        previewImageListRvAdapter = PreviewImageListRvAdapter(mContext)
        previewImageListRvAdapter?.listener = PreviewImageListRvAdapterListenerImpl()
        val linearManager = LinearLayoutManager(mContext)
        linearManager.orientation = LinearLayoutManager.HORIZONTAL
        base_dpi_list_rv.apply {
            adapter = previewImageListRvAdapter
            layoutManager = linearManager
        }

        previewImageVpRvAdapter = PreviewImageVpRvAdapter(mContext)
        previewImageVpRvAdapter?.listener = PreviewImageVpRvAdapterListenerImpl()
        base_dpi_preview_vp2.apply {
            this.adapter = previewImageVpRvAdapter
            this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    L.i(TAG, "------------- $position")
                    previewImageListRvAdapter?.targetSelectedPosition(position)
                }
            })
        }

        base_dpi_top_back_iv?.setOnClickListener {
            dismissAllowingStateLoss()
        }


    }

    override fun initData() {
        arguments?.apply {
            selectedImageSrc = this.getString("currentImage", "")
            selectedFileList = this.getParcelableArrayList("fileImageList")
        }
        val haveMoreImg = !selectedFileList.isNullOrEmpty()
        base_dpi_list_rv?.visibility = if (haveMoreImg) View.VISIBLE else View.GONE

        if (haveMoreImg) {
            val bean = selectedFileList?.get(0);
            if (bean != null) {
                val bitmap = BitmapFactory.decodeFile(bean.filePath)
                val newBitmap = ImageUtils.resizeBitmap(bitmap, 0.1F)
                L.i(TAG, "initData")
            }
        }

        base_dpi_top_rl.postDelayed({
            val index = getImageInListPosition(selectedImageSrc ?: "", selectedFileList)
            loadImageToRv(index)
        }, 500)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            val size = DisplayMetrics()
            val lp = this.attributes
            this.windowManager.defaultDisplay.getMetrics(size)
            lp.apply {
                height = size.heightPixels
                width = size.widthPixels
                gravity = Gravity.TOP.or(Gravity.CENTER_HORIZONTAL)
            }
            this.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    /**
     * 加载图片文件
     *
     * @param selectedPosition 选中文件位置
     */
    private fun loadImageToRv(selectedPosition: Int) {
        val imageFileList = ArrayList<BasePreviewImageBean>()
        selectedFileList?.forEachIndexed { index, it ->
            if (it.fileType == Constants.FileType.FILE_TYPE_IMAGE) {
                val bean = BasePreviewImageBean(it)
                bean.isSelectedPreview = index == selectedPosition
                imageFileList.add(bean)
            }
        }
        if (!imageFileList.isNullOrEmpty()) {
            previewImageListRvAdapter?.refreshData(imageFileList)

        } else {
            val bean = BasePreviewImageBean()
            bean.filePath = selectedImageSrc ?: ""
            imageFileList.add(bean)
        }
        previewImageVpRvAdapter?.refreshData(imageFileList)
    }

    /**
     * 获取当前图片在列表中位置
     */
    private fun getImageInListPosition(
        currentImageSrc: String,
        fileList: ArrayList<BaseChooseFileBean>?
    ): Int {
        fileList?.forEachIndexed { index, baseChooseFileBean ->
            if (currentImageSrc.equals(baseChooseFileBean.filePath)) {
                return index
            }
        }
        return -1
    }

    /**
     * 切换底部图片预览显示
     */
    private fun targetBottomRvVisibility() {
        if (!selectedFileList.isNullOrEmpty()) {
            base_dpi_list_rv?.apply {
                // TODO: 2020/10/15 添加动画
                val vs = this.visibility
                this.visibility = if (vs == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }

    /**
     * 切换 界面显示
     */
    private fun targetImageContent() {
        val isShow = base_dpi_top_rl?.visibility == View.VISIBLE
        val vi = if (isShow) View.GONE else View.VISIBLE
        base_dpi_top_rl?.visibility = vi
        base_dpi_bottom_rl?.visibility = vi
        base_dpi_list_rv?.visibility = vi
    }

    /**
     * 预览图片列表 Adapter 监听
     */
    private inner class PreviewImageListRvAdapterListenerImpl :
        PreviewImageListRvAdapter.PreviewImageListRvAdapterListener {
        override fun onImageClick(v: View?, position: Int) {
            L.i(TAG, "---------------  $position")
            base_dpi_preview_vp2?.currentItem = position
        }
    }

    private inner class PreviewImageVpRvAdapterListenerImpl :
        PreviewImageVpRvAdapter.PreviewImageVpRvAdapterListener {
        override fun onImageClick(v: View?, position: Int) {
            targetImageContent()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    class Builder(val mContext: Context) {

        private var currentImageSrc: String? = ""
        private var chooseFileList: ArrayList<BaseChooseFileBean>? = null

        fun setCurrentImageSrc(imageSrc: String): Builder {
            currentImageSrc = imageSrc
            return this
        }

        fun setImageList(chooseFileList: ArrayList<BaseChooseFileBean>?): Builder {
            this.chooseFileList = chooseFileList
            return this
        }

        fun build(): PreviewImageFragmentDialog {
            return getInstant(currentImageSrc ?: "", chooseFileList)
        }

    }

}