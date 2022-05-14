package com.inz.z.note_book.view.fragment

import junit.framework.TestCase
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2022/5/14 21:10
 */
class NoteNavFragmentTest : TestCase() {

    private var testRunnable: TestRunnable? = null
    private var testFuture: ScheduledFuture<*>? = null
    private var scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor? = null


    fun testSchedule() {
        scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(3)
        exec()
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
            println("NoteNavFragmentTest.scheduleTest 000<< ")
        }
        scheduledThreadPoolExecutor?.execute(CancelRunnable())
        try {
            Thread.sleep(3000)
        } catch (e: Exception) {
            println("NoteNavFragmentTest.scheduleTest 000<< ")
        }
    }

    private fun exec() {
        println("NoteNavFragmentTest.exec Start")
        if (testRunnable == null) {
            testRunnable = TestRunnable()
        }
        testFuture = scheduledThreadPoolExecutor?.schedule(
            testRunnable,
            2,
            TimeUnit.MILLISECONDS
        )
    }

    private inner class TestRunnable : Runnable {
        override fun run() {
            println("TestRunnable.run -------------------->>> ")
            exec()
        }
    }

    private inner class CancelRunnable : Runnable {
        override fun run() {
            println("CancelRunnable.run !!! ")
//            scheduledThreadPoolExecutor?.queue?.remove(testRunnable)
            val isRemove = scheduledThreadPoolExecutor?.remove(testRunnable)
            testFuture?.cancel(true)
            println("CancelRunnable.run  END $isRemove")
        }
    }
}