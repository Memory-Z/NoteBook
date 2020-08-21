package com.inz.z.base.view.activity.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.request.RequestOptions
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
class ChooseFileRvAdapter :
    AbsBaseRvAdapter<BaseChooseFileBean, ChooseFileRvAdapter.ChooseFileRvHolder> {

    @ChooseFileActivity.ShowMode
    var showMode = ChooseFileActivity.MODE_LIST

    constructor(mContext: Context?) : super(mContext)

    constructor(mContext: Context?, showMode: Int) : super(mContext) {
        this.showMode = showMode
    }

    private var requestOption = RequestOptions()
        .error(R.drawable.image_load_error)
        .timeout(1000)

    private val colorPrimaryCsl =
        ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorPrimary))
    private val colorWhiteCsl =
        ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.text_white_base_color))

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ChooseFileRvHolder {
        when (showMode) {
            ChooseFileActivity.MODE_TABLE -> {
                val view =
                    mLayoutInflater.inflate(R.layout.base_item_choose_file_table, parent, false)
                return ChooseFileTableRvHolder(view)
            }
            else -> {
                val view =
                    mLayoutInflater.inflate(R.layout.base_item_choose_file_list, parent, false)
                return ChooseFileListRvHolder(view)
            }
        }
    }

    override fun onBindVH(holder: ChooseFileRvHolder, position: Int) {
        val bean = list[position]
        var checkBox: CheckBox? = null
        if (holder is ChooseFileListRvHolder) {
            holder.baseItemChooseFileListBinding?.chooseFile = bean
            val iv = holder.baseItemChooseFileListBinding?.baseItemCfListIv
            checkBox = holder.baseItemChooseFileListBinding?.baseItemCfListCbox
            if (bean.fileIsDirectory) {
                iv?.apply {
                    Glide.with(mContext).load(R.drawable.ic_baseline_folder_24).apply(requestOption)
                        .into(this)
                    ImageViewCompat.setImageTintList(this, colorPrimaryCsl)
                }
            } else {
                iv?.apply {
                    when (bean.fileType) {
                        BaseChooseFileBean.FILE_TYPE_IMAGE -> {
                            Glide.with(mContext).load(bean.filePath).apply(requestOption)
                                .into(iv)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        else -> {
                            Glide.with(mContext).load(R.drawable.ic_baseline_insert_drive_file_24)
                                .apply(requestOption)
                                .into(iv)
                            ImageViewCompat.setImageTintList(this, colorWhiteCsl)
                        }
                    }
                }
            }
        } else if (holder is ChooseFileTableRvHolder) {
            holder.baseItemChooseFileTableBinding?.chooseFile = bean
            val iv = holder.baseItemChooseFileTableBinding?.baseItemCfTableIv
            checkBox = holder.baseItemChooseFileTableBinding?.baseItemCfTableCbox
            if (bean.fileIsDirectory) {
                iv?.apply {
                    Glide.with(mContext).load(R.drawable.ic_baseline_folder_24).apply(requestOption)
                        .into(this)
                    ImageViewCompat.setImageTintList(this, colorPrimaryCsl)
                }
            } else {
                iv?.apply {
                    when (bean.fileType) {
                        BaseChooseFileBean.FILE_TYPE_IMAGE -> {
                            Glide.with(mContext).load(bean.filePath).apply(requestOption)
                                .into(iv)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        else -> {
                            Glide.with(mContext).load(R.drawable.ic_baseline_insert_drive_file_24)
                                .apply(requestOption)
                                .into(iv)
                            ImageViewCompat.setImageTintList(this, colorWhiteCsl)
                        }
                    }
                }
            }
        }
        checkBox?.visibility = if (bean.fileIsDirectory) View.GONE else View.VISIBLE
        checkBox?.isChecked = bean.checked
    }

    open class ChooseFileRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    inner class ChooseFileListRvHolder(itemView: View) : ChooseFileRvHolder(itemView),
        View.OnClickListener {
        val baseItemChooseFileListBinding: BaseItemChooseFileListBinding? =
            DataBindingUtil.bind(itemView)

        init {
            baseItemChooseFileListBinding?.baseItemCfListCbox?.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    R.id.base_item_cf_list_cbox -> {
                        val bean = list[adapterPosition]
                        bean.checked =
                            baseItemChooseFileListBinding?.baseItemCfListCbox?.isChecked ?: false
                    }
                    else -> {
                        baseItemChooseFileListBinding?.baseItemCfListCbox?.performClick()
                    }
                }
            }
        }
    }

    inner class ChooseFileTableRvHolder(itemView: View) : ChooseFileRvHolder(itemView),
        View.OnClickListener {
        val baseItemChooseFileTableBinding: BaseItemChooseFileTableBinding? =
            DataBindingUtil.bind(itemView)

        init {
            baseItemChooseFileTableBinding?.baseItemCfTableCbox?.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (v?.id) {
                    R.id.base_item_cf_table_cbox -> {
                        val bean = list[adapterPosition]
                        bean.checked =
                            baseItemChooseFileTableBinding?.baseItemCfTableCbox?.isChecked ?: false
                    }
                    else -> {
                        baseItemChooseFileTableBinding?.baseItemCfTableCbox?.performClick()
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

}