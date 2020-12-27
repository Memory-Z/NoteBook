package com.inz.slide_table.view;

/**
 * 行 项 属性
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/04 09:20.
 */
public interface RowItemLayoutParams {

    /**
     * 获取列 数
     *
     * @return 列数
     */
    int getColumnSize();

    /**
     * 每列 宽度
     *
     * @return 列 宽度 数组 length == {@link #getColumnSize()}
     */
    int[] columnWidthPixel();
}
