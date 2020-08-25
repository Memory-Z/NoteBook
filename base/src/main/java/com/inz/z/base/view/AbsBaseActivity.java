package com.inz.z.base.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSONObject;
import com.inz.z.base.BuildConfig;
import com.inz.z.base.entity.UpdateVersionBean;
import com.inz.z.base.util.SPHelper;
import com.inz.z.base.view.dialog.UpdateVersionDialog;
import com.qmuiteam.qmui.util.QMUINotchHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

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
    protected void onStart() {
        super.onStart();
        checkVersion();
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

    /* >>>>>>>>>>>>>>>>>>>>>>>>>>>>> 版本更新 <<<<<<<<<<<<<<<<<<<<<<<<<<<<< */

    /**
     * 是否需要检测版本更新
     *
     * @return 是否需要检测
     */
    protected boolean needCheckVersion() {
        return true;
    }

    /**
     * 获取当前版本号
     *
     * @return 当前版本号
     */
    protected int getCurrentVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * 版本检测更新
     */
    private void checkVersion() {
        boolean isLater = SPHelper.getInstance().isLaterUpdateVersion();
        if (!needCheckVersion() || isLater) {
            return;
        }
        getLastVersion();
    }


    /**
     * 获取最新版本
     */
    private void getLastVersion() {
        Observable
                .create(new ObservableOnSubscribe<UpdateVersionBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<UpdateVersionBean> emitter) throws Exception {
                        String checkVersionUrl = SPHelper.getInstance().getUpdateVersionUrl();
                        if (TextUtils.isEmpty(checkVersionUrl)) {
                            emitter.onError(new NullPointerException("check version url is empty. "));
                        }
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {
                            URL url = new URL(checkVersionUrl);
                            URLConnection connection = url.openConnection();
                            connection.connect();
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String lines;
                            byte[] bytes = new byte[1024];
                            while ((lines = reader.readLine()) != null) {
                                sb.append(lines).append("\n");
                            }
                            reader.close();
                        } catch (IOException e) {
                            emitter.onError(e);
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    emitter.onError(e);
                                }
                            }
                        }
                        emitter.onNext(JSONObject.parseObject(sb.toString(), UpdateVersionBean.class));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UpdateVersionBean>() {
                    @Override
                    public void onNext(UpdateVersionBean versionBean) {
                        Log.i("baseActivity", "onNext: data" + versionBean);
                        if (versionBean != null) {
                            int curCode = SPHelper.getInstance().getCurrentVersionCode();
                            Log.i("baseActivity", "checkVersion: versionCode = " + curCode);
                            int ignoreV = SPHelper.getInstance().ignoreVersionCode();
                            if (curCode < versionBean.getVersionCode() && versionBean.getVersionCode() != ignoreV) {
                                for (int ignoreVersion : versionBean.getIgnoreVersion()) {
                                    if (ignoreVersion == curCode) {
                                        return;
                                    }
                                }
                                showVersionUpdateDialog(versionBean);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("baseActivity", "getLastVersion: onError: ", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 显示新版提示弹窗
     *
     * @param versionBean 版本信息
     */
    private void showVersionUpdateDialog(UpdateVersionBean versionBean) {
        if (mContext != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            new UpdateVersionDialog
                    .Builder(fragmentManager)
                    .setUpdateVersion(versionBean)
                    .show();
        }

    }

    /* >>>>>>>>>>>>>>>>>>>>>>>>>>>>> 版本更新 <<<<<<<<<<<<<<<<<<<<<<<<<<<<< */
}
