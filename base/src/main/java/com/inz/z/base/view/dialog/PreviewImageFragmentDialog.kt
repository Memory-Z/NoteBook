package com.inz.z.base.view.dialog

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseDialogFragment
import com.inz.z.base.view.dialog.adapter.PreviewImageListRvAdapter
import com.inz.z.base.view.dialog.adapter.PreviewImageVpRvAdapter
import kotlinx.android.synthetic.main.base_dialog_preview_image.*
import kotlinx.android.synthetic.main.base_dialog_preview_image.view.*

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
            return getInstant(currentImageSrc, null, null)
        }

        fun getInstant(
            currentImageSrc: String,
            fileList: ArrayList<BaseChooseFileBean>?,
            listener: PreviewImageFragmentDialogListener?
        ): PreviewImageFragmentDialog {
            return getInstant(currentImageSrc, null, fileList, listener)
        }

        fun getInstant(
            currentImageBean: BaseChooseFileBean,
            fileList: ArrayList<BaseChooseFileBean>?,
            listener: PreviewImageFragmentDialogListener?
        ): PreviewImageFragmentDialog {
            return getInstant("", currentImageBean, fileList, listener)
        }

        fun getInstant(
            currentImageSrc: String,
            currentImageBean: BaseChooseFileBean?,
            fileList: ArrayList<BaseChooseFileBean>?,
            listener: PreviewImageFragmentDialogListener?
        ): PreviewImageFragmentDialog {
            val dialog = PreviewImageFragmentDialog()
            val bundle = Bundle()
            bundle.putString("currentImage", currentImageSrc)
            bundle.putParcelable("currentImageBean", currentImageBean)
            bundle.putParcelableArrayList("fileImageList", fileList)
            dialog.arguments = bundle
            dialog.listener = listener
            return dialog
        }
    }

    private var selectedImageSrc: String? = ""
    private var selectedImageBean: BaseChooseFileBean? = null
    private var selectedFileList: List<BaseChooseFileBean>? = null

    private var previewImageList: List<BasePreviewImageBean>? = null

    private var previewImageListRvAdapter: PreviewImageListRvAdapter? = null
    private var previewImageVpRvAdapter: PreviewImageVpRvAdapter? = null

    var listener: PreviewImageFragmentDialogListener? = null

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
            setItemViewCacheSize(5)
            adapter = previewImageListRvAdapter
            layoutManager = linearManager
        }

        previewImageVpRvAdapter = PreviewImageVpRvAdapter(mContext)
        previewImageVpRvAdapter?.listener = PreviewImageVpRvAdapterListenerImpl()
        base_dpi_preview_vp2.apply {
            this.adapter = previewImageVpRvAdapter
            offscreenPageLimit = 5
            this.registerOnPageChangeCallback(ViewPager2ChangeListenerExt())
        }

        base_dpi_top_back_iv?.setOnClickListener {
            dismissAllowingStateLoss()
        }

        base_dpi_bottom_cbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            val position = base_dpi_preview_vp2?.currentItem
            if (position != null) {
                val bean = previewImageVpRvAdapter?.getItemByPosition(position)
                if (bean != null && bean.checked != isChecked) {
                    bean.checked = isChecked
                    previewImageVpRvAdapter?.refreshItemOnPosition(position, bean)
                }

            }
        }

        base_dpi_top_done_tv?.setOnClickListener {
            val previewList = previewImageVpRvAdapter?.list?.toList()
            val fileList = resetChooseFileListWithPreview(previewList)
            listener?.onSubmit(fileList)
            dismissAllowingStateLoss()
        }

    }

    override fun initData() {
        arguments?.apply {
            selectedImageSrc = this.getString("currentImage", "")
            selectedImageBean = this.getParcelable("currentImageBean")
            selectedFileList = this.getParcelableArrayList("fileImageList")
        }
        val haveMoreImg = !selectedFileList.isNullOrEmpty()
        base_dpi_list_rv?.visibility = if (haveMoreImg) View.VISIBLE else View.GONE

        var fileName = ""
        if (selectedImageBean != null) {
            selectedImageSrc = selectedImageBean!!.filePath
            fileName = selectedImageBean!!.fileName
        } else {

            val src = selectedImageSrc?.replace("\\", "/") ?: ""
            val index = src.lastIndexOf("/")
            if (index != -1) {
                fileName = src.substring(index)
            }
            selectedImageBean = BaseChooseFileBean()
                .apply {
                    this.checked = true
                    this.filePath = selectedImageSrc ?: ""
                    this.canChoose = true
                    this.fileName = fileName
                    this.fileIsDirectory = false
                    this.fileType = Constants.FileType.FILE_TYPE_IMAGE
                }
        }
        L.i(TAG, "initData ----------------- $fileName ")
        base_dpi_top_title_tv?.text = fileName

//
//        if (haveMoreImg) {
//            val bean = selectedFileList?.get(0);
//            if (bean != null) {
//                val bitmap = BitmapFactory.decodeFile(bean.filePath)
//                val newBitmap = ImageUtils.resizeBitmap(bitmap, 0.1F)
//                L.i(TAG, "initData")
//            }
//        }

        base_dpi_top_rl.postDelayed({
            val index = getImageInListPosition(selectedImageSrc ?: "", selectedFileList)
            loadImageToRv(index)
        }, 1000)
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

    override fun onResume() {
        super.onResume()
        listener?.onDialogShow()
    }

    override fun onPause() {
        super.onPause()
        listener?.onDialogHide()
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
            previewImageListRvAdapter?.refreshData(this.previewImageList?.toMutableList())
        } else {
            if (selectedImageBean != null) {
                val bean = BasePreviewImageBean(selectedImageBean!!)
                bean.isSelectedPreview = true
                imageFileList.add(bean)
            } else {
                val bean = BasePreviewImageBean()
                bean.filePath = selectedImageSrc ?: ""
                bean.checked = true
                bean.isSelectedPreview = true
                imageFileList.add(bean)
            }
        }
        this.previewImageList = imageFileList

        previewImageVpRvAdapter?.refreshData(this.previewImageList)
    }

    /**
     * 获取当前图片在列表中位置
     */
    private fun getImageInListPosition(
        currentImageSrc: String,
        fileList: List<BaseChooseFileBean>?
    ): Int {
        fileList?.forEachIndexed { index, baseChooseFileBean ->
            if (currentImageSrc.equals(baseChooseFileBean.filePath)) {
                return index
            }
        }
        return -1
    }

    /**
     * ViewPager2 切换监听实现
     */
    private inner class ViewPager2ChangeListenerExt : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            previewImageListRvAdapter?.targetSelectedPosition(position)
            val bean = previewImageVpRvAdapter?.getItemByPosition(position)
            if (bean != null) {
                mView?.base_dpi_bottom_cbox?.isChecked = bean.checked
                mView?.base_dpi_top_title_tv?.text = bean.fileName
            }
        }
    }

    /**
     * 切换底部图片预览显示
     */
    private fun targetBottomRvVisibility() {
        if (!selectedFileList.isNullOrEmpty()) {
            base_dpi_list_rv?.apply {
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
        targetBottomRvVisibility()
    }

    /**
     * 预览图片列表 Adapter 监听
     */
    private inner class PreviewImageListRvAdapterListenerImpl :
        PreviewImageListRvAdapter.PreviewImageListRvAdapterListener {
        override fun onImageClick(v: View?, position: Int) {
            base_dpi_preview_vp2?.currentItem = position
        }
    }

    private inner class PreviewImageVpRvAdapterListenerImpl :
        PreviewImageVpRvAdapter.PreviewImageVpRvAdapterListener {
        override fun onImageClick(v: View?, position: Int) {
            targetImageContent()
        }
    }

    /**
     * 重置选中文件列表 通过 预览文件
     */
    private fun resetChooseFileListWithPreview(previewImageList: List<BasePreviewImageBean>?): List<BaseChooseFileBean>? {
        selectedFileList?.forEach { file ->
            run previewForEach@{
                previewImageList?.forEach { preview ->
                    if (file.filePath.equals(preview.filePath)) {
                        file.checked = preview.checked
                        return@previewForEach
                    }
                }
            }
        }
        return selectedFileList
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    class Builder(val mContext: Context) {

        private var currentImageSrc: String? = ""
        private var currentImageBean: BaseChooseFileBean? = null
        private var chooseFileList: ArrayList<BaseChooseFileBean>? = null
        private var listener: PreviewImageFragmentDialogListener? = null

        fun setCurrentImageSrc(imageSrc: String): Builder {
            this.currentImageSrc = imageSrc
            return this
        }

        fun setCurrentImageBean(currentImageBean: BaseChooseFileBean?): Builder {
            this.currentImageBean = currentImageBean
            return this
        }

        fun setImageList(chooseFileList: ArrayList<BaseChooseFileBean>?): Builder {
            this.chooseFileList = chooseFileList
            return this
        }

        fun setListener(listener: PreviewImageFragmentDialogListener?): Builder {
            this.listener = listener
            return this
        }

        fun build(): PreviewImageFragmentDialog {
            return getInstant(currentImageSrc ?: "", currentImageBean, chooseFileList, listener)
        }

    }

    interface PreviewImageFragmentDialogListener {
        /**
         * 切换选中
         */
        fun onTargetCheck(v: View?, position: Int)

        /**
         * 弹窗显示
         */
        fun onDialogShow()

        /**
         * 弹窗消失
         */
        fun onDialogHide()

        /**
         * 提交
         * @param previewImageList 选中列表
         */
        fun onSubmit(previewImageList: List<BaseChooseFileBean>?)

    }

}