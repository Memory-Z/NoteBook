package com.inz.z.note_book

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.Toast
import com.inz.z.base.R
import com.inz.z.base.util.CrashHandler
import com.inz.z.base.util.L
import com.inz.z.note_book.base.ActivityLifeCallbackImpl
import com.inz.z.note_book.base.TaskValue
import com.inz.z.note_book.broadcast.ScreenBroadcast
import com.inz.z.note_book.database.bean.UserInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.SPHelper

/**
 * 笔记Application .
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/10/17 14:39.
 */
class NoteBookApplication : Application() {

    companion object {
        private const val TAG = "NoteBookApplication"

        @SuppressLint("StaticFieldLeak")
        lateinit var mInstance: NoteBookApplication
    }


    var mContext: Context? = null

    /**
     * 当前用户信息
     */
    var currentUserInfo: UserInfo? = null
    private var mHandler: Handler? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mContext = this
        mHandler = Handler(Looper.getMainLooper())
        // 初始化日志
        L.initL(applicationContext)
        // 初始化 崩溃管理
        CrashHandler.instance(applicationContext, CrashHandlerListenerImpl())
        // 初始化数据库
        GreenDaoHelper.getInstance().initGreenDaoHelper(applicationContext)
        // 初始化 SharePreferences
        SPHelper.init(applicationContext)
        SPHelper.instance?.saveUpdateVersionUrl(BuildConfig.UPDATE_VERSION_URL)
        SPHelper.instance?.saveCurrentVersionCode(BuildConfig.VERSION_CODE)
        SPHelper.instance?.saveLaterUpdateVersion(false)

        // 初始化 数据
        TaskValue.initScheduleTypeValue(this)

        val processName = getProcessNameStr()
        L.i(TAG, "onCreate: current process name = $processName")
        // 判断当前进程
        when (processName) {
            packageName -> {
                // 主进程
                initMainProcessService()
            }
            else -> {
                // 其他进程
            }
        }

    }

    /**
     * 初始化主进程服务
     */
    private fun initMainProcessService() {
        // 设置生命周期监督
        setActivityLifeCallback()
        // 初始化服务
        initService()
        // 初始化广播
        initBroadcast()
    }

    /**
     * 初始化服务。
     */
    private fun initService() {

//        val intent = Intent(mContext, UserInfoService::class.java)
//            .apply {
//                this.putExtra(UserInfoService.TAG_FLAG_INIT_USER, UserInfoService.FLAG_INIT_USER)
//            }
//        startService(intent)
        mHandler?.postDelayed(
            {
                // 发送启动Application 广播
                val backupService = Intent()
                backupService.`package` = packageName
                backupService.action = Constants.BaseBroadcastParams.APPLICATION_CREATE_ACTION
                sendBroadcast(backupService)
                L.w(TAG, "initService: SEND APPLICATION BROADCAST. ")
            },
            500
        )

    }

    /**
     * 初始化广播
     */
    private fun initBroadcast() {
        L.i(TAG, "initBroadcast: ")
        // 屏幕显示广播。
        val screenBroadcast = ScreenBroadcast()
        val intentFilter = IntentFilter()
            .apply {
                this.addAction(Intent.ACTION_SCREEN_OFF)
                this.addAction(Intent.ACTION_SCREEN_ON)
                this.addAction(Intent.ACTION_USER_UNLOCKED)
                this.addAction(Intent.ACTION_USER_PRESENT)
            }
        registerReceiver(screenBroadcast, intentFilter)
    }

    /**
     * 设置生命周期回调
     */
    private fun setActivityLifeCallback() {
        ActivityLifeCallbackImpl.init(
            this,
            object : ActivityLifeCallbackImpl.ActivityLifeCallbackListener {
                override fun applicationInBackground(background: Boolean) {
                    L.i(TAG, "applicationInBackground: $background")
//                    val intent = Intent()
//                    intent.action = Constants.LifeAction.LIFE_CHANGE_ACTION
//                    sendBroadcast(intent)
                }
            }
        )
    }

    /**
     * 崩溃处理 监听实现
     */
    private inner class CrashHandlerListenerImpl : CrashHandler.CrashHandlerListener {
        override fun setHaveCrash() {
            SPHelper.instance?.setCrashState(true)
        }

        override fun uploadLogToServer(filePath: String?, content: String?) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "uploadLogToServer: ----> \n $content")
            }
        }

        override fun showErrorTintOnUI() {
            applicationContext?.apply {
                Toast.makeText(
                    this,
                    this.getString(R.string._sorry_application_have_error_will_exit),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    /* ------------------- 获取进程名 -------------------- */

    /**
     * 获取当前进程名
     */
    private fun getProcessNameStr(): String {
        // 如果 大于P 直接使用Application 方法获取，
        // 否则使用ActivityManager 获取
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return getProcessName()
        } else {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            activityManager?.let {
                val list = it.runningAppProcesses
                // 根据 当前进程 ID 判断是否为同一进程，
                val currentPid = Process.myPid()
                list?.forEach { info ->
                    if (info.pid == currentPid) {
                        return info.processName
                    }
                }
            }
            return ""
        }
    }

    /* ------------------- 获取进程名 -------------------- */
}