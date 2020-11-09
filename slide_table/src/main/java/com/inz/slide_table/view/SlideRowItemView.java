package com.inz.slide_table.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;

import com.inz.slide_table.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 自定义 LinearView
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/05 10:07.
 */
public class SlideRowItemView extends LinearLayout {
    private static final String TAG = "SlideRowItemView";
    private static final int DEFAULT_ITEM_WIDTH_DP = 96;
    private static final int DEFAULT_ITEM_HEIGHT_DP = 48;
    /**
     * 默认 textView 大小
     */
    private static final int DEFAULT_ITEM_TEXT_SIZE = 14;
    private static final int DEFAULT_LAYOUT_PADDING_DP = 4;


    private final Context mContext;
    private List<String> itemList;
    private List<TextView> itemTvList;

    private int layoutPaddingSize = 0;

    private ItemTextViewSet itemTextViewSet;

    public SlideRowItemView(Context context) {
        this(context, null);
    }

    public SlideRowItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideRowItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SlideRowItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        if (itemTextViewSet == null) {
            itemTextViewSet = getDefaultTextViewSet();
        }
        initViewStyle(attrs);
        initView();
    }

    @SuppressLint("RestrictedApi")
    private void initViewStyle(AttributeSet attrs) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.SlideRowItemView, 0, 0);
        float width = array.getDimension(R.styleable.SlideRowItemView_slide_item_width, 0F);
        Log.i(TAG, "initViewStyle: " + width);
        itemTextViewSet.itemWidth = (int) width;
        float marginStart = array.getDimension(R.styleable.SlideRowItemView_slide_item_margin_left, 0F);
        Log.i(TAG, "initViewStyle: " + marginStart);
        itemTextViewSet.itemMarginStart = (int) marginStart;
        array.recycle();
    }

    private void initView() {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        setLayoutStyle();

    }

    /**
     * 设置布局样式
     */
    private void setLayoutStyle() {

        if (layoutPaddingSize < 0) {
            layoutPaddingSize = dp2px(mContext, DEFAULT_LAYOUT_PADDING_DP);
        }
        setPadding(layoutPaddingSize, layoutPaddingSize, layoutPaddingSize, layoutPaddingSize);

        setTextViewList();
    }

    /**
     * 设置项列表 并 添加
     */
    private void setTextViewList() {
        if (mContext == null) {
            return;
        }
        Log.i(TAG, "setTextViewList: width = " + itemTextViewSet);
        if (itemTvList == null) {
            itemTvList = new ArrayList<>();
        }
        for (TextView tv : itemTvList) {
            if (tv.getParent() != null) {
                removeView(tv);
            }
        }
        // 清空内容 TextView;
        itemTvList.clear();
        if (itemList != null) {
            setFullItemTextBackground();
            for (int index = 0; index < itemList.size(); index++) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER_VERTICAL;
                lp.leftMargin = itemTextViewSet.itemMarginStart;
                lp.topMargin = itemTextViewSet.itemMarginTop;
                lp.rightMargin = itemTextViewSet.itemMarginEnd;
                lp.bottomMargin = itemTextViewSet.itemMarginBottom;
                lp.height = itemTextViewSet.itemHeight;
                // 设置 预设 宽
                lp.width = getItemTextWidth(index);
                String text = itemList.get(index);
                TextView textView = new TextView(mContext);
//                textView.setWidth(getItemTextWidth(index));
                textView.setText(text);
                textView.setGravity(itemTextViewSet.itemTextGravity);
                textView.setBackground(getItemTextBackground(index));
                textView.setSingleLine(true);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemTextViewSet.itemTextSize);
                textView.setTextColor(itemTextViewSet.itemTextColor);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setPadding(
                        itemTextViewSet.itemPaddingStart,
                        itemTextViewSet.itemPaddingTop,
                        itemTextViewSet.itemPaddingEnd,
                        itemTextViewSet.itemPaddingBottom
                );
                addView(textView, lp);
                itemTvList.add(textView);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthOrign = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        if (itemList != null) {
            width = getLayoutWidth();
        }
        Log.i(TAG, "onMeasure: Width = " + width + " Screen Width = " + widthOrign);
        width = Math.max(width, widthOrign);
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                width,
                MeasureSpec.EXACTLY
        );
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                itemTextViewSet.itemHeight,
                MeasureSpec.EXACTLY
        );
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private static int dp2px(Context context, float dpPixel) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dpPixel + .5F);
    }

    /**
     * 获取Layout 的 宽度
     *
     * @return layout 宽
     */
    private int getLayoutWidth() {
        int width = layoutPaddingSize * 2 + getPaddingStart() + getPaddingEnd();
        if (itemTextViewSet.itemWidthList != null && !itemTextViewSet.itemWidthList.isEmpty()) {
            if (itemList != null) {
                width += itemList.size() * (itemTextViewSet.itemMarginStart + itemTextViewSet.itemMarginEnd);
                int widthListSize = itemTextViewSet.itemWidthList.size();
                if (itemList.size() == widthListSize) {
                    for (Integer w : itemTextViewSet.itemWidthList) {
                        width += w;
                    }
                } else {
                    int nu = itemList.size() / widthListSize;
                    int m = itemList.size() % widthListSize;
                    for (int i = 0; i < widthListSize; i++) {
                        int count = nu;
                        if (i < m) {
                            count += 1;
                        }
                        width += itemTextViewSet.itemWidthList.get(i) * count;
                    }
                }
            }
        } else {
            if (itemList != null) {
                width += itemList.size() * (itemTextViewSet.itemWidth + itemTextViewSet.itemMarginStart + itemTextViewSet.itemMarginEnd);
            }
        }
        return width;
    }

    /**
     * 获取默认 项 text view 配置
     *
     * @return ItemTextViewSet
     */
    private ItemTextViewSet getDefaultTextViewSet() {
        return new ItemTextViewSet.Builder(mContext)
                .build();
    }

    private void setFullItemTextBackground() {
        if (itemList != null) {
            if (itemTextViewSet.textBackgroundDrawableList != null && itemTextViewSet.originDrawables != null) {
                int size = itemList.size();
                int dSize = itemTextViewSet.textBackgroundDrawableList.size();
                while (dSize < size) {
                    Drawable[] drawables = itemTextViewSet.originDrawables.clone();
                    for (int i = 0; i < drawables.length; i++) {
                        Drawable drawable = drawables[i];
                        drawables[i] = drawable.getConstantState().newDrawable();
                    }
                    itemTextViewSet.textBackgroundDrawableList.addAll(Arrays.asList(drawables));
                    dSize = itemTextViewSet.textBackgroundDrawableList.size();
                }
            }
        }
    }

    /**
     * 获取项 背景
     *
     * @param position 位
     * @return 背景
     */
    @Nullable
    private Drawable getItemTextBackground(int position) {
        if (itemTextViewSet.textBackgroundDrawableList != null) {
            int listSize = itemTextViewSet.textBackgroundDrawableList.size();
            if (listSize != 0) {
                int index = position % listSize;
                return itemTextViewSet.textBackgroundDrawableList.get(index);
            }
        }
        return null;
    }

    /**
     * 获取项 宽度
     *
     * @param position 位
     * @return 宽度 px
     */
    private int getItemTextWidth(int position) {
        if (itemTextViewSet.itemWidthList != null) {
            int listSize = itemTextViewSet.itemWidthList.size();
            if (listSize != 0) {
                int index = position % listSize;
                return itemTextViewSet.itemWidthList.get(index);
            }
        }
        return itemTextViewSet.itemWidth;

    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 项text view 配置
     */
    public static class ItemTextViewSet {

        private int itemWidth = 0;
        private int itemHeight = 0;
        private int itemPaddingStart = 0;
        private int itemPaddingTop = 0;
        private int itemPaddingEnd = 0;
        private int itemPaddingBottom = 0;
        private int itemMarginStart = 0;
        private int itemMarginTop = 0;
        private int itemMarginEnd = 0;
        private int itemMarginBottom = 0;
        private int itemTextSize = 0;
        private int itemTextColor = Color.BLACK;
        private int itemTextGravity = Gravity.CENTER;
        private List<Drawable> textBackgroundDrawableList;
        private List<Integer> itemWidthList;
        private Drawable[] originDrawables;

        public static class Builder {

            private final ItemTextViewSet textViewSet;
            private final Context mContext;

            public Builder(Context mContext) {
                textViewSet = new ItemTextViewSet();
                textViewSet.textBackgroundDrawableList = new ArrayList<>();
                textViewSet.itemWidthList = new LinkedList<>();
                this.mContext = mContext;
            }

            public Builder setTextBackground(Drawable... drawables) {
                textViewSet.originDrawables = drawables.clone();
                textViewSet.textBackgroundDrawableList.addAll(Arrays.asList(drawables));
                return this;
            }

            public Builder setTextBackgroundColor(@ColorInt int... colors) {
                List<Drawable> backgroundDrawableList = new ArrayList<>();
                for (int color : colors) {
                    Drawable drawable;
                    try {
                        drawable = ContextCompat.getDrawable(mContext, color);
                    } catch (Exception e) {
                        drawable = new ColorDrawable(color);
                    }
                    if (drawable != null) {
                        backgroundDrawableList.add(drawable);
                    }
                }
                textViewSet.textBackgroundDrawableList.addAll(backgroundDrawableList);
                return this;
            }

            public Builder setItemWidth(int widthPixel) {
                textViewSet.itemWidth = widthPixel;
                return this;
            }

            public Builder setItemWidth(Integer... widths) {
                textViewSet.itemWidthList.addAll(Arrays.asList(widths));
                return this;
            }

            public Builder setItemWidthDp(int... widthDps) {
                List<Integer> widthList = new ArrayList<>();
                for (int width : widthDps) {
                    int widthPx = dp2px(mContext, width);
                    widthList.add(widthPx);
                }
                textViewSet.itemWidthList.addAll(widthList);
                return this;
            }

            public Builder setTextColorRes(@ColorRes int colorRes) {
                textViewSet.itemTextColor = ContextCompat.getColor(mContext, colorRes);
                return this;
            }

            public Builder setTextColorInt(@ColorInt int colorInt) {
                textViewSet.itemTextColor = colorInt;
                return this;
            }


            public Builder setTextGravity(int gravity) {
                textViewSet.itemTextGravity = gravity;
                return this;
            }

            public Builder setTextPadding(int padding) {
                return setTextPadding(padding, padding, padding, padding);
            }

            public Builder setTextMargin(int margin) {
                return setTextMargin(margin, margin, margin, margin);
            }

            public Builder setTextMargin(int marginStart, int marginTop, int marginEnd, int marginBottom) {
                textViewSet.itemMarginStart = marginStart;
                textViewSet.itemMarginTop = marginTop;
                textViewSet.itemMarginEnd = marginEnd;
                textViewSet.itemMarginBottom = marginBottom;
                return this;
            }

            public Builder setTextPadding(int paddingStart, int paddingTop, int paddingEnd, int paddingBottom) {
                textViewSet.itemPaddingStart = paddingStart;
                textViewSet.itemPaddingTop = paddingTop;
                textViewSet.itemPaddingEnd = paddingEnd;
                textViewSet.itemPaddingBottom = paddingBottom;
                return this;
            }

            public ItemTextViewSet build() {
                if (textViewSet.itemWidth <= 0) {
                    textViewSet.itemWidth = dp2px(mContext, DEFAULT_ITEM_WIDTH_DP);
                }
                if (textViewSet.itemHeight <= 0) {
                    textViewSet.itemHeight = dp2px(mContext, DEFAULT_ITEM_HEIGHT_DP);
                }
                if (textViewSet.itemTextSize <= 0) {
                    textViewSet.itemTextSize = DEFAULT_ITEM_TEXT_SIZE;
                }

                // 释放 列表数据
                if (textViewSet.itemWidthList.isEmpty()) {
                    textViewSet.itemWidthList = null;
                }
                if (textViewSet.textBackgroundDrawableList.isEmpty()) {
                    textViewSet.textBackgroundDrawableList = null;
                }
                return textViewSet;
            }
        }


    }

    /**
     * 设置数据项
     *
     * @param itemList 数据项
     */
    public void setItemList(List<String> itemList) {
        this.itemList = itemList;
        setTextViewList();
        invalidate();
    }

    public void setItemTextViewSet(ItemTextViewSet itemTextViewSet) {
        this.itemTextViewSet = itemTextViewSet;
        setTextViewList();
        invalidate();
    }
}
