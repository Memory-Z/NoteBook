package com.inz.z.note_book.view.fragment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.local.LocalImageInfo

/**
 * 系统文件 适配器
 *
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 16:27.
 */
class SysFileImageRvAdapter :
    AbsBaseRvAdapter<LocalImageInfo, SysFileImageRvAdapter.SysFileImageBaseRvViewHolder> {
    companion object {
        private const val VIEW_TYPE_ITEM = 0x01A0
        private const val VIEW_TYPE_MORE = 0x01A1
    }


    constructor(mContext: Context?) : super(mContext)

    override fun onCreateVH(parent: ViewGroup, viewType: Int): SysFileImageBaseRvViewHolder {
//        when(viewType) {
//            VIEW_TYPE_ITEM -> {
        val view = mLayoutInflater.inflate(R.layout.item_image_base, parent, false)
        return SysFileImageItmeRvViewHolder(view)
//            }
//        }
    }

    override fun onBindVH(holder: SysFileImageBaseRvViewHolder, position: Int) {
        when (holder) {
            is SysFileImageItmeRvViewHolder -> {

            }
            else -> {

            }
        }
    }

    open class SysFileImageBaseRvViewHolder(itemView: View) : AbsBaseRvViewHolder(itemView) {}

    class SysFileImageItmeRvViewHolder(itemView: View) : SysFileImageBaseRvViewHolder(itemView) {

    }
}