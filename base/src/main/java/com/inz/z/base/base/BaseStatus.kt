package com.inz.z.base.base

import androidx.annotation.IntDef
import com.inz.z.base.entity.Constants

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2021/11/28 22:44
 */


/**
 * 选择文件参数
 */
object ChooseFileConstants {
    const val CHOOSE_FILE_RESULT_CODE = 0x010001
    const val CHOOSE_FILE_RESULT_LIST_TAG = "chooseFileList"
    const val CHOOSE_FILE_RESULT_SIZE_TAG = "chooseFileSize"

    /**
     * 选择文件显示类型
     */
    const val SHOW_TYPE_DIR = 0x000A01
    const val SHOW_TYPE_IMAGE = 0x000A02
    const val SHOW_TYPE_AUDIO = 0x000A03
    const val SHOW_TYPE_VIDEO = 0x000A04
}

/**
 * 选择文件显示类型
 */
@IntDef(
    ChooseFileConstants.SHOW_TYPE_AUDIO,
    ChooseFileConstants.SHOW_TYPE_DIR,
    ChooseFileConstants.SHOW_TYPE_IMAGE,
    ChooseFileConstants.SHOW_TYPE_VIDEO
)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
annotation class ChooseFileShowType {

}


/**
 * 文件类型
 */
object FileType {

    const val FILE_TYPE_FILE = 0x0000A001
    const val FILE_TYPE_IMAGE = 0x0000A002
    const val FILE_TYPE_AUDIO = 0x0000A003
    const val FILE_TYPE_VIDEO = 0x0000A004
    const val FILE_TYPE_TEXT = 0x0000A005
    const val FILE_TYPE_APPLICATION = 0x0000A006
    const val FILE_TYPE_OTHER = 0x0000A007
}

@IntDef(
    FileType.FILE_TYPE_FILE,
    FileType.FILE_TYPE_IMAGE,
    FileType.FILE_TYPE_AUDIO,
    FileType.FILE_TYPE_VIDEO,
    FileType.FILE_TYPE_TEXT,
    FileType.FILE_TYPE_APPLICATION,
    FileType.FILE_TYPE_OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class FileTypeAnn
