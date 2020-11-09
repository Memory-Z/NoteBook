package com.inz.slide_table.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 列 适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 14:00.
 */
public abstract class SlideColumnRvAdapter<VH extends SlideColumnRvViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<String> columnList = new ArrayList<>();
    protected LayoutInflater mLayoutInflater;

    public SlideColumnRvAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return columnList == null ? 0 : columnList.size();
    }

    public void refreshColumn(List<String> strList) {
        this.columnList.clear();
        this.columnList.addAll(strList);
        notifyDataSetChanged();
    }
}
