package com.inz.z.base.view.activity.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.R
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.databinding.BaseItemChooseFileListBinding
import com.inz.z.base.databinding.BaseItemChooseFileTableBinding
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.L
import com.inz.z.base.util.ThreadPoolUtils
import com.inz.z.base.view.activity.ChooseFileActivity
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 选择图片适配器器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 11:53.
 */
class ChooseFileRvAdapter :
    AbsBaseRvAdapter<BaseChooseFileBean, ChooseFileRvAdapter.ChooseFileRvHolder> {

    companion object {
        private const val TAG = "ChooseFileRvAdapter"
        private const val DEFAULT_MAX_CHOOSE_FILE_COUNT = 10
    }

    @ChooseFileActivity.ShowMode
    private var showMode = ChooseFileActivity.MODE_TABLE

    /**
     * 监听
     */
    var listener: ChooseFileRvAdapterListener? = null

    /**
     * 显示选择界面。默认不显示；
     * 显示后，点击图片进行选中。（无法预览）
     */
    private val showSelectView = AtomicBoolean(false)

    /**
     * 最大文件选择数。
     */
    private var maxSelectedCount = DEFAULT_MAX_CHOOSE_FILE_COUNT

    /**
     * 当前文件选择数
     */
    private var currentSelectedCount = 0

    constructor(mContext: Context?) : this(mContext, ChooseFileActivity.MODE_TABLE)

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


    override fun getItemViewType(position: Int): Int {
        return showMode
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ChooseFileRvHolder {
        return when (viewType) {
            ChooseFileActivity.MODE_TABLE -> {
//                val view =
//                    mLayoutInflater.inflate(R.layout.base_item_choose_file_table, parent, false)
                val binding = BaseItemChooseFileTableBinding.inflate(mLayoutInflater, parent, false)
                ChooseFileTableRvHolder(binding)
            }
            else -> {
//                val view =
//                    mLayoutInflater.inflate(R.layout.base_item_choose_file_list, parent, false)
                val binding = BaseItemChooseFileListBinding.inflate(mLayoutInflater, parent, false)
                ChooseFileListRvHolder(binding)
            }
        }
    }

    override fun onBindVH(holder: ChooseFileRvHolder, position: Int) {
        val bean = list[position]
        var checkBox: CheckBox? = null
        if (holder is ChooseFileListRvHolder) {
            val iv = holder.baseItemChooseFileListBinding.baseItemCfListIv
            checkBox = holder.baseItemChooseFileListBinding.baseItemCfListCbox
            holder.baseItemChooseFileListBinding.baseItemCfListContentTv.text = bean.fileChangeDate
            holder.baseItemChooseFileListBinding.baseItemCfListTitleTv.text = bean.fileName
            if (bean.fileIsDirectory) {
                iv.apply {
                    Glide.with(mContext).load(R.drawable.ic_baseline_folder_24).apply(requestOption)
                        .into(this)
                    ImageViewCompat.setImageTintList(this, colorPrimaryCsl)
                }
            } else {
                iv.apply {
                    when (bean.fileType) {
                        Constants.FileType.FILE_TYPE_IMAGE -> {
                            Glide.with(mContext).load(bean.filePath).apply(requestOption)
                                .into(this)
                            ImageViewCompat.setImageTintList(this, null)
                        }
                        Constants.FileType.FILE_TYPE_AUDIO -> {
                            loadImageIcon(this, R.drawable.ic_file_music)
                        }
                        Constants.FileType.FILE_TYPE_VIDEO -> {
                            loadImageIcon(this, R.drawable.ic_file_video)
                        }
                        Constants.FileType.FILE_TYPE_APPLICATION -> {
                            loadImageIcon(this, R.drawable.ic_file_installation_pa)
                        }
                        Constants.FileType.FILE_TYPE_TEXT -> {
                            loadImageIcon(this, R.drawable.ic_file_txt)
                        }
                        else -> {
                            loadImageIcon(this, R.drawable.ic_file_unknown)
                        }
                    }
                }
            }
            checkBox.visibility = if (bean.fileIsDirectory) View.GONE else View.VISIBLE
            // 设置 CheckBox 是否 可以点击
            checkBox.let {
                setCanClickable(it)
            }
        } else if (holder is ChooseFileTableRvHolder) {
            val iv = holder.baseItemChooseFileTableBinding.baseItemCfTableIv
            checkBox = holder.baseItemChooseFileTableBinding.baseItemCfTableCbox
            holder.baseItemChooseFileTableBinding.baseItemCfOverlyIv.visibility =
                if (bean.checked) View.VISIBLE else View.GONE
            holder.baseItemChooseFileTableBinding.baseItemCfTableContentTv.text =
                bean.fileChangeDate
            holder.baseItemChooseFileTableBinding.baseItemCfTableTitleTv.text = bean.fileName
            if (bean.fileIsDirectory) {
                iv.apply {
                    Glide.with(mContext).load(R.drawable.ic_baseline_folder_24).apply(requestOption)
                        .into(this)
                    ImageViewCompat.setImageTintList(this, colorPrimaryCsl)
                }
            } else {
                iv.apply {
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
            // 仅 文件时显示 checkbox
            checkBox.visibility =
                if (!showSelectView.get()) View.GONE
                else if (bean.fileIsDirectory) View.GONE
                else View.VISIBLE
        }
        checkBox?.isChecked = bean.checked
    }

    /**
     * 加载图标。
     */
    private fun loadImageIcon(imageView: ImageView, @DrawableRes resId: Int) {
        Glide.with(mContext).load(resId).apply(requestOption).into(imageView)
        ImageViewCompat.setImageTintList(imageView, null)
    }

    /**
     * 文件选择 Holder.
     */
    abstract class ChooseFileRvHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * 选择文件 list Holder.
     */
    inner class ChooseFileListRvHolder(val baseItemChooseFileListBinding: BaseItemChooseFileListBinding) :
        ChooseFileRvHolder(baseItemChooseFileListBinding.root),
        View.OnClickListener {

        init {
            baseItemChooseFileListBinding.baseItemCfListCbox.setOnClickListener(this)
            baseItemChooseFileListBinding.baseItemCfListIv.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val bean = list[adapterPosition]
                when (v?.id) {
                    R.id.base_item_cf_list_cbox -> {
                        val checked =
                            baseItemChooseFileListBinding.baseItemCfListCbox.isChecked ?: false
                        bean.checked = checked
                        if (!checked) {
                            listener?.removeChoseFile(adapterPosition, v)
                        } else {
                            listener?.addChoseFile(adapterPosition, v)
                        }
                        // 更新 Check 点击状态
                        dealCanClickable()
                    }
                    R.id.base_item_cf_list_iv -> {
                        listener?.showFullImage(adapterPosition, v)
                    }
                    else -> {
                        if (bean.fileIsDirectory && v != null) {
                            listener?.openFileDirectory(adapterPosition, v)
                        } else {
                            baseItemChooseFileListBinding.baseItemCfListCbox.performClick()
                        }
                    }
                }
            }
        }
    }

    /**
     * 选择文件Table ViewHolder
     */
    inner class ChooseFileTableRvHolder(val baseItemChooseFileTableBinding: BaseItemChooseFileTableBinding) :
        ChooseFileRvHolder(baseItemChooseFileTableBinding.root),
        View.OnClickListener {

        init {
            baseItemChooseFileTableBinding.baseItemCfTableCbox.setOnClickListener(this)
            baseItemChooseFileTableBinding.baseItemCfTableIv.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val bean = list[adapterPosition]
                when (v?.id) {
                    R.id.base_item_cf_table_cbox -> {
                        // 切换选中状态。
                        targetCheckStatus(bean, v, adapterPosition)
                    }
                    R.id.base_item_cf_table_iv -> {
                        // 显示选中界面时，切换checkbox 状态，否则进行预览
                        if (showSelectView.get()) {
                            targetCheckStatus(bean, v, adapterPosition)
                        } else {
                            listener?.showFullImage(adapterPosition, v)
                        }
                    }
                    else -> {
                        if (bean.fileIsDirectory && v != null) {
                            listener?.openFileDirectory(adapterPosition, v)
                        } else {
                            baseItemChooseFileTableBinding.baseItemCfTableCbox.performClick()
                        }
                    }
                }
            }
        }

        /**
         * 切换选中状态。
         * @param bean 数据项
         * @param v View.
         */
        private fun targetCheckStatus(bean: BaseChooseFileBean, v: View, position: Int) {
            val checked = !bean.checked
            bean.checked = checked
            if (checked) {
                // 超出选中 回退。
                if (currentSelectedCount >= maxSelectedCount) {
                    bean.checked = false
                    notifyItemChanged(position)
                } else {
                    currentSelectedCount += 1
                }
                listener?.addChoseFile(adapterPosition, v)
            } else {
                currentSelectedCount -= 1
                listener?.removeChoseFile(adapterPosition, v)
            }
            notifyItemChanged(position)
            // 处理文件。
            dealChooseFile()
        }
    }

    /**
     * 加载更多 ViewHolder
     */
    inner class LoadMoreRvHolder(itemView: View) : ChooseFileRvHolder(itemView) {

    }

    /**
     * 有序处理文件。
     */
    private fun dealChooseFile() {
        L.i(TAG, "dealChooseFile: ")
        synchronized(this.list) {
            ThreadPoolUtils
                .getScheduleThread(TAG + "_deal_choose_file")
                .execute(DealChooseFileRunnable())
        }
    }

    /**
     * 处理是否可 点击 状态 。
     */
    private fun dealCanClickable() {
//        for ()
    }

    /**
     * 设置 View 是否可以进行点击
     * @param view 按钮
     */
    private fun setCanClickable(view: CheckBox) {
        // 当前是否 选择达最大值。
        val isSelectedMax = maxSelectedCount == currentSelectedCount
        L.i(TAG, "setCanClickable: --->> $view ---> isMax = $isSelectedMax")
        // 判断当前是否 达最大选择
        if (isSelectedMax) {
            // 根据当前是否 选中 设置 view 是否可以 进行点击
            view.isClickable = view.isChecked
            view.isFocusable = view.isChecked
            view.isEnabled = view.isChecked
        } else {
            // 未到 最大选择， 可以进行点击操作
            view.isClickable = true
            view.isFocusable = true
            view.isEnabled = true
        }
    }

    /**
     * 是否 达到最大选择量
     */
    private fun isMaxSelected() = maxSelectedCount == currentSelectedCount

    /**
     * 处理选中 文件线程.
     */
    private inner class DealChooseFileRunnable : Runnable {
        override fun run() {
            val chooseFileList = getSelectedFileList()
            val selectedFileSize = getFileSize(chooseFileList)
            L.i(TAG, "run: ---> selectedFileSize = $selectedFileSize")
            listener?.chooseFileList(chooseFileList, selectedFileSize)
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
        fun openFileDirectory(position: Int, view: View)

        /**
         * 获取选中文件。
         * @param list 文件列表
         * @param fileSize 文件总大小
         */
        fun chooseFileList(list: List<BaseChooseFileBean>, fileSize: Long)

    }

    /**
     * 切换显示内容.
     */
    fun changeShowMode(@ChooseFileActivity.ShowMode mode: Int) {
        // TODO: 2020/10/22 切换显示内容
        this.showMode = mode

        notifyDataSetChanged()
    }

    /**
     * 切换显示选中界面，
     * <b>仅在 表格模式下 可用，</b>
     */
    fun targetShowSelectView(showSelect: Boolean) {
        L.i(TAG, "targetShowSelectView: > $showSelect")
        this.showSelectView.set(showSelect)
        notifyDataSetChanged()
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
     * 获取文件大小
     * @param list 文件列表
     */
    fun getFileSize(list: List<BaseChooseFileBean>): Long {
        var totalSize = 0L
        list.forEach {
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
                if (baseChooseFileBean.filePath == it.filePath) {
                    baseChooseFileBean.checked = it.checked
                    return@forEach
                }
            }
        }
        notifyDataSetChanged()
    }


}