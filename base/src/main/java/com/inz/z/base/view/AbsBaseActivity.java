package com.inz.z.base.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
}
