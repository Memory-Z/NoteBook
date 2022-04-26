package com.inz.z.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.alibaba.fastjson.JSONObject;
import com.inz.z.base.BuildConfig;
import com.inz.z.base.entity.UpdateVersionBean;
import com.inz.z.base.util.L;
import com.inz.z.base.util.SPHelper;
import com.inz.z.base.util.ThreadPoolUtils;
import com.inz.z.base.view.dialog.UpdateVersionDialog;
import com.qmuiteam.qmui.util.QMUINotchHelper;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/05/23 15:36.
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    /**
     * layoutId
     */
    public static final int VIEW_TYPE_ID = 0x00F0;
    /**
     * ViewBinding
     */
    public static final int VIEW_TYPE_VIEW_BINDING = 0x00F1;
    /**
     * DataBinding
     */
    public static final int VIEW_TYPE_DATA_BINDING = 0x00F2;
    /**
     * 是否显示线程日志。
     */
    public static final boolean SHOW_THREAD_LOG = BuildConfig.DEBUG;
    /**
     * ScheduledExecutorService
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * Executor  主线程。
     */
    private ThreadPoolUtils.MainThreadExecutor uiThread;
    /**
     * Executor 单线程
     */
    private Executor singleThread;
    /**
     * ThreadPoolExecutor 线程池
     */
    private ThreadPoolExecutor workThread;

    protected Context mContext;

    protected abstract void initWindow();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 是否需要重新设置 底部导航栏
     *
     * @return true 设置paddingBottom
     */
    protected abstract boolean resetBottomNavigationBar();

    /**
     * 是否使用 DataBinding , 返回true时，{@linkplain #setDataBindingView()} 必须复写
     *
     * @return 默认不使用
     */
    protected boolean useDataBinding() {
        return false;
    }

    /**
     * 是否使用 ViewBinding
     *
     * @return 是否使用
     * @see #setViewBinding()
     */
    protected boolean useViewBinding() {
        return false;
    }

    /**
     * 使用 ViewBinding
     */
    protected void setViewBinding() {

    }

    /**
     * 设置DataBindingView 内容
     */
    protected void setDataBindingView() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowDefault();
        initWindow();
        if (resetBottomNavigationBar()) {
            setNavigationBar();
        }
        // 判断是否是否 ViewBinding/DataBinding
        if (useViewBinding()) {
            setViewBinding();
        } else if (useDataBinding()) {
            setDataBindingView();
        } else {
            setContentView(getLayoutId());
        }
        mContext = this;
        View rootView = getRootContentView();
        if (rootView != null) {
            isNotchSupport(rootView);
        }
        onCreateTask();
        onCreateData();
    }

    protected void onCreateTask() {
        initView();
    }

    protected void onCreateData() {
        initData();
    }

    private void initWindowDefault() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 刘海屏支持/ 设置刘海屏不显示界面内容
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkVersion();
    }

    @Override
    protected final void onDestroy() {
        super.onDestroy();
        onDestroyTask();
        onDestroyData();
    }

    /**
     * 销毁任务
     */
    protected void onDestroyTask() {
        // 清除 线程。
        clearThreadPool();
        mContext = null;
    }

    /**
     * 销毁数据
     */
    protected void onDestroyData() {

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
                    params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
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
        L.i(this.getClass().getName(), "fitNotchView: ");
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
        return 0;
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

    protected void showLastVersionToast() {

    }


    /**
     * 获取最新版本
     */
    protected void getLastVersion() {
        Observable
                .create(new ObservableOnSubscribe<UpdateVersionBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<UpdateVersionBean> emitter) throws Exception {
                        String checkVersionUrl = SPHelper.getInstance().getUpdateVersionUrl();
                        if (TextUtils.isEmpty(checkVersionUrl)) {
//                            emitter.onError(new NullPointerException("check version url is empty. "));
                            return;
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
                        UpdateVersionBean bean = JSONObject.parseObject(sb.toString(), UpdateVersionBean.class);
                        if (emitter != null) {
                            if (bean != null) {
                                emitter.onNext(bean);
                            } else {
                                emitter.onError(new IllegalArgumentException("get version describe failure. "));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UpdateVersionBean>() {
                    @Override
                    public void onNext(@NotNull UpdateVersionBean versionBean) {
                        Log.i("baseActivity", "onNext: data" + versionBean);
                        int curCode = SPHelper.getInstance().getCurrentVersionCode();
                        Log.i("baseActivity", "checkVersion: versionCode = " + curCode);
                        int ignoreV = SPHelper.getInstance().ignoreVersionCode();
                        if (curCode < versionBean.getVersionCode() && versionBean.getVersionCode() != ignoreV) {
                            for (int ignoreVersion : versionBean.getIgnoreVersion()) {
                                if (ignoreVersion == curCode) {
                                    showLastVersionToast();
                                    return;
                                }
                            }
                            showVersionUpdateDialog(versionBean);
                        } else {
                            showLastVersionToast();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
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

    /* -------------------- 底部状态栏 ------------------------ */

    /**
     * 设置导航栏
     */
    protected void setNavigationBar() {
        boolean haveNavigation = checkNavigation(this);
        if (haveNavigation) {
            int navigationBarHeight = getNavigationBarHeight(this);

            ContentFrameLayout contentView = findViewById(android.R.id.content);
            if (contentView != null) {
                contentView.setPadding(
                        contentView.getPaddingLeft(),
                        contentView.getPaddingTop(),
                        contentView.getPaddingTop(),
                        contentView.getPaddingBottom() + navigationBarHeight
                );
            }
        }
    }

    /**
     * 检测是否存在底部栏
     */
    private boolean checkNavigation(Context context) {
        boolean haveNavigationBar = false;
        Resources resources = context.getResources();
        int barId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (barId > 0) {
            haveNavigationBar = resources.getBoolean(barId);
        }
        try {
            @SuppressLint("PrivateApi")
            Class<?> className = Class.forName("android.os.SystemProperties");
            Method method = className.getMethod("get", String.class);
            String navBarOverride = (String) method.invoke(className, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                // 不存在 虚拟按键
                haveNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                // 存在 虚拟按键
                haveNavigationBar = true;
//                val navigationBarHeight = getNavigationBarHeight(this)
//                L.i(TAG, "checkNavigate:  $navigationBarHeight")
            }
        } catch (Exception ignore) {
        }
        boolean isShowNav = isShowNavigationBar();
        L.d(getClass().getName(), "checkNavigation: isShowNav = " + isShowNav + " , haveNav = " + haveNavigationBar);
        return haveNavigationBar && isShowNav;
    }

    /**
     * 获取底部导航栏高度
     */
    protected int getNavigationBarHeight(Activity activity) {
        int resId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取 状态栏高度
     *
     * @param activity Activity
     * @return height
     */
    protected int getStatusBarHeight(Activity activity) {
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取屏幕实际 Size
     *
     * @return Rect
     */
    protected Rect getScreenSize() {
        Rect rect = new Rect();
        WindowManager manager = getWindowManager();
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                rect = manager.getCurrentWindowMetrics().getBounds();
            } else {
                manager.getDefaultDisplay().getRectSize(rect);
            }
        }
        return rect;
    }

    /**
     * 是否显示 底部导航栏
     *
     * @return 是否显示
     */
    protected boolean isShowNavigationBar() {
        Rect windowRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(windowRect);
        int activityHeight = windowRect.height();
        int remainHeight = getScreenSize().height() - getNavigationBarHeight(this) - getStatusBarHeight(this);
        return activityHeight == remainHeight;
    }
    /* -------------------- 底部状态栏 ------------------------ */

    /* - ---------------------------------- 获取线程池 ----------------------------- - */

    @Nullable
    protected ScheduledExecutorService getScheduleThread(@Nullable String tag) {
        if (isDestroyed()) {
            return null;
        }
        if (scheduledExecutorService == null) {
            if (tag == null) {
                tag = getPackageName();
            }
            scheduledExecutorService = ThreadPoolUtils.INSTANCE.getScheduleThread(tag);
        }
        if (SHOW_THREAD_LOG) {
            L.d("BASE_THREAD", "getScheduleThread: tag = " + tag);
        }
        return scheduledExecutorService;
    }

    @Nullable
    protected ThreadPoolUtils.MainThreadExecutor getUiThread(@Nullable String tag) {
        if (isDestroyed()) {
            return null;
        }
        if (uiThread == null) {
            if (tag == null) {
                tag = getPackageName();
                uiThread = ThreadPoolUtils.INSTANCE.getUiThread(tag);
            }
        }
        if (SHOW_THREAD_LOG) {
            L.d("BASE_THREAD", "getUiThread: TAG = " + tag);
        }
        return uiThread;
    }

    @Nullable
    protected Executor getSingleThread(@Nullable String tag) {
        if (isDestroyed()) {
            return null;
        }
        if (singleThread == null) {
            if (tag == null) {
                tag = getPackageName();
            }
            singleThread = ThreadPoolUtils.INSTANCE.getSingleThread(tag);
        }
        if (SHOW_THREAD_LOG) {
            L.d("BASE_THREAD", "getSingleThread: TAG = " + tag);
        }
        return singleThread;
    }

    @Nullable
    protected ThreadPoolExecutor getWorkThread(@Nullable String tag) {
        if (isDestroyed()) {
            return null;
        }
        if (workThread == null) {
            if (tag == null) {
                tag = getPackageName();
            }
            workThread = ThreadPoolUtils.INSTANCE.getWorkThread(tag);
        }
        if (SHOW_THREAD_LOG) {
            L.d("BASE_THREAD", "getWorkThread: TAG = " + tag);
        }
        return workThread;
    }

    /**
     * 清除 线程 工具。
     */
    protected void clearThreadPool() {
        if (SHOW_THREAD_LOG) {
            L.d("BASE_THREAD", "clearThreadPool: CLEAR_THREAD_POOL  start . ");
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService = null;
        }
        if (uiThread != null) {
            uiThread = null;
        }
        if (singleThread != null) {
            singleThread = null;
        }
        if (workThread != null) {
            workThread = null;
        }
        if (SHOW_THREAD_LOG) {
            L.d("BASE_THREAD", "clearThreadPool: CLEAR_THREAD_POOL  end . ");
        }
    }

    /* - ---------------------------------- 获取线程池 ----------------------------- - */


}
