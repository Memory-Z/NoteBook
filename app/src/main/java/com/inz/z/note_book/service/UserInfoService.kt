package com.inz.z.note_book.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.ThreadPoolUtils
import com.inz.z.note_book.NoteBookApplication
import com.inz.z.note_book.database.bean.UserInfo
import com.inz.z.note_book.database.controller.UserController
import java.util.*

/**
 *
 * 用户信息 Service
 * ====================================================
 * Create by 11654 in 2021/1/1 21:18
 */
class UserInfoService : Service() {

    companion object {
        private const val TAG = "UserInfoService"

        const val FLAG_INIT_USER = 0x00F0
        const val TAG_FLAG_INIT_USER = "FLAG_INIT_USER"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.getIntExtra(TAG_FLAG_INIT_USER, 0) == FLAG_INIT_USER) {
                initUserInfo()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initUserInfo() {
        ThreadPoolUtils
            .getWorkThread(TAG + "_initUserInfo")
            .execute {
                // 获取当前用户信息。
                val userInfo = UserController.getCurrentUser() ?: createDefaultUserInfo()
                NoteBookApplication.mInstance.currentUserInfo = userInfo
            }

    }

    /**
     * 创建默认用户信息
     */
    private fun createDefaultUserInfo(): UserInfo {
        val calender = Calendar.getInstance(Locale.getDefault())
        val num = BaseTools.getDateFormat("MMdd", Locale.getDefault()).format(calender.time)
        val userInfo = UserInfo()
            .apply {
                this.enable = 1
                this.password = "111111"
                this.nickName = "nickname_$num"
                this.createDate = calender.time
                this.updateDate = calender.time
            }
        UserController.insertUser(userInfo)
        return userInfo
    }
}