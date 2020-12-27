package com.inz.z.base.view.activity

import org.junit.Assert.*
import org.junit.Test
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/19 14:04.
 */
class ChooseFileActivityTest {

    var isStop = AtomicInteger(0)

    @Test
    fun randomTest() {

        object : Thread() {
            override fun run() {
                super.run()
                for (i in 0..9) {
                    RandomNumber("ThreadNumber_$i").start()
                }
            }
        }.start()
        while (isStop.get() != 10) {
            try {
                Thread.sleep(2000)
            } catch (e: Exception) {

            }
        }
        System.out.println("--------------- Test is end.")
    }

    inner class RandomNumber(name: String) : Thread(name) {

        override fun run() {
            super.run()
            var i = 0
            val nums = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            while (i < 10000) {
                val random = Random()
                val number = random.nextInt(10)
                nums[number]++
                System.out.println("---------------------> Name: $name ($i)-- Number: $number  ---- ${System.currentTimeMillis()}")
                try {
                    Thread.sleep(500)
                    i++
                } catch (e: Exception) {
                }
            }
            System.out.println("<<<<<<<<<<<<<<<${isStop.addAndGet(1)}")
            val list = nums.asList()
            val sb = StringBuilder()
            list.forEachIndexed { index, va ->
                val s = StringBuilder()
                for (d in 0..va) {
                    s.append("*")
                }
                sb.append(index).append(":").append("------------$s").append("\r\n")
            }
            System.out.println(" >>>>>>>>>>> END ${name}--- \r\n${sb}")
        }
    }
}