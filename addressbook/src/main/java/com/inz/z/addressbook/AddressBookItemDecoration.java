package com.inz.z.addressbook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.z.addressbook.adapter.AddressBookRvAdapter;
import com.inz.z.addressbook.bean.AddressBookPinyinBean;
import com.inz.z.addressbook.tool.Tools;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/3/5 12:48.
 */
public class AddressBookItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "AddressBookItemDecorati";

    private Context mContext;
    private int headerHeight = 48;
    private int headerPaddingStart = 16;
    private List<AddressBookPinyinBean> dataList;

    private Paint headerPaint;
    private Paint textPaint;

    public AddressBookItemDecoration(Context mContext, List<AddressBookPinyinBean> dataList) {
        this(mContext, 48, dataList);
    }

    public AddressBookItemDecoration(Context context, int headerHeight, List<AddressBookPinyinBean> dataList) {
        this(context, headerHeight, 16, dataList);
    }

    public AddressBookItemDecoration(Context mContext, int headerHeight, int headerPaddingStart, List<AddressBookPinyinBean> dataList) {
        this.mContext = mContext;
        this.headerHeight = Tools.dp2px(mContext, headerHeight);
        this.headerPaddingStart = Tools.dp2px(mContext, headerPaddingStart);
        this.dataList = dataList;
        initPaint();
    }

    private void initPaint() {
        headerPaint = new Paint();
        headerPaint.setColor(Color.LTGRAY);
        headerPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Tools.sp2px(mContext, 16));

    }

    public void setDataList(List<AddressBookPinyinBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (dataList != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(view);
                boolean canDraw = false;
                String tag = dataList.get(position).getPinyinFirstChar();
                if (position > 0) {
                    String upperTag = dataList.get(position - 1).getPinyinFirstChar();
                    if (!tag.equals(upperTag)) {
                        canDraw = true;
                    }
                } else {
                    canDraw = true;
                }
                Log.i(TAG, "onDraw: tag = " + tag);
                if (canDraw) {
                    drawHeaderView(c, parent, view, tag);
                }
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (dataList != null) {
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (!(adapter instanceof AddressBookRvAdapter)) {
                return;
            }
            int itemCount = adapter.getItemCount();
            if (itemCount == 1) {
                return;
            }
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager)
                    || LinearLayoutManager.VERTICAL != ((LinearLayoutManager) layoutManager).getOrientation()) {
                return;
            }
            int position = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
            String tag = dataList.get(position).getPinyinFirstChar();
            RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                boolean canDraw = false;
                if (position + 1 < dataList.size() && !tag.equals(dataList.get(position + 1).getPinyinFirstChar())) {
                    if (view.getBottom() <= headerHeight) {
                        c.save();
                        canDraw = true;
                        c.translate(0, view.getHeight() + view.getTop() - headerHeight);
                    }
                }
                drawFloatHeaderView(c, parent, tag);
                if (canDraw) {
                    c.restore();
                }
            }
        }

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (dataList != null) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager)
                    || LinearLayoutManager.VERTICAL != ((LinearLayoutManager) layoutManager).getOrientation()) {
                return;
            }
            int position = parent.getChildAdapterPosition(view);
            boolean canDraw = false;
            if (position > 0) {
                String tag = dataList.get(position).getPinyinFirstChar();
                String upperTag = dataList.get(position - 1).getPinyinFirstChar();
                if (!tag.equals(upperTag)) {
                    canDraw = true;
                }
            } else {
                canDraw = true;
            }
            if (canDraw) {
                outRect.set(0, headerHeight, 0, 0);
            }
        }
    }

    /**
     * 绘制顶部栏
     */
    private void drawHeaderView(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull View view, String tag) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        int startX = parent.getPaddingStart();
        int endX = parent.getWidth() - parent.getPaddingEnd();
        int endY = view.getTop() - layoutParams.topMargin;
        int startY = endY - headerHeight;
        c.drawRect(startX, startY, endX, endY, headerPaint);
        int x = startX + headerPaddingStart;
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int centerY = headerHeight / 2 - (fontMetricsInt.top + fontMetricsInt.bottom) / 2;
        int y = startY + centerY;
        c.drawText(tag, x, y, textPaint);
    }

    /**
     * 绘制 悬浮栏
     */
    private void drawFloatHeaderView(@NonNull Canvas c, @NonNull RecyclerView parent, String tag) {
        int left = parent.getPaddingStart();
        int right = parent.getWidth() - parent.getPaddingEnd();
        int bottom = headerHeight;
        int top = 0;
        c.drawRect(left, top, right, bottom, headerPaint);
        int x = left + headerPaddingStart;
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int centerY = headerHeight / 2 - (fontMetricsInt.top + fontMetricsInt.bottom) / 2;
        int y = left + centerY;
        c.drawText(tag, x, y, textPaint);
    }
}
