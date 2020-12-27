package com.inz.z.note_book

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.inz.z.base.R
import com.inz.z.base.util.CrashHandler
import com.inz.z.base.util.L
import com.inz.z.note_book.base.ActivityLifeCallbackImpl
import com.inz.z.note_book.database.util.GreenDaoHelper
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
    }

    override fun onCreate() {
        super.onCreate()
        L.initL(applicationContext)
        CrashHandler.instance(applicationContext, CrashHandlerListenerImpl())
        GreenDaoHelper.getInstance().initGreenDaoHelper(applicationContext)
        SPHelper.init(applicationContext)
        SPHelper.instance?.saveUpdateVersionUrl(BuildConfig.UPDATE_VERSION_URL)
        SPHelper.instance?.saveCurrentVersionCode(BuildConfig.VERSION_CODE)
        SPHelper.instance?.saveLaterUpdateVersion(false)

        // 设置生命周期监督
        setActivityLifeCallback()

        LocalMediaHelper.getLocalPicture(this, 0)
        LocalMediaHelper.getLocalPicture(this, 1)
        LocalMediaHelper.getLocalPicture(this, 2)
        LocalMediaHelper.getLocalPicture(this, 3)

        LocalMediaHelper.getLocalAudioList(this, 0)
        LocalMediaHelper.getLocalAudioList(this, 1)
        LocalMediaHelper.getLocalAudioList(this, 2)
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
                    val intent = Intent()
                    intent.action = Constants.LifeAction.LIFE_CHANGE_ACTION
                    sendBroadcast(intent)
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