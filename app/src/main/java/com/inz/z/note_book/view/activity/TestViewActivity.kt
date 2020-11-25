package com.inz.z.note_book.view.activity

import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inz.z.base.util.FileUtils
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.OperationLogInfo
import com.inz.z.note_book.database.controller.LogController
import com.inz.z.note_book.util.ExcelUtil
import com.inz.z.note_book.view.activity.adapter.TestViewStringRvAdapter
import com.inz.z.note_book.view.activity.test_data.TestExcelBean
import kotlinx.android.synthetic.main.test_view_adapter.*
import java.io.File

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/07/27 15:58.
 */
class TestViewActivity : AbsBaseActivity() {
    private var testAdapter: TestViewStringRvAdapter? = null

    private var stringList: MutableList<String> = arrayListOf()

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.test_view_adapter
    }

    override fun initView() {
        test_view_btn.setOnClickListener {

            testAdapter?.let {
                val posi = stringList.size - 1
                it.list.removeAt(posi)
                it.notifyItemRemoved(posi)
            }
        }

        testAdapter = TestViewStringRvAdapter(mContext)
        test_view_rv.apply {
            layoutManager = GridLayoutManager(mContext, 3)
            this.adapter = testAdapter
        }

        Glide.with(mContext).load(ContextCompat.getDrawable(mContext, R.drawable.img_photo_3))
            .into(test_top_bg_iv)

    }

    override fun resetBottomNavigationBar(): Boolean {
        return false
    }

    override fun initData() {
        for (i in 1..20) {
            stringList.add("--> $i")
        }
        testAdapter?.list = stringList
        testAdapter?.clickListener = object : TestViewStringRvAdapter.ClickListener {
            override fun addClick(view: View) {
                val p = stringList.size
                stringList.add(" A -> $p")
                testAdapter?.notifyItemInserted(p)

                val dataList = mutableListOf<TestExcelBean>()
                for (size in 0..10) {
                    val bean = TestExcelBean()
                    bean.name = "name - $size"
                    bean.value = "v === $size"
                    dataList.add(bean)
                }

                val logList = LogController.query()

                val dirPath = FileUtils.getCacheFilePath(mContext) + File.separatorChar + "excel"
                val dir = File(dirPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = File(dir, "test.xls")

                val clazz = OperationLogInfo::class
                val sheetName = clazz.simpleName.toString()

                ExcelUtil.writeDataToExcel(
                    logList!!.toMutableList(),
                    clazz,
                    file.absolutePath,
                    sheetName
                )
            }
        }
    }
}