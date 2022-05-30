package com.inz.z.note_book.util

import android.app.PendingIntent
import android.os.Build
import com.inz.z.base.util.BaseTools

/**
 * 基础工具类
 *
 * ====================================================
 * Create by 11654 in 2022/5/2 15:01
 */
object BaseUtil : BaseTools() {


    // +bug, 11654, 2022/5/2 , modify , update pendingIntent FLAG .
    /**
     * 获取 PendingIntent FLAG
     */
    fun getPendingIntentFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_CANCEL_CURRENT
    // -bug, 11654, 2022/5/2 , modify , update pendingIntent FLAG .

    fun getMutablePendingIntentFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

}