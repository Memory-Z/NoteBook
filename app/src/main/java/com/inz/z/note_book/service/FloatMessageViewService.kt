package com.inz.z.note_book.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.*
import com.inz.z.base.util.L
import com.inz.z.note_book.view.widget.FloatMessageBaseView
import com.inz.z.note_book.view.widget.FloatMessageMinView

/**
 * 浮动消息 服务
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/18 09:47.
 */
class FloatMessageViewService : Service() {

    companion object {
        private const val TAG = "FloatMessageViewService"
        private val realSize = Point()
        private val realMetrics = DisplayMetrics()

        /**
         * 更新 View
         */
        private fun updateRemoteView(
            v: View,
            mContext: Context,
            mParams: WindowManager.LayoutParams
        ) {
            val windowManager =
                mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            windowManager?.updateViewLayout(v, mParams)
        }
    }

    private var windowDisplay: Display? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initWindowDinesty()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        targetFloatView(true)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initWindowDinesty() {
        val windowManager =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.apply {
            windowDisplay = this.defaultDisplay
            this.defaultDisplay.getRealSize(realSize)
            this.defaultDisplay.getMetrics(realMetrics)

        }
    }

    /**
     * 获取参数
     */
    private fun getWindownLayoutParams(): WindowManager.LayoutParams {
        val windowLayoutParams = WindowManager.LayoutParams()
        windowLayoutParams.apply {
            this.flags = WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                .or(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                .or(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
                .or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                .or(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                this.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                this.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP.or(Gravity.START)
            x = realMetrics.widthPixels
            y = realMetrics.heightPixels / 4 * 3
            this.format = PixelFormat.RGBA_8888
        }
        return windowLayoutParams
    }

    /**
     * 添加 悬浮View
     */
    private fun addRemoteView(v: View, params: WindowManager.LayoutParams) {
        L.i(TAG, "addRemoteView: $v")
        val windowManager =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.apply {
            this.addView(v, params)
        }
    }

    /**
     * 移除 悬浮 View
     */
    private fun removeRemoteView(v: View) {
        L.i(TAG, "removeRemoteView: $v")
        val windowManager =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.apply {
            this.removeViewImmediate(v)
        }
    }

    private class OnTouchListenerImpl(
        var mContext: Context,
        var params: WindowManager.LayoutParams,
        var mView: View
    ) :
        View.OnTouchListener {
        private var gestureDetector: GestureDetector? = null

        companion object {
            private var firstTouchX = 0F;
            private var paramsX = 0
            private var viewWidth = 0
            private var viewHeight = 0
        }

        init {
            viewWidth = mView.width
            viewHeight = mView.height
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (gestureDetector == null) {
                gestureDetector =
                    GestureDetector(mContext, GestureListenerImpl(mContext, params, mView))
            }
            if (event != null) {
                val x = event.rawX
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        firstTouchX = x
                        val usableWidth = realMetrics.widthPixels - mView.width
                        if (viewWidth >= realMetrics.widthPixels) {
                            paramsX = 0
                        } else {
                            paramsX = params.x
                        }
                        paramsX = Math.min(paramsX, usableWidth)
                    }
                    MotionEvent.ACTION_UP -> {
                        val midWidth = realMetrics.widthPixels / 2
                        var dx = (x - firstTouchX).toInt()
                        L.i(TAG, "onTouch: ---- $dx ")
                        if (viewWidth >= realMetrics.widthPixels) {
                            dx = 0
                        }
                        val usableWidth = realMetrics.widthPixels - mView.width
                        // center  dot x
                        var finishX = paramsX + dx
                        L.i(TAG, "onTouch: >>>> $usableWidth - ${finishX} _-_ $dx")
                        finishX = Math.max(0, finishX)
                        finishX = Math.min(finishX, usableWidth)
                        if (finishX > midWidth) {
                            params.x = usableWidth
                        } else {
                            params.x = 0
                        }
                        updateRemoteView(mView, mContext, params)
                    }
                }
            }
            return gestureDetector!!.onTouchEvent(event)
        }
    }

    private class GestureListenerImpl(
        var mContext: Context,
        private var mParams: WindowManager.LayoutParams,
        var mView: View
    ) :
        GestureDetector.OnGestureListener {

        companion object {
            private var lastX = 0
            private var lastY = 0
            private var paramsX = 0
            private var paramsY = 0
        }

        override fun onDown(e: MotionEvent?): Boolean {
            e?.apply {
                lastX = rawX.toInt()
                lastY = rawY.toInt()
            }
            val usableWidth = realMetrics.widthPixels - mView.width
            val usableHeight = realMetrics.heightPixels - mView.height
            if (mView.width >= realMetrics.widthPixels) {
                paramsX = 0
            } else {
                paramsX = mParams.x
            }
            paramsX = Math.min(paramsX, usableWidth)
            if (mView.height >= realMetrics.heightPixels) {
                paramsY = 0
            } else {
                paramsY = mParams.y
            }
            paramsY = Math.min(paramsY, usableHeight)
            return false
        }

        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            var dx = ((e2?.rawX ?: 0F) - lastX).toInt()
            var dy = ((e2?.rawY ?: 0F) - lastY).toInt()
            if (mView.width >= realMetrics.widthPixels) {
                dx = 0
            }
            if (mView.height >= realMetrics.heightPixels) {
                dy = 0
            }
            L.i(TAG, "onScroll: Width >>>>>>>>>>>  X = ${e2?.rawX} -- lX = ${lastX}")
            L.i(
                TAG,
                "onScroll: Width >>>>>>>>>>>  ${paramsX} --  ${mParams.x} =+> ${mView.width} -- ${dx} ===> ${realMetrics.widthPixels}"
            )
            L.i(TAG, "onScroll: Height >>>>>>>>>>  Y = ${e2?.rawY} -- lY = ${lastY}")
            L.i(
                TAG,
                "onScroll: Height >>>>>>>>>>  ${paramsY} --  ${mParams.y} =+> ${mView.height} - ${dy} ===> ${realMetrics.heightPixels}"
            )
            val usableWidth = realMetrics.widthPixels - mView.width
            val usableHeight = realMetrics.heightPixels - mView.height
            var endX = paramsX + dx
            endX = Math.max(endX, 0)
            endX = Math.min(endX, usableWidth)

            var endY = paramsY + dy
            endY = Math.max(endY, 0)
            endY = Math.min(endY, usableHeight)

            mParams.apply {
                this.x = endX
                this.y = endY
            }
            updateRemoteView(mView, mContext, mParams)
            return true
        }

        override fun onLongPress(e: MotionEvent?) {

        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            L.i(TAG, "onFling: ")
            return false
        }
    }


