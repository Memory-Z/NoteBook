package com.inz.slide_table.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.slide_table.bean.SlideTableBean;
import com.inz.slide_table.view.SlideTableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 行标题 适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 11:53.
 */
public abstract class SlideRowTitleRvAdapter<T extends SlideTableBean<?>, VH extends SlideRowTitleRvViewHolder> extends RecyclerView.Adapter<VH> {

    private static final int NO_SELECTED_ROW = -1;

    protected LayoutInflater mLayoutInflater;
    protected List<T> titleList = new ArrayList<>();
    protected int selectedRow = NO_SELECTED_ROW;
    protected SlideTableListener slideTableListener;

    public SlideRowTitleRvAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return titleList == null ? 0 : titleList.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 刷新标题列表
     *
     * @param titleList 标题列表
     */
    public void refreshTitleList(@NonNull List<T> titleList) {
        this.selectedRow = NO_SELECTED_ROW;
        this.titleList.clear();
        this.titleList.addAll(titleList);
        notifyDataSetChanged();
    }

    /**
     * 获取行标题
     *
     * @param position position
     * @return 标题名称
     */
    @Nullable
    public String getTitleByRow(int position) {
        if (position >= 0 && position < getItemCount()) {
            return titleList.get(position).getRowTitle();
        }
        return null;
    }

    public void setSlideTableListener(SlideTableListener slideTableListener) {
        this.slideTableListener = slideTableListener;
    }
}
