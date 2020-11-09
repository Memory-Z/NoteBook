package com.inz.slide_table.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.slide_table.bean.SlideTableBean;
import com.inz.slide_table.view.SlideTableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动 表格 适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 10:45.
 */
public abstract class SlideRvAdapter<T extends SlideTableBean<?>, VH extends SlideRvViewHolder> extends RecyclerView.Adapter<VH>
        implements SlideTableListener {
    private static final String TAG = "SlideRvAdapter";

    private static final int NO_SELECTED_ROW = -1;

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected List<T> dataList = new ArrayList<>();
    /**
     * 选中 行 ； 默认无选中
     */
    protected int selectedRowPosition = NO_SELECTED_ROW;

    protected LinearLayoutManager linearLayoutManager;

    public SlideRvAdapter(Context mContext) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.linearLayoutManager = new LinearLayoutManager(mContext);
        this.linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateVH(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onBindVH(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onRowClick(@Nullable View view, int position) {
        Log.i(TAG, "onRowClick: selectedPosition = " + selectedRowPosition + " , position = " + position);
        if (selectedRowPosition != position) {
            if (selectedRowPosition != NO_SELECTED_ROW) {
                T row = getItemByPosition(selectedRowPosition);
                if (row != null) {
                    row.setSelectedRow(false);
                    notifyItemChanged(selectedRowPosition, row);
                }
            }
            T row = getItemByPosition(position);
            if (row != null) {
                row.setSelectedRow(true);
                // 更新选中项
                notifyItemChanged(position, row);
                selectedRowPosition = position;
            }
        }
        // else: nothing to do . this row is selected.

    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    protected abstract VH onCreateVH(@NonNull ViewGroup parent, int viewType);

    protected abstract void onBindVH(@NonNull VH holder, int position);

    /**
     * 通过项获取数据行
     *
     * @param position 行数【含标题栏 】
     * @return 每行数据
     */
    @Nullable
    public T getItemByPosition(int position) {
        if (position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        }
        return null;
    }


    /**
     * 设置显示内容数据
     *
     * @param contentDataList 内容数据列表
     */
    public void setContentData(List<T> contentDataList) {
        this.selectedRowPosition = NO_SELECTED_ROW;
        this.dataList.clear();
        this.dataList.addAll(contentDataList);
        notifyDataSetChanged();
    }

    /**
     * 替换某项 数据
     *
     * @param data     数据
     * @param position 项
     */
    public void replaceItemWithPosition(T data, int position) {
        T item = getItemByPosition(position);
        if (item != null) {
            this.dataList.set(position, data);
            notifyItemChanged(position);
        }
    }
}
