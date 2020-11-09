package com.inz.slide_table.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * 滑动表单 数据封装类
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 10:40.
 */
public abstract class SlideTableBean<T> implements Serializable {

    /**
     * 数据内容
     */
    private T data = null;
    /**
     * 顶部标题行
     */
    private boolean headerRow = false;

    /**
     * 选中行
     */
    private boolean selectedRow = false;

    @Nullable
    protected abstract String getRowTitle();

    /**
     * 转换为数据列
     *
     * @return 显示数据列表
     */
    @NonNull
    protected abstract List<String> toDataColumnList();

    /**
     * 获取数据列表 列标题
     *
     * @return 列标题
     */
    @NonNull
    protected abstract List<String> toDataHeaderList();

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    public boolean isSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(boolean selectedRow) {
        this.selectedRow = selectedRow;
    }

    @NonNull
    @Override
    public String toString() {
        return "SlideTableBean{" +
                "data=" + data +
                ", headerRow=" + headerRow +
                ", selectedRow=" + selectedRow +
                '}';
    }
}