    /* -------------------------------------------------------------   */

    private fun targetFloatView(minView: Boolean) {
        L.i(TAG, "targetFloatView: $minView")
        if (minView) {
            if (floatMessageBaseView != null) {
                removeRemoteView(floatMessageBaseView!!)
                floatMessageBaseView = null
            }
            initMessageMinView(applicationContext)
        } else {
            if (floatMessageMinView != null) {
                removeRemoteView(floatMessageMinView!!)
                floatMessageMinView = null
            }
            initMessageBaseView(applicationContext)
        }
    }

    private var floatMessageMinView: FloatMessageMinView? = null

    /**
     * 初始化小图
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initMessageMinView(mContext: Context) {
        floatMessageMinView = FloatMessageMinView(mContext)
        floatMessageMinView?.apply {
            this.floatMessageMinViewListener = FloatMessageMinViewListenerImpl()
            val params = getWindownLayoutParams()
            this.setOnTouchListener(OnTouchListenerImpl(mContext, params, this))
            addRemoteView(this, params)
        }
    }

    private inner class FloatMessageMinViewListenerImpl :
        FloatMessageMinView.FloatMessageMinViewListener {
        override fun onImageClick(v: View?) {
            targetFloatView(false)
        }
    }

    private var floatMessageBaseView: FloatMessageBaseView? = null

    /**
     * 初始化内容
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initMessageBaseView(mContext: Context) {
        floatMessageBaseView = FloatMessageBaseView(mContext)
        floatMessageBaseView?.apply {
            this.floatMessageBaseViewListener = FloatMessageBaseViewListenerImpl()
            val params = getWindownLayoutParams()
            params.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                x = 0
            }
            this.setOnTouchListener(OnTouchListenerImpl(mContext, params, this))
            addRemoteView(this, params)
        }
    }

    private class FloatMessageBaseViewListenerImpl :
        FloatMessageBaseView.FloatMessageBaseViewListener {

    }

}