package com.inz.z.note_book.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 基础 生命周期 监听 。
 *
 * ====================================================
 * Create by 11654 in 2021/11/9 21:26
 */
interface BaseLifecycleObserver : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event)
}
