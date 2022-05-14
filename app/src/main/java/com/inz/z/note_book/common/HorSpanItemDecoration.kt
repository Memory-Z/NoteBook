package com.inz.z.note_book.common

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.note_book.R

/**
 * 横向间隔
 *
 * ====================================================
 * Create by 11654 in 2022/5/14 11:05
 */
class HorSpanItemDecoration(val mContext: Context) : RecyclerView.ItemDecoration() {

    private val spanSize = mContext.resources.getDimensionPixelOffset(R.dimen.margin_layout)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(outRect.left, outRect.top, outRect.right + spanSize, outRect.bottom)
    }
}
