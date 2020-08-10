package com.inz.z.base.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.util.QMUINotchHelper;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/05/23 15:36.
 */
public abstract class AbsBaseActivity extends AppCompatActivity {
    protected Context mContext;

    protected abstract void initWindow();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 是否使用 DataBinding , 返回true时，{@linkplain #setDataBindingView()} 必须复写
     *
     * @return 默认不使用
     */
    protected boolean useDataBinding() {
        return false;
    }

    /**
     * 设置DataBindingView 内容
     */
    protected void setDataBindingView() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initWindowDefault();
        initWindow();
        if (!useDataBinding()) {
            setContentView(getLayoutId());
        } else {
            setDataBindingView();
        }
        mContext = this;
        View rootView = getRootContentView();
        if (rootView != null) {
            isNotchSupport(rootView);
        }
        initView();
        initData();
    }

    private void initWindowDefault() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    /* ======================= 判断是否为刘海屏 ======================= */


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View rootView = getRootContentView();
        if (rootView != null) {
            isNotchSupport(rootView);
        }
    }

    /**
     * 获取根目录容器 布局
     *
     * @return View
     */
    @Nullable
    protected View getRootContentView() {
        return null;
    }

    /**
     * 判断是否支持全面屏
     *
     * @param rootView 根 View
     */
    protected void isNotchSupport(View rootView) {
        if (QMUINotchHelper.hasNotch(this)) {
            if (QMUINotchHelper.isNotchOfficialSupport()) {
                // 刘海屏支持/ 设置刘海屏不显示界面内容
                WindowManager.LayoutParams params = getWindow().getAttributes();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                }
                getWindow().setAttributes(params);

            } else {
                // 如果 android 版本 < 28 通过系统方式获取
                fitNotchView(rootView);
            }
        }
    }

    // RECOVERED: 2019/09/03 11:05  by inz --> 修复刘海屏手机在 9.0 一下不支持情况

    /**
     * 适配刘海屏布局
     */
    private void fitNotchView(View rootView) {
        View mView = getWindow().getDecorView();
        int left = QMUINotchHelper.getSafeInsetLeft(mView);
        int top = QMUINotchHelper.getSafeInsetTop(mView);
        int right = QMUINotchHelper.getSafeInsetRight(mView);
        int bottom = QMUINotchHelper.getSafeInsetBottom(mView);
        mView.setBackgroundColor(Color.BLACK);
        if (rootView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rootView.getLayoutParams();
            if (lp != null) {
                lp.setMarginStart(lp.leftMargin + left);
                rootView.setLayoutParams(lp);
            }
        }
    }
    /* ======================= 判断是否为刘海屏 ======================= */
}
