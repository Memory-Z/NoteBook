package com.inz.z.note_book.view.widget.mdel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.util.BaseTools
import com.inz.z.note_book.R

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 10:51.
 */
class DynamicItemDecoration(val mContext: Context) : RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = BaseTools.dp2px(
            mContext,
            8F
        )
    }
}