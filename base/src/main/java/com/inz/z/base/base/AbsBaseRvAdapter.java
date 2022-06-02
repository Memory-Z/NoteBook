package com.inz.z.base.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象类 ，用于 数据列表显示
 * T : 数据对象
 * VH : 显示视图对象
 * Create By 11654
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create By 2018/8/12 11:02
 */
public abstract class AbsBaseRvAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    /**
     * 上下文
     */
    public Context mContext;

    /**
     * 数据列表
     */
    public List<T> list;

    /**
     * 布局填充
     */
    protected LayoutInflater mLayoutInflater;

    public AbsBaseRvAdapter(Context mContext) {
        this.mContext = mContext;
        this.list = new ArrayList<>();
        mLayoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return list.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mContext = null;
        mLayoutInflater = null;
        this.list.clear();
        this.list = null;
    }

    /**
     * 刷新数据
     *
     * @param list 数据列表
     */
    public void refreshData(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     *
     * @param list 数据列表
     */
    public void loadMoreData(List<T> list, boolean haveMore) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 添加数据项
     *
     * @param data Data
     */
    public void addItemData(T data) {
        this.list.add(data);
        notifyItemInserted(this.list.size() - 1);
    }

    /**
     * 移除数据项
     *
     * @param position position
     */
    public void removeItemData(int position) {
        if (isUsablePosition(position)) {
            this.list.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 判断 Position 是否有效
     *
     * @param position position
     * @return 是否有效
     */
    public boolean isUsablePosition(int position) {
        return position >= 0 && position < this.list.size();
    }

    /**
     * 获取项
     *
     * @param position 位置
     * @return 值
     */
    @Nullable
    public T getItemByPosition(int position) {
        if (isUsablePosition(position)) {
            return this.list.get(position);
        }
        return null;
    }

    /**
     * 更新数据项
     *
     * @param item     数据项
     * @param position 位置
     */
    public void updateItemByPosition(@NonNull T item, int position) {
        if (isUsablePosition(position)) {
            this.list.set(position, item);
            notifyItemChanged(position);
        }
    }

    /**
     * 创建 ViewHolder
     *
     * @param parent   父布局
     * @param viewType 类型
     * @return ViewHolder
     */
    public abstract VH onCreateVH(@NonNull ViewGroup parent, int viewType);

    /**
     * 绑定 ViewHolder
     *
     * @param holder   ViewHolder
     * @param position N
     */
    public abstract void onBindVH(@NonNull VH holder, int position);
}
