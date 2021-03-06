package com.inz.z.base.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 运行帮助类
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/25 15:42.
 */
public class LauncherHelper {

    /**
     * 启动其他软件
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void launcherPackageName(@NonNull Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (checkPackageName(packageManager, packageName)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } else {
            throw new RuntimeException(" not find this package: " + packageName + " . ");
        }
    }

    public static PackageInfo findApplicationInfoByPackageName(@NonNull Context context, String packageName) {
        PackageManager manager = context.getPackageManager();

        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 检测包名是否存在
     *
     * @param manager     管理器
     * @param packageName 包名
     * @return 是否存在
     */
    private static boolean checkPackageName(PackageManager manager, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 获取安装程序列表
     *
     * @param context 上下文
     * @return 应用列表
     */
    public static List<PackageInfo> getApplicationList(@NonNull Context context) {
        List<PackageInfo> packageInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo info : packageInfoList) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                packageInfos.add(info);
            }
        }
        return packageInfos;
    }


    /**
     * 获取缓存文件下载地址
     *
     * @param context 上下文
     * @return 地址
     */
    @NonNull
    public static String getCacheDownloadFilePath(Context context) {
        String path = FileUtils.getCacheFilePath(context) + File.separatorChar + "download";
        File file = new File(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取缓存 更新 文件 下载地址
     *
     * @param context 上下文
     * @return 更新文件地址
     */
    @NonNull
    public static String getCacheDownloadVersionPath(Context context) {
        String path = getCacheDownloadFilePath(context) + File.separatorChar + "version";
        File file = new File(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 文件安装
     *
     * @param context       上下文
     * @param applicationId 应用ID  BuildConfig.getApplicationId();
     * @param file          文件
     */
    @Nullable
    public static Intent installApk(Context context, String applicationId, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                String authority = applicationId + ".baseFileProvider";
                uri = FileProvider.getUriForFile(context, authority, file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

}
