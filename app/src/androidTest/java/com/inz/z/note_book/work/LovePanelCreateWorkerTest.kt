package com.inz.z.note_book.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.TestWorkerBuilder
import junit.framework.TestCase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2022/5/27 23:13
 */
class LovePanelCreateWorkerTest : TestCase() {

    private lateinit var context: Context
    private lateinit var executor: Executor
    public override fun setUp() {
        super.setUp()
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
    }

    public fun testLoveWork() {
        val worker = TestWorkerBuilder<LovePanelCreateWorker>(context, executor)
            .build()
        println("${worker.doWork()} -------------------")
    }
}