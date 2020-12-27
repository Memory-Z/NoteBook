package com.inz.slide_table.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.slide_table.view.SlideTableListener;

/**
 * 行标题 适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 13:17.
 */
public abstract class SlideRowTitleRvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected SlideTableListener slideTableListener;

    public SlideRowTitleRvViewHolder(@NonNull View itemView) {
        super(itemView);
        this.slideTableListener = getSlideTableListener();
        itemView.setOnClickListener(this);
    }

    public void setSlideTableListener(SlideTableListener slideTableListener) {
        this.slideTableListener = slideTableListener;
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (v.getId() == itemView.getId()) {
            if (slideTableListener != null) {
                slideTableListener.onRowClick(v, position);
            }
        }
    }

    /**
     * 获取监听
     *
     * @return 监听
     */
    @Nullable
    protected abstract SlideTableListener getSlideTableListener();

    /**
     * 行点击
     *
     * @param v        View
     * @param position 位置
     */
    protected abstract void onClickRow(@Nullable View v, int position);
}
