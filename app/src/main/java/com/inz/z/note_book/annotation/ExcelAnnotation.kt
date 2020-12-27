package com.inz.z.note_book.annotation

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/05 11:51.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelAnnotation(val value: String = "", val column: String = "")