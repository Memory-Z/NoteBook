package com.inz.z.base.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.inz.z.base.R;
import com.inz.z.base.util.ImageUtils;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/06/12 16:59.
 */
public class BaseTopActionLayout extends LinearLayout {
    private static final String TAG = "BaseTopActionLayout";
    private Context mContext;
    private View mView;
    private RelativeLayout leftRl, rightRl, centerRl;
    private ConstraintLayout centerCl;
    private Toolbar toolbar;

    //    /**
//     * 背景
//     */
//    private LinearLayout backgroundLl;
    private AppBarLayout appBarLayout;

    private View userLeftView, userCenterView, userRightView;
    private int userLeftLayoutId, userCenterLayoutId, userRightLayoutId;
    private String titleStr;
    private int statusBarHeight = 0;
    /**
     * 显示自定义布局
     */
    private boolean showCustomView = false;
    /**
     * 状态栏显示背景图
     */
    private boolean showBackgroundWithStatusBar = false;


    public enum TitleGravity {
        START(Gravity.START),
        CENTER(Gravity.CENTER),
        END(Gravity.END);

        int value;

        TitleGravity(int value) {
            this.value = value;
        }
    }

    public BaseTopActionLayout(Context context) {
        this(context, null);
    }

    public BaseTopActionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initStyle(attrs);
        initView();
    }

//    public BaseTopActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mContext = context;
//        initView();
//        initStyle(attrs);
//    }

    @SuppressLint("InflateParams")
    private void initView() {
        initStatusBarHeight();
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.base_top_action_layout, this, true);
            leftRl = mView.findViewById(R.id.base_top_action_left_rl);
            centerRl = mView.findViewById(R.id.base_top_action_center_rl);
            rightRl = mView.findViewById(R.id.base_top_action_right_rl);
            centerCl = mView.findViewById(R.id.base_top_action_center_cl);
            centerCl.setVisibility(showCustomView ? VISIBLE : GONE);
            toolbar = mView.findViewById(R.id.base_top_action_toolbar);
//            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            addView(mView, layoutParams);
//            backgroundLl = mView.findViewById(R.id.base_top_action_background_ll);
            appBarLayout = mView.findViewById(R.id.base_top_action_root_abl);
//            targetBackgroundShow(showBackgroundWithStatusBar);
        }
    }

    /**
     * 初始化状态栏高度
     */
    private void initStatusBarHeight() {
        int resourcesId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourcesId > 0) {
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourcesId);
        }
    }

    @SuppressLint("RestrictedApi")
    private void initStyle(AttributeSet attrs) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.BaseTopActionLayout, 0, 0);
        userLeftLayoutId = array.getResourceId(R.styleable.BaseTopActionLayout_base_top_left_layout, R.id.base_top_action_left_rl);
        userCenterLayoutId = array.getResourceId(R.styleable.BaseTopActionLayout_base_top_center_layout, R.id.base_top_action_center_rl);
        userRightLayoutId = array.getResourceId(R.styleable.BaseTopActionLayout_base_top_right_layout, R.id.base_top_action_right_rl);
        titleStr = array.getString(R.styleable.BaseTopActionLayout_base_top_title);
        showCustomView = array.getBoolean(R.styleable.BaseTopActionLayout_base_show_custom_view, false);
        showBackgroundWithStatusBar = array.getBoolean(R.styleable.BaseTopActionLayout_base_show_background_in_status, false);
        array.recycle();
    }


//    @Override
//    public void setBackgroundColor(int color) {
//        if (showBackgroundWithStatusBar) {
////            backgroundLl.setBackgroundColor(color);
//            super.setBackgroundColor(Color.TRANSPARENT);
//        } else {
//            super.setBackgroundColor(color);
//        }
//    }
//
//    @Override
//    public void setBackgroundResource(int resid) {
//        if (showBackgroundWithStatusBar) {
////            backgroundLl.setBackgroundResource(resid);
//            super.setBackgroundResource(android.R.color.transparent);
//        } else {
//            super.setBackgroundResource(resid);
//        }
//    }
//
//    @Override
//    public void setBackground(Drawable background) {
//        if (showBackgroundWithStatusBar) {
////            backgroundLl.setBackground(background);
//            background.setAlpha(0);
//        }
//        super.setBackground(background);
//    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTopActionTitle(String title) {
        titleStr = title;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initializeView();
