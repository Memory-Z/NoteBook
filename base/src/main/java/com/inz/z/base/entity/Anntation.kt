package com.inz.z.base.entity

import androidx.annotation.IntDef

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/19 09:25.
 */


@IntDef(
    Constants.BitmapMergeType.HORIZONTAL,
    Constants.BitmapMergeType.VERTICAL
)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MergeBitmapOrientation {

}