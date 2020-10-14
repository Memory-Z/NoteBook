package com.inz.z.base.entity

import android.content.Context
import androidx.annotation.IntDef
import com.inz.z.base.BuildConfig

/**
 * 静态 实体类
 * Create By 11654
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create By 2018/7/21 10:16
 */
class Constants(val applicationId: String) {


    companion object {
        private var constants: Constants? = null

        public fun getInstant(applicationId: String): Constants {
            if (constants == null) {
                constants = Constants(applicationId)
            }
            return constants!!
        }


    }

    val VERSION_UPDATE_ACTION = applicationId + ".action.VERSION_UPDATE_ACTION"
    val VERSION_UPDATE_DOWNLOAD_ACTION = applicationId + ".action.VERSION_UPDATE_DOWNLOAD_ACTION"
    val VERSION_UPDATE_INSTALL_ACTION = applicationId + ".action.VERSION_UPDATE_INSTALL_ACTION"

    object VersionUpdate {

        const val VERSION_UPDATE_PERMISSION = "com.inz.z.base.permission.VERSION_UPDATE"

        const val VERSION_UPDATE_URL = "versionUpdateUrl"

        const val VERSION_UPDATE_FILE_PATH = "versionUpdateFilePath"

        const val VERSION_UPDATE_TOTAL = "versionUpdateTotal"

        const val VERSION_UPDATE_PROGRESS = "versionUpdateProgress"

        const val VERSION_UPDATE_FAILURE = "versionUpdateFailure"
        const val VERSION_UPDATE_FAILURE_MESSAGE = "versionUpdateFailureMessage"

        const val VERSION_UPDAE_SUCCESS = "versionUpdateSuccess"

        const val VERSION_NOTIFICATION_CHANNEL_ID = "VersionUpdate"

        const val VERSION_NOTIFICATION_ID = 0x000FFF00

        /**
         * 最大显示更新提示次数
         */
        const val MAX_SHOW_UPDATE_NUMBER = 3
    }

    object FileType {

        const val FILE_TYPE_FILE = 0x0000A001
        const val FILE_TYPE_IMAGE = 0x0000A002
        const val FILE_TYPE_AUDIO = 0x0000A003
        const val FILE_TYPE_VIDEO = 0x0000A004
        const val FILE_TYPE_TEXT = 0x0000A005
        const val FILE_TYPE_APPLICATION = 0x0000A006
        const val FILE_TYPE_OTHER = 0x0000A007

        @IntDef(
            FILE_TYPE_FILE,
            FILE_TYPE_IMAGE,
            FILE_TYPE_AUDIO,
            FILE_TYPE_VIDEO,
            FILE_TYPE_TEXT,
            FILE_TYPE_APPLICATION,
            FILE_TYPE_OTHER
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class FileTypeAnn
    }

}