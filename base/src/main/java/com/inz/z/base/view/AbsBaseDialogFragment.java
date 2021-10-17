package com.inz.z.base.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/05/23 15:53.
 */
public abstract class AbsBaseDialogFragment extends DialogFragment {
    protected Context mContext;
    protected View mView;

    protected abstract void initWindow();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 是否使用 ViewBinding
     *
     * @return 是否使用 , 默认不使用
     */
    protected boolean useViewBinding() {
        return false;
    }

    /**
     * 设置 ViewBinding
     *
     * @return 获取 ViewBinding.root
     */
    @Nullable
    protected View getViewBindingView() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 是否使用 ViewBinding
        if (useViewBinding()) {
            return getViewBindingView();
        }
        return inflater.inflate(getLayoutId(), null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mContext = getContext();
        initView();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mView = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }


    /**
     * 显示提示
     *
     * @param message 提示内容
     */
    protected void showToast(String message) {
        if (mContext != null) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示提示
     *
     * @param resId 资源ID
     */
    protected void showToast(@StringRes int resId) {
        if (mContext != null) {
            String message = mContext.getString(resId);
            showToast(message);
        }
    }
}
