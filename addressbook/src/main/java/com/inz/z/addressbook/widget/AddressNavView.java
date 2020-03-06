package com.inz.z.addressbook.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * 通讯录导航栏
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/3/4 12:50.
 */
public class AddressNavView extends LinearLayout {
    private static final String TAG = "AddressNavView";
    private static final String[] BASE_NAV_ARRAY = {
            "*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };
    private static final int ITEM_BASE_TEXT_COLOR = Color.parseColor("#2D2D2D");
    private static final int ITEM_CHECKED_TEXT_COLOR = Color.parseColor("#FF6BFF7F");

    private Context mContext;
    private LinkedList<String> navList = new LinkedList<>();
    private LinkedList<NavItemView> navItemViewLinkedList = new LinkedList<>();
    private RecyclerView.LayoutManager layoutManager;

    public AddressNavView(Context context) {
        super(context);
        this.mContext = context;
        initView();
        initData();
    }

    public AddressNavView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddressNavView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initData();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        navList.clear();
        navList.addAll(Arrays.asList(BASE_NAV_ARRAY));
        resetViewList();


    }

    private void resetViewList() {
        navItemViewLinkedList.clear();
        for (String nav : navList) {
            NavItemView itemView = new NavItemView(mContext);
            itemView.setText(nav);
            navItemViewLinkedList.add(itemView);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
    }

    private int startY = 0;
    private int itemHeight = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = b - t - getPaddingTop() - getPaddingBottom();
        int width = r - l - getPaddingStart() - getPaddingEnd();
        itemHeight = height / navItemViewLinkedList.size();
        startY = (height - itemHeight * navItemViewLinkedList.size()) / 2 + getPaddingTop();
        int startX = getPaddingStart();
        for (int i = 0; i < navItemViewLinkedList.size(); i++) {
            NavItemView itemView = navItemViewLinkedList.get(i);
            int top = startY + itemHeight * i;

            itemView.layout(startX, top, startX + width, top + itemHeight);
            addView(itemView);
        }

    }

    /**
     * 按下时间
     */
    private long onTouchTime = 0L;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        long time = System.currentTimeMillis();
        Log.i(TAG, "onTouchEvent: action - " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                onTouchTime = time;
                int position = (int) ((y - startY) / itemHeight);
                Log.i(TAG, "onTouchEvent: ACTION_DOWN position = " + position);
                onCheckedItem(position);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                onTouchTime = 0L;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int position = (int) ((y - startY) / itemHeight);
                Log.i(TAG, "onTouchEvent: ACTION_MOVE position = " + position);
                onCheckedItem(position);

                break;
            }
            case MotionEvent.ACTION_UP: {
                if (time - onTouchTime < 500) {
                    // 按下弹起时间小于 500ms 认为是 点击事件
                    performClick();
                    break;
                }
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int onTouchItem = -1;

    /**
     * 设置选中的项
     *
     * @param position 位
     */
    private void onCheckedItem(int position) {
        if (position < 0) {
            position = 0;
        }
        if (position >= navList.size()) {
            position = navList.size() - 1;
        }
        Log.i(TAG, "onCheckedItem: position = " + position);
        if (onTouchItem != position) {
            if (onTouchItem >= 0 && onTouchItem < navItemViewLinkedList.size()) {
                NavItemView itemView = navItemViewLinkedList.get(onTouchItem);
                itemView.setTextColor(ITEM_BASE_TEXT_COLOR);
            }
            onTouchItem = position;
        }
        if (listener != null) {
            listener.onTouchItem(navList.get(position), position);
        }
        if (navItemViewLinkedList.size() == navList.size()) {
            NavItemView itemView = navItemViewLinkedList.get(position);
            itemView.setTextColor(Color.RED);
        }
        if (layoutManager != null) {
            layoutManager.scrollToPosition(position);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外接口
    ///////////////////////////////////////////////////////////////////////////


    public interface AddressNavViewListener {
        void onTouchItem(String nav, int position);
    }

    private AddressNavViewListener listener;

    public void setListener(AddressNavViewListener listener) {
        this.listener = listener;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
}
