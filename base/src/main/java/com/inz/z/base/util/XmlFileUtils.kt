package com.inz.z.base.util

import android.content.res.XmlResourceParser
import android.text.TextUtils

/**
 * XML 文件 工具
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/12 16:03.
 */
object XmlFileUtils {

    /**
     * 获取XML 文件数据列表
     * @param parser Xml 文件
     * @param checkTag 需要解析标签
     * @param clazz 解析后的对象
     */
    fun <T> getXmlValueDataList(
        parser: XmlResourceParser,
        checkTag: String,
        clazz: Class<T>
    ): MutableList<T> {
        val list = mutableListOf<T>()
        try {
            var eventType = parser.eventType
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                when (eventType) {
                    XmlResourceParser.START_DOCUMENT -> {

                    }
                    XmlResourceParser.START_TAG -> {
                        val tagName = parser.name
                        if (checkTag.equals(tagName)) {
                            val count = parser.attributeCount
                            val attrNameList = mutableListOf<String>()
                            for (index in 0..count - 1) {
                                attrNameList.add(parser.getAttributeName(index))
                            }
                            val instant = clazz.newInstance()

                            val fields = clazz.declaredFields
                            for (field in fields) {
                                var xmlValue = ""
                                val fieldName = field.name
                                attrNameList.forEachIndexed { index, s ->
                                    if (s.equals(fieldName)) {
                                        xmlValue = parser.getAttributeValue(index) ?: ""
                                        return@forEachIndexed
                                    }
                                }
                                if (!TextUtils.isEmpty(xmlValue)) {
                                    field.isAccessible = true
                                    field.set(instant, xmlValue)
                                }
                            }
                            list.add(instant)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

}