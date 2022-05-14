package com.inz.z.note_book.view.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.base.AbsBaseRvAdapter
import com.inz.z.base.base.AbsBaseRvViewHolder
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.FileInfo
import com.inz.z.note_book.databinding.ItemNewNoteInfoImageBinding

/**
 * 新笔记  图片 适配器
 *
 * ====================================================
 * Create by 11654 in 2022/5/12 21:34
 */
class NewNoteInfoImageRvAdapter(mContext: Context?) :
    AbsBaseRvAdapter<FileInfo, NewNoteInfoImageRvAdapter.NewNoteInfoImageRvViewHolder>(mContext) {

    companion object {
        private const val TAG = "NewNoteInfoImageRvAdapt"
    }

    private var options: RequestOptions? = null

    var listener: NewNoteInfoImageRvAdapterListener? = null

    init {
        options = RequestOptions()
            .override(400, 400)
            .centerInside()
            .error(R.drawable.image_load_error)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onCreateVH(
        parent: ViewGroup,
        viewType: Int
    ): NewNoteInfoImageRvAdapter.NewNoteInfoImageRvViewHolder {
        val view = ItemNewNoteInfoImageBinding.inflate(mLayoutInflater, parent, false)
        return NewNoteInfoImageRvViewHolder(view)
    }

    override fun onBindVH(
        holder: NewNoteInfoImageRvAdapter.NewNoteInfoImageRvViewHolder,
        position: Int
    ) {
        val item = getItemByPosition(position)
        holder.binding.itemNniiIv.let {
            if (options != null) {
                Glide.with(it).load(item?.uri).apply(options!!).into(it)
            }
        }
    }

    inner class NewNoteInfoImageRvViewHolder(val binding: ItemNewNoteInfoImageBinding) :
        AbsBaseRvViewHolder(binding.root), View.OnClickListener {
        init {

            binding.itemNniiCloseIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (RecyclerView.NO_POSITION != position) {
                if (v?.id == binding.itemNniiCloseIv.id) {
                    listener?.clickClose(v, position)
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Glide.with(mContext).clear(recyclerView)
        options = null
        listener = null
    }

    interface NewNoteInfoImageRvAdapterListener {
        fun clickClose(v: View?, position: Int)
    }
}