//        setTopStatusHeight(statusBarHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        setTopStatusHeight(statusBarHeight);

    }

    /**
     * 初始化自定义 View
     */
    private void initializeView() {
        setUserLeftView();
        setUserCenterView();
        setUserRightView();
        setTitleTv();
        offsetLeftAndRight(48);
    }

    /**
     * 设置左侧自定义View
     */
    private void setUserLeftView() {
        if (userLeftLayoutId != R.id.base_top_action_left_rl) {
            View view = findViewById(userLeftLayoutId);
            if (view != null) {
                removeView(view);
                userLeftView = view;
                leftRl.addView(userLeftView);
            }
        }
    }

    /**
     * 设置中间自定义View
     */
    private void setUserCenterView() {
        if (userCenterLayoutId != R.id.base_top_action_center_rl) {
            View view = findViewById(userCenterLayoutId);
            if (view != null) {
                removeView(view);
                userCenterView = view;
                centerRl.addView(userCenterView);
            }
        }
    }

    /**
     * 设置右侧自定义View
     */
    private void setUserRightView() {
        if (userRightLayoutId != R.id.base_top_action_right_rl) {
            View view = findViewById(userRightLayoutId);
            if (view != null) {
                removeView(view);
                userRightView = view;
                rightRl.addView(userRightView);
            }
        }
    }

    /**
     * 设置标题
     */
    private void setTitleTv() {
        if (toolbar != null) {
            toolbar.setTitle(titleStr);
        }
    }

    /**
     * 设置顶部状态栏高
     *
     * @param statusHeight 状态栏高度
     */
    private void setTopStatusHeight(int statusHeight) {
        setPaddingRelative(getPaddingStart(), statusHeight, getPaddingEnd(), getPaddingBottom());
        if (toolbar != null) {
            Bitmap bitmap = getToolbarBackground(toolbar, statusHeight);
            if (bitmap != null) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                toolbar.setBackground(drawable);
            } else {
                toolbar.setBackground(getBackground());
            }
        }
    }

    /**
     * 切换顶部背景是否显示
     *
     * @param show 是否显示
     */
    private void targetBackgroundShow(Boolean show) {
//        if (appBarLayout != null) {
//            appBarLayout.setFitsSystemWindows(!show);
//        }
//        if (backgroundLl != null) {
//            backgroundLl.setVisibility(show ? VISIBLE : GONE);
//        }
        if (toolbar != null) {
//            toolbar.setFitsSystemWindows(!show);
            ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
            int height = layoutParams.height;
            int paddingTop = toolbar.getPaddingTop();
            if (show) {
                height += statusBarHeight;
                paddingTop += statusBarHeight;
            } else {
//                height -= statusBarHeight;
//                paddingTop -= statusBarHeight;
            }
            layoutParams.height = height;
//            toolbar.setPadding(
//                    toolbar.getPaddingLeft(),
//                    paddingTop,
//                    toolbar.getPaddingRight(),
//                    toolbar.getPaddingBottom()
//            );
        }
    }


    /**
     * 获取 Toolbar 背景
     *
     * @return Bitmap
     */
    private Bitmap getToolbarBackground(Toolbar toolbar, int statusBarHeight) {
        Drawable backgroundDrawable = getBackground();
        Bitmap backBitmap = ImageUtils.getBitmapFromDrawable(backgroundDrawable);
        if (backBitmap != null) {
            int bW = backBitmap.getWidth();
            int bH = backBitmap.getHeight();
            int lW = getMeasuredWidth();
            int lH = getMeasuredHeight();
            int tH = toolbar.getMeasuredHeight();
            float needH = (float) bH / lH * tH;
            float startY = (float) bH / lH * statusBarHeight;
            if (needH > bH - startY ) {
                needH = bH - startY;
            }
            return Bitmap.createBitmap(backBitmap, 0, (int) startY, bW, (int) needH);
        }
        return null;
    }

    public void setStatusBarHeight(int statusBarHeight) {
        this.statusBarHeight = statusBarHeight;
    }

}
