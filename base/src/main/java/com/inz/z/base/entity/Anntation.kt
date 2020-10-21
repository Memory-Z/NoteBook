package com.inz.z.base.entity

import androidx.annotation.IntDef

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/19 09:25.
 */


/**
 * 合并图片方向
 */
@IntDef(
    Constants.BitmapMergeType.HORIZONTAL,
    Constants.BitmapMergeType.VERTICAL
)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MergeBitmapOrientation {

}

/**
 * 选择文件显示类型
 */
@IntDef(
    Constants.FileShowType.SHOW_TYPE_AUDIO,
    Constants.FileShowType.SHOW_TYPE_DIR,
    Constants.FileShowType.SHOW_TYPE_IMAGE,
    Constants.FileShowType.SHOW_TYPE_VIDEO
)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
annotation class ChooseFileShowType {

}