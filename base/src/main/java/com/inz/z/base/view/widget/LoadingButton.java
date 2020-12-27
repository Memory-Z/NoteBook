package com.inz.z.base.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.TintTypedArray;

import com.inz.z.base.R;

/**
 * Loading Button
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/08 15:59.
 */
public class LoadingButton extends AppCompatButton {
    private static final String TAG = "LoadingButton";

    private Context mContext;
    private View mView;
    private ImageView iconIv;
    private TextView contentTv;

    public LoadingButton(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public LoadingButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (attrs != null) {
            initAttr(attrs);
        }
        initView();
    }

    @SuppressLint("RestrictedApi")
    private void initAttr(@NonNull AttributeSet attr) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attr, R.styleable.LoadingButton, 0, 0);

        array.recycle();
    }

    private void initView() {
//        if (mView == null) {
//            mView = LayoutInflater.from(mContext).inflate(R.layout.base_loading_button, null, true);
//
//
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }
}
