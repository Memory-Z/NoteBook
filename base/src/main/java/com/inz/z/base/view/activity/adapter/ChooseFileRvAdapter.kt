package com.inz.z.base.view.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.R
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.databinding.BaseItemChooseFileListBinding
import com.inz.z.base.databinding.BaseItemChooseFileTableBinding
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.view.activity.ChooseFileActivity

/**
 * 选择图片适配器器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 11:53.
 */
class ChooseFileRvAdapter(val mContext: Context) :
    AbsBaseRvAdapter<BaseChooseFileBean, ChooseFileRvAdapter.ChooseFileRvHolder>(mContext) {
    companion object {

        @ChooseFileActivity.ShowMode
        var showMode = ChooseFileActivity.MODE_LIST

    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ChooseFileRvHolder {
        val view: View
        when (showMode) {
            ChooseFileActivity.MODE_TABLE -> {
                view = mLayoutInflater.inflate(R.layout.base_item_choose_file_table, parent, false)
                return ChooseFileTableRvHolder(view)
            }
            else -> {
                view = mLayoutInflater.inflate(R.layout.base_item_choose_file_list, parent, false)
                return ChooseFileListRvHolder(view)
            }
        }
    }

    override fun onBindVH(holder: ChooseFileRvHolder, position: Int) {
        val bean = list[position]
        if (holder is ChooseFileListRvHolder) {
            holder.baseItemChooseFileListBinding?.chooseFile = bean
        } else if (holder is ChooseFileTableRvHolder) {
            holder.baseItemChooseFileTableBinding?.chooseFile = bean
        }
    }

    open class ChooseFileRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class ChooseFileListRvHolder(itemView: View) : ChooseFileRvHolder(itemView) {
        val baseItemChooseFileListBinding: BaseItemChooseFileListBinding? =
            DataBindingUtil.bind(itemView)

    }

    class ChooseFileTableRvHolder(itemView: View) : ChooseFileRvHolder(itemView) {
        val baseItemChooseFileTableBinding: BaseItemChooseFileTableBinding? =
            DataBindingUtil.bind(itemView)

    }
}