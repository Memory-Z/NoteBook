package com.inz.z.base.util

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

import org.junit.Assert.*

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 15:58.
 */
class ProviderUtilTest {

    @Test
    fun queryFileImageWithContextProvider() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val list = ProviderUtil.queryFileImageWithContextProvider(context)

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ${list.size}")
        for (item in list) {
            System.out.println("------------> $item")
        }
    }

    @Test
    fun queryFileListByDir() {
        val filePath = FileUtils.getSDPath();
        val list = ProviderUtil.queryFileListByDir(filePath)
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ${list.size}")
        for (item in list) {
            System.out.println("------------> $item")
        }
    }
}