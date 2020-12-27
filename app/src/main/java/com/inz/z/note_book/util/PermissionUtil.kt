package com.inz.z.note_book.util

import android.Manifest
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
}