package com.inz.slide_table.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.slide_table.view.SlideRowItemView;

/**
 * 标准 滑动表格适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 10:46.
 */
public abstract class SlideRvViewHolder extends RecyclerView.ViewHolder {

    public SlideRowItemView slideRowItemView;

    public SlideRvViewHolder(@NonNull View itemView, SlideRowItemView.ItemTextViewSet itemTextViewSet) {
        super(itemView);
        slideRowItemView = getSlideRowItemView(itemView);
        slideRowItemView.setItemTextViewSet(itemTextViewSet);
    }

    @NonNull
    protected abstract SlideRowItemView getSlideRowItemView(@NonNull View itemView);


}
