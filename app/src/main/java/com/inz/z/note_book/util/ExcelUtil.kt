package com.inz.z.note_book.util

import android.text.TextUtils
import android.util.Log
import com.inz.z.note_book.annotation.ExcelAnnotation
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Excel Util
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/05 10:06.
 */
object ExcelUtil {

    const val TAG = "ExcelUtil"
    const val BASE_MIN_FONT_SIZE = 14

    private fun format(fontSize: Int, alignment: Alignment): WritableCellFormat? {
        try {
            val arialFont = WritableFont(WritableFont.ARIAL, fontSize)
            arialFont.colour = Colour.BLACK
            val arialFormat = WritableCellFormat(arialFont)
            arialFormat.apply {
                setAlignment(alignment)
                setBorder(Border.ALL, BorderLineStyle.THIN)
                setBackground(if (fontSize > BASE_MIN_FONT_SIZE) Colour.GRAY_25 else Colour.WHITE)
            }
            return arialFormat
        } catch (ignore: Exception) {

        }
        return null

    }

    /**
     * init Excel file
     * @param fileName file's name
     * @param sheetName sheet's name
     * @param colName all column name
     */
    private fun <T : Any> initExcel(fileName: String, sheetName: String, clazzName: KClass<T>) {
        val arial14FontFormat = format(14, Alignment.CENTRE)
        var workbook: WritableWorkbook? = null
        try {
            val file = File(fileName)
            if (!file.exists()) {
                val create = file.createNewFile()
            }
            var sheet: WritableSheet?
            try {
                val book = Workbook.getWorkbook(file)
                workbook = Workbook.createWorkbook(file, book)
                try {
                    sheet = workbook.getSheet(sheetName)
                } catch (e: Exception) {
                    val sheetNum = workbook.sheetNames.size
                    sheet = workbook.createSheet(sheetName, sheetNum)
                }
            } catch (e: Exception) {
                workbook = Workbook.createWorkbook(file)
                sheet = workbook.createSheet(sheetName, 0)
            }
            if (sheet == null || workbook == null) {
                return
            }
            // create title's name
            val baseRow = sheet.rows
            sheet.addCell(Label(0, baseRow, sheetName, arial14FontFormat))
            val rowNo = sheet.rows
            var index = 0
            clazzName.memberProperties.forEach {
                var columnName = it.name
                val anno = it.findAnnotation<ExcelAnnotation>()
                anno?.let {
                    val column = anno.value
                    if (!TextUtils.isEmpty(column)) {
                        columnName = column
                    }
                    val label = Label(index, rowNo, columnName, arial14FontFormat)
                    sheet.addCell(label)
                    index++
                }

            }

            sheet.setRowView(0, 340)
            sheet.mergeCells(0, baseRow, index - 1, baseRow)
            workbook.write()
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        } finally {
            try {
                workbook?.close()
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }
        }

    }

    /**
     * write data to excel file .
     *
     * @param dataList data list
     * @param clazzName data class
     * @param fileName Excel's file name
     * @param sheetName sheet's name
     */
    fun <T : Any> writeDataToExcel(
        dataList: MutableList<T>,
        clazzName: KClass<T>,
        fileName: String,
        sheetName: String
    ) {
        initExcel(fileName, sheetName, clazzName)
        val arial10FontFormat = format(10, Alignment.LEFT)
        var writableWorkbook: WritableWorkbook? = null
        var inputStream: InputStream? = null
        try {
            val workbookSettings = WorkbookSettings()
            workbookSettings.encoding = Charsets.UTF_8.name()
            val file = File(fileName)
            inputStream = FileInputStream(file)

            val workbook = Workbook.getWorkbook(inputStream)
            writableWorkbook = Workbook.createWorkbook(file, workbook)
            val sheet = writableWorkbook.getSheet(sheetName)
            var rowNo = sheet.rows
            for (data in dataList) {
                try {
                    var colNo = 0
                    clazzName.memberProperties.forEach {

                        val anno = it.findAnnotation<ExcelAnnotation>()
                        if (anno != null) {
                            sheet.addCell(
                                Label(
                                    colNo,
                                    rowNo,
                                    it.get(data).toString(),
                                    arial10FontFormat
                                )
                            )
                            colNo++
                        }
                    }
                    rowNo++
                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                }
            }
            writableWorkbook.write()
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        } finally {
            try {
                writableWorkbook?.close()
                inputStream?.close()
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }
        }

    }
}