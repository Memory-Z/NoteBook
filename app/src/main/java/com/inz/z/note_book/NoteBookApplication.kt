package com.inz.z.note_book

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import com.inz.z.base.R
import com.inz.z.base.util.CrashHandler
import com.inz.z.base.util.L
import com.inz.z.note_book.base.ActivityLifeCallbackImpl
import com.inz.z.note_book.broadcast.ScreenBroadcast
import com.inz.z.note_book.database.bean.UserInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.service.UserInfoService
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.util.LocalMediaHelper
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

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mContext = this
        L.initL(applicationContext)
        CrashHandler.instance(applicationContext, CrashHandlerListenerImpl())
        GreenDaoHelper.getInstance().initGreenDaoHelper(applicationContext)
        SPHelper.init(applicationContext)
        SPHelper.instance?.saveUpdateVersionUrl(BuildConfig.UPDATE_VERSION_URL)
        SPHelper.instance?.saveCurrentVersionCode(BuildConfig.VERSION_CODE)
        SPHelper.instance?.saveLaterUpdateVersion(false)

        // 设置生命周期监督
        setActivityLifeCallback()

        // 初始化服务
        initService()
        // 初始化广播
        initBroadcast()
    }

    private fun initService() {

        val intent = Intent(mContext, UserInfoService::class.java)
            .apply {
                this.putExtra(UserInfoService.TAG_FLAG_INIT_USER, UserInfoService.FLAG_INIT_USER)
            }
        startService(intent)
    }

    private fun initBroadcast() {
        val screenBroadcast = ScreenBroadcast()
        val intentFilter = IntentFilter()
            .apply {
                this.addAction(Intent.ACTION_SCREEN_OFF)
                this.addAction(Intent.ACTION_SCREEN_ON)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    this.addAction(Intent.ACTION_USER_UNLOCKED)
                }
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
                Log.i(TAG, "uploadLogToServer: ----> $content")
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
}