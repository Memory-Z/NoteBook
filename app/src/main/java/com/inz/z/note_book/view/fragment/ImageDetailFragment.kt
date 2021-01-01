package com.inz.z.note_book.view.fragment

import android.os.Bundle
import com.bumptech.glide.Glide
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.local.LocalImageInfo
import kotlinx.android.synthetic.main.fragment_image_detail_base.*
import java.io.Serializable

/**
 *
 * 默认图片详情 界面
 * ====================================================
 * Create by 11654 in 2021/1/1 4:06
 */
class ImageDetailFragment : AbsBaseFragment() {

    companion object {
        fun getInstance(imagePath: String, imageId: Long): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            val bundle = fragment.arguments ?: Bundle()
            bundle.putString("imagePath", imagePath)
            bundle.putLong("imageId", imageId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var imageInfo: LocalImageInfo? = null
    private var imagePath: String = ""

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_image_detail_base
    }

    override fun initView() {

    }

    override fun initData() {
        arguments?.let {
            imagePath = it.getString("imagePath", "") ?: ""
            val imageId = it.getLong("imageId", 0L)
        }
        Glide.with(mContext).load(imagePath).into(fm_image_detail_base_iv)
    }


}