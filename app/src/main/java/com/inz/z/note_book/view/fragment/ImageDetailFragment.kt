package com.inz.z.note_book.view.fragment

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.local.LocalImageInfo
import com.inz.z.note_book.databinding.FragmentImageDetailBaseBinding

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

    private var binding: FragmentImageDetailBaseBinding? = null

    private var imageInfo: LocalImageInfo? = null
    private var imagePath: String = ""

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_image_detail_base
    }

    override fun useViewBinding(): Boolean = true

    override fun getViewBindingView(): View? {
        binding = FragmentImageDetailBaseBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {

    }

    override fun initData() {
        arguments?.let {
            imagePath = it.getString("imagePath", "") ?: ""
            val imageId = it.getLong("imageId", 0L)
        }
        binding?.let {
            Glide.with(mContext).load(imagePath).into(it.fmImageDetailBaseIv)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}