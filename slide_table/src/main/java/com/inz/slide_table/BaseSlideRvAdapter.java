package com.inz.slide_table;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.inz.slide_table.adapter.SlideRvAdapter;
import com.inz.slide_table.adapter.SlideRvViewHolder;
import com.inz.slide_table.view.SlideRowItemView;

/**
 * 默认适配器
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 13:48.
 */
public class BaseSlideRvAdapter extends SlideRvAdapter<BaseSlideTableBean, BaseSlideRvAdapter.BaseSlideRvViewHolder> {

    private static final String TAG = "BaseSlideRvAdapter";

    private static SlideRowItemView.ItemTextViewSet itemTextViewSet;

    public BaseSlideRvAdapter(Context mContext) {
        super(mContext);
        initSlideRowItemTextViewSet(mContext);
    }

    /**
     * 初始化 项配置
     *
     * @param context 上下文
     */
    private void initSlideRowItemTextViewSet(Context context) {
        itemTextViewSet = new SlideRowItemView.ItemTextViewSet.Builder(context)
                .setItemWidthDp(48, 64)
                .setTextBackgroundColor(Color.GRAY, Color.GREEN)
                .setTextColorInt(Color.BLACK)
                .build();
    }

    @Override
    protected BaseSlideRvViewHolder onCreateVH(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_slide_column, parent, false);
        return new BaseSlideRvViewHolder(view, itemTextViewSet);
    }

    @Override
    protected void onBindVH(@NonNull BaseSlideRvViewHolder holder, int position) {
        BaseSlideTableBean bean = getItemByPosition(position);
        Log.i(TAG, "onBindVH: " + bean);
        if (bean != null) {
            if (bean.isHeaderRow()) {
                holder.slideRowItemView.setItemList(bean.toDataHeaderList());
            } else {
                holder.slideRowItemView.setItemList(bean.toDataColumnList());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.slideRowItemView.setForeground(
                        bean.isSelectedRow()
                                ? ContextCompat.getDrawable(mContext, R.drawable.row_right_selected)
                                : ContextCompat.getDrawable(mContext, R.drawable.row_unselected)
                );
            }
        }
    }

    static class BaseSlideRvViewHolder extends SlideRvViewHolder {

        public BaseSlideRvViewHolder(@NonNull View itemView, SlideRowItemView.ItemTextViewSet itemTextViewSet) {
            super(itemView, itemTextViewSet);
        }

        @NonNull
        @Override
        protected SlideRowItemView getSlideRowItemView(@NonNull View itemView) {
            return itemView.findViewById(R.id.item_slide_col_sriv);
        }
    }

}
