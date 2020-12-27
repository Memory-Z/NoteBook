package com.inz.z.note_book.view.activity.test_data

import com.inz.z.note_book.annotation.ExcelAnnotation

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/05 14:23.
 */
class TestExcelBean {
    @ExcelAnnotation("Name")
    public var name: String = ""

    @ExcelAnnotation("Value", column = "eee")
    public var value: String = ""

}