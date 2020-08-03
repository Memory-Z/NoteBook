package com.inz.z.base.entity

import android.content.Context
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

    }


}