package com.inz.z.addressbook.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.z.addressbook.BuildConfig;
import com.inz.z.addressbook.bean.AddressBookPinyinBean;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    /**
     * 导航栏
     */
    private LinkedList<String> navList = new LinkedList<>();
    /**
     * 有效的 导航栏
     */
    private LinkedList<String> userNavList = new LinkedList<>();
    /**
     * 关联数据
     */
    private List<? extends AddressBookPinyinBean> dataList;

    private LinkedList<NavItemView> navItemViewLinkedList = new LinkedList<>();
    private LinearLayoutManager layoutManager;

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
            NavItemViewBean bean = new NavItemViewBean();
            bean.setTag(nav);
            bean.setCanClick(true);
            itemView.setNavItemViewBean(bean);
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
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                onTouchTime = time;
                int position = (int) ((y - startY) / itemHeight);
                onCheckedItem(position);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                onTouchTime = 0L;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int position = (int) ((y - startY) / itemHeight);
                onCheckedItem(position);
                break;
            }
            case MotionEvent.ACTION_UP: {
                clearCheckedItem();
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
        if (onTouchItem != position) {
            if (onTouchItem >= 0 && onTouchItem < navItemViewLinkedList.size()) {
                NavItemView itemView = navItemViewLinkedList.get(onTouchItem);
                itemView.setTextColor(ITEM_BASE_TEXT_COLOR);
            }
            onTouchItem = position;
        }
        String tag = navList.get(position);
        if (listener != null) {
            listener.onTouchItem(tag, position);
        }
        if (navItemViewLinkedList.size() == navList.size()) {
            NavItemView itemView = navItemViewLinkedList.get(position);
            if (itemView.getNavItemViewBean().isCanClick()) {
                itemView.setTextColor(Color.RED);
            }
        }
        if (layoutManager != null) {
            int dataListPosition = getPositionByTag(tag);
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onCheckedItem: TAG = " + tag + " , position = " + dataListPosition);
            }
            if (dataListPosition != -1) {
                layoutManager.scrollToPositionWithOffset(dataListPosition, 0);
            }
        }
    }

    /**
     * 清除点击项
     */
    private void clearCheckedItem() {
        if (onTouchItem != -1) {
            if (onTouchItem >= 0 && onTouchItem < navItemViewLinkedList.size()) {
                NavItemView itemView = navItemViewLinkedList.get(onTouchItem);
                itemView.setTextColor(ITEM_BASE_TEXT_COLOR);
            }
        }
    }

    /**
     * 通过标签获取 数据中 序号
     *
     * @param tag 标签
     * @return 序号
     */
    private int getPositionByTag(@NonNull String tag) {
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                AddressBookPinyinBean bean = dataList.get(i);
                if (bean != null && tag.equals(bean.getPinyinFirstChar())) {
                    return i;
                }
            }
        }
        return -1;
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

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public LinkedList<String> getUserNavList() {
        return userNavList;
    }

    public void setUserNavList(LinkedList<String> userNavList) {
        for (NavItemView navItemView : navItemViewLinkedList) {
            boolean canClick = false;
            if (navItemView != null) {
                NavItemViewBean bean = navItemView.getNavItemViewBean();
                String oldNav = bean.getTag();
                for (String nav : userNavList) {
                    if (nav.equals(oldNav)) {
                        canClick = true;
                        break;
                    }
                }
                bean.setCanClick(canClick);
                navItemView.setNavItemViewBean(bean);
            }
        }
        this.userNavList = userNavList;
    }

    public List<? extends AddressBookPinyinBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<? extends AddressBookPinyinBean> dataList) {
        this.dataList = dataList;
        invalidate();
    }
}
