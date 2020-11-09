package com.inz.slide_table.view;

import android.view.View;

import androidx.annotation.Nullable;

/**
 * 滑动表格 监听
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/09 13:18.
 */
public interface SlideTableListener {

    /**
     * 行点击
     *
     * @param view     View
     * @param position 列表位置
     */
    void onRowClick(@Nullable View view, int position);



}
