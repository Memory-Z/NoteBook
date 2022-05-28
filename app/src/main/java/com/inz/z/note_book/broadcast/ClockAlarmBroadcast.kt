package com.inz.z.note_book.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.note_book.service.CreateLovePanelService
import com.inz.z.note_book.util.Constants

/**
 * 时钟 广播
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/15 15:51.
 */
class ClockAlarmBroadcast : BroadcastReceiver() {

    companion object {
        private const val TAG = "ClockAlarmBroadcast"
        private val receiveListenerList = mutableListOf<ReceiveListener>()

        fun addListener(listener: ReceiveListener) {
            receiveListenerList.add(listener)
        }

        fun removeListener(listener: ReceiveListener?) {
            receiveListenerList.remove(listener)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        L.i(TAG, "onReceive ------------- ")
        val action = intent?.action
        L.i(TAG, "onReceive: $action <<<<<< ")
        val bundle = intent?.extras
        when (action) {
            Constants.CLOCK_ALARM_START_ACTION -> {

            }
            Constants.AlarmAction.ALARM_BROADCAST_BASE_ACTION -> {
                L.i(TAG, " ALARM_BROADCAST_BASE_ACTION: ---------------------->  ")
                for (receiveListener in receiveListenerList) {
                    receiveListener.onDayTwo(action)
                }
            }
            Constants.AlarmAction.ALARM_BROADCAST_LAUNCHER_ACTION -> {
                // 启动
                bundle?.apply {
                    val taskPackageName = this.getString(
                        Constants.AlarmAction.ALARM_BROADCAST_LAUNCHER_FLAG_PACKAGE_NAME,
                        ""
                    )
                    val taskLabel = this.getString(
                        Constants.AlarmAction.ALARM_BROADCAST_LAUNCHER_FLAG_LABEL,
                        ""
                    )
                    if (taskPackageName.isNotEmpty() && context != null) {
                        L.i(TAG, "onReceive: launcher ---> $taskPackageName")
                        LauncherHelper.launcherPackageName(context, taskPackageName)
                    } else {
                        L.w(
                            TAG,
                            "onReceive: launcher ---> $taskPackageName  & $taskLabel  is failure. "
                        )
                    }
                }
            }
            Constants.AlarmAction.ALARM_BROADCAST_SCHEDULE_ACTION -> {
                // 计划

            }
            Constants.AlarmAction.ALARM_BROADCAST_HINT_ACTION -> {
                // 提示
                bundle?.apply {
                    val message =
                        this.getString(Constants.AlarmAction.ALARM_BROADCAST_HINT_FLAG_MESSAGE, "")
                    L.i(TAG, "onReceive: hint $message. ")
                    if (context != null && message.isNotEmpty()) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Constants.AlarmAction.ALARM_BROADCAST_CLOCK_ACTION -> {
                // 闹钟
            }
            // 创建 LovePanel 广播
            Constants.AlarmAction.ALARM_BROADCAST_CLOCK_CREATE_LOVE_PANEL_ACTION -> {
                context?.let {
                    sendStartCreateLovePanelService(it)
                }
            }

            else -> {

            }
        }
    }

    /**
     * 接收监听
     */
    interface ReceiveListener {
        /**
         * 第二天
         */
        fun onDayTwo(t: String)
    }

    /**
     * 创建 LovePanel
     */
    private fun sendStartCreateLovePanelService(context: Context) {
        val service = Intent(context, CreateLovePanelService::class.java)
        context.startService(service)
        L.i(TAG, "sendStartCreateLovePanelService: starting ... ")
    }

}