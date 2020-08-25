package com.inz.z.base.entity

import androidx.annotation.NonNull
import java.io.Serializable

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/25 09:17.
 */
class UpdateVersionBean : Serializable {
    /**
     * 版本号
     */
    var versionCode = 1

    /**
     * 版本名
     */
    var versionName = ""

    /**
     * 更新描述
     */
    var content = ""

    /**
     * 最小版本号
     */
    var minSupport = 1

    /**
     * 更新地址
     */
    var url = ""

    /**
     * 忽略版本号
     */
    var ignoreVersion = arrayListOf<Int>()

    @NonNull
    override fun toString(): String {
        return "UpdateVersionBean(versionCode=$versionCode, versionName='$versionName', content='$content', minSupport=$minSupport, url='$url', ignoreVersion=$ignoreVersion)"
    }


}