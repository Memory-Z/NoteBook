package com.inz.z.note_book.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 *
 * 权限检查工具
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 13:38.
 */
object PermissionUtil {

    /**
     * 检查是否拥有存储权限
     */
    fun checkReadStoragePermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    /**
     * 权限检测
     * @param context 上下文
     * @param permission 权限名称
     * @see ActivityCompat.checkSelfPermission
     */
    fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 获取未 获取权限列表
     * @param context 上下文
     * @param permissions 权限列表
     */
    fun filterNoPermission(context: Context, permissions: Array<String>): Array<String> {
        val noPermissionArray = arrayListOf<String>()
        for (permission in permissions) {
            val granted = checkPermission(context, permission)
            if (!granted) {
                noPermissionArray.add(permission)
            }
        }
        return noPermissionArray.toArray(arrayOf())
    }

    /**
     * 权限请求
     * @param activity Activity
     * @param permissions 需要的权限
     * @param requestCode 请求吗
     */
    fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
        val noPermissions = filterNoPermission(activity, permissions)
        ActivityCompat.requestPermissions(activity, noPermissions, requestCode)
    }
}