package com.inz.slide_table;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.inz.slide_table.adapter.SlideRowTitleRvAdapter;
import com.inz.slide_table.adapter.SlideRowTitleRvViewHolder;
import com.inz.slide_table.view.SlideTableListener;

/**
 * 默认 标题适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 13:24.
 */
public class BaseSlideRowRvAdapter extends SlideRowTitleRvAdapter<BaseSlideTableBean, BaseSlideRowRvAdapter.BaseSlideRowTitleRvViewHolder> {


    private final SlideTableListener listener;
    private boolean showOrder = false;
    private final Context mContext;

    public BaseSlideRowRvAdapter(Context context, SlideTableListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseSlideRowTitleRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_slide_row_title, parent, false);
        return new BaseSlideRowTitleRvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseSlideRowTitleRvViewHolder holder, int position) {
        String title = titleList.get(position).getRowTitle();
        holder.titleNameTv.setText(title);
        holder.orderTv.setVisibility(showOrder ? View.VISIBLE : View.GONE);
        String orderStr = position + ".";
        holder.orderTv.setText(orderStr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.itemView.setForeground(
                    selectedRow == position
                            ? ContextCompat.getDrawable(mContext, R.drawable.row_left_selected)
                            : ContextCompat.getDrawable(mContext, R.drawable.row_unselected)
            );
        }
    }


    public class BaseSlideRowTitleRvViewHolder extends SlideRowTitleRvViewHolder {
        TextView titleNameTv;
        TextView orderTv;

        public BaseSlideRowTitleRvViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTv = itemView.findViewById(R.id.item_slide_row_title_order_tv);
            titleNameTv = itemView.findViewById(R.id.item_slide_row_title_name_tv);
        }

        @Override
        protected SlideTableListener getSlideTableListener() {
            return listener;
        }

        @Override
        protected void onClickRow(@Nullable View v, int position) {
            selectedRow = position;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    public void setShowOrder(boolean showOrder) {
        this.showOrder = showOrder;
        notifyDataSetChanged();
    }
}
