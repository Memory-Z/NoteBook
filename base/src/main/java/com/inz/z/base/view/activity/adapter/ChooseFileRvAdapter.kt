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
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.R
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.databinding.BaseItemChooseFileListBinding
import com.inz.z.base.databinding.BaseItemChooseFileTableBinding
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.Constants
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

    var listener: ChooseFileRvAdapterListener? = null

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
                        Constants.FileType.FILE_TYPE_IMAGE -> {
                            Glide.with(mContext).load(bean.filePath).apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        Constants.FileType.FILE_TYPE_AUDIO -> {
                            Glide.with(mContext).load(R.drawable.ic_file_music).apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        Constants.FileType.FILE_TYPE_VIDEO -> {
                            Glide.with(mContext).load(R.drawable.ic_file_video)
                                .apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        Constants.FileType.FILE_TYPE_APPLICATION -> {
                            Glide.with(mContext).load(R.drawable.ic_file_installation_pa)
                                .apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        Constants.FileType.FILE_TYPE_TEXT -> {
                            Glide.with(mContext).load(R.drawable.ic_file_txt)
                                .apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        else -> {
                            Glide.with(mContext).load(R.drawable.ic_file_unknown)
                                .apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
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
                        Constants.FileType.FILE_TYPE_IMAGE -> {
                            Glide.with(mContext).load(bean.filePath).apply(requestOption)
                                .into(iv)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        else -> {
                            Glide.with(mContext).load(R.drawable.ic_file_unknown)
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
            baseItemChooseFileListBinding?.baseItemCfListIv?.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val bean = list[adapterPosition]
                when (v?.id) {
                    R.id.base_item_cf_list_cbox -> {
                        val checked =
                            baseItemChooseFileListBinding?.baseItemCfListCbox?.isChecked ?: false
                        bean.checked = checked
                        if (!checked) {
                            listener?.removeChoseFile(adapterPosition, v)
                        } else {
                            listener?.addChoseFile(adapterPosition, v)
                        }
                    }
                    R.id.base_item_cf_list_iv -> {
                        listener?.showFullImage(adapterPosition, v)
                    }
                    else -> {
                        if (bean.fileIsDirectory && v != null) {
                            listener?.openFileDirectory(adapterPosition, v)
                        } else {
                            baseItemChooseFileListBinding?.baseItemCfListCbox?.performClick()
                        }
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
            baseItemChooseFileTableBinding?.baseItemCfTableIv?.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val bean = list[adapterPosition]
                when (v?.id) {
                    R.id.base_item_cf_table_cbox -> {
                        val checked =
                            baseItemChooseFileTableBinding?.baseItemCfTableCbox?.isChecked ?: false

                        bean.checked = checked
                        if (!checked) {
                            listener?.removeChoseFile(adapterPosition, v)
                        } else {
                            listener?.addChoseFile(adapterPosition, v)
                        }
                    }
                    R.id.base_item_cf_table_iv -> {
                        listener?.showFullImage(adapterPosition, v)
                    }
                    else -> {
                        if (bean.fileIsDirectory && v != null) {
                            listener?.openFileDirectory(adapterPosition, v)
                        } else {
                            baseItemChooseFileTableBinding?.baseItemCfTableCbox?.performClick()
                        }
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 文件选择监听
     */
    interface ChooseFileRvAdapterListener {
        /**
         * 添加选中文件
         */
        fun addChoseFile(position: Int, view: View)

        /**
         * 移除选中文件
         */
        fun removeChoseFile(position: Int, view: View)

        /**
         * 显示全屏图像
         */
        fun showFullImage(position: Int, view: View)

        /**
         * 进入目录
         */
        fun openFileDirectory(position: Int, view: View);

    }

    fun changeShowMode(@ChooseFileActivity.ShowMode mode: Int) {

        notifyItemInserted(0)
    }

    /**
     * 获取选中文件
     */
    fun getSelectedFileList(): List<BaseChooseFileBean> {
        return this.list.filter {
            return@filter it.checked
        }
    }

    /**
     * 获取选中文件大小
     */
    fun getSelectedFileTotalSize(): Long {
        var totalSize = 0L;
        getSelectedFileList().forEach {
            totalSize += it.fileLength
        }
        return totalSize
    }

    /**
     * 切换项数据
     */
    fun changeItemData(bean: BaseChooseFileBean, position: Int) {
        val item = getItemByPosition(position)
        if (item != null) {
            this.list.set(position, bean)
            notifyItemChanged(position)
        }
    }

    /**
     * 同步刷新 选中 数据
     */
    fun notifyChooseItemList(chooseList: List<BaseChooseFileBean>) {
        this.list.forEachIndexed { index, baseChooseFileBean ->
            chooseList.forEach {
                if (baseChooseFileBean.filePath.equals(it.filePath)) {
                    baseChooseFileBean.checked = it.checked
                    return@forEach
                }
            }
        }
        notifyDataSetChanged()
    }

}