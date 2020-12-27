package com.inz.slide_table;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.inz.slide_table.adapter.SlideColumnRvAdapter;
import com.inz.slide_table.adapter.SlideColumnRvViewHolder;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 14:24.
 */
public class BaseSlideColumnRvAdapter extends SlideColumnRvAdapter<BaseSlideColumnRvAdapter.BaseSlideColumnRvViewHolder> {

    public BaseSlideColumnRvAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseSlideColumnRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_slide_column_item, parent, false);
        return new BaseSlideColumnRvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseSlideColumnRvViewHolder holder, int position) {
        holder.columnTv.setText(columnList.get(position));
    }

    static class BaseSlideColumnRvViewHolder extends SlideColumnRvViewHolder {
        TextView columnTv;

        public BaseSlideColumnRvViewHolder(@NonNull View itemView) {
            super(itemView);
            columnTv = itemView.findViewById(R.id.item_slide_col_item_tv);
        }
    }

}
