package com.inz.z.note_book.database.controller

import android.util.Log
import com.inz.z.note_book.database.UserInfoDao
import com.inz.z.note_book.database.bean.UserInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import com.inz.z.note_book.database.util.GreenDaoOpenHelper

/**
 *
 *
 * 用户操作
 * ====================================================
 * Create by 11654 in 2021/1/1 1:07
 */
object UserController {
    private const val TAG = "UserController"

    private fun getUserDao() = GreenDaoHelper.getInstance().getDaoSession()?.userInfoDao

    /**
     * 添加用户信息
     */
    fun insertUser(userInfo: UserInfo) {
        getUserDao()?.let {
            it.insert(userInfo)
            updateUserLogInfo(null, userInfo)
        }
    }

    fun updateUser(userInfo: UserInfo) {
        getUserDao()?.let {
            val ur = it.queryBuilder()
                .where(UserInfoDao.Properties.UserId.eq(userInfo.userId))
                .unique()
            if (ur != null) {
                it.update(userInfo)
                updateUserLogInfo(ur, userInfo)
            }
        }
    }

    /**
     * 更新用户信息日志
     * @param oldUser 旧 用户信息
     * @param newUser 新 用户信息
     */
    fun updateUserLogInfo(oldUser: UserInfo?, newUser: UserInfo) {
        val methods = UserInfo::class.java.methods
        val oldStr = ""
        val newStr = ""
        for (method in methods) {
            val oldValue = method.invoke(oldUser, "") as String?
            val newValue = method.invoke(newUser, "") as String?
            if (oldValue != newValue) {
                oldStr.plus("${method.name}=$oldStr; ")
                newStr.plus("${method.name}=$newStr; ")
            }
        }
        Log.i(TAG, "updateUserLogInfo: $oldStr --- $newStr")

    }
}