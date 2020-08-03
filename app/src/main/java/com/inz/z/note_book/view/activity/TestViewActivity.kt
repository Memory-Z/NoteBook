package com.inz.z.note_book.view.activity

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.view.activity.adapter.TestViewStringRvAdapter
import kotlinx.android.synthetic.main.test_view_adapter.*

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

    }

    override fun initData() {
        for (i in 1..200) {
            stringList.add("--> $i")
        }
        testAdapter?.list = stringList
        testAdapter?.clickListener = object : TestViewStringRvAdapter.ClickListener {
            override fun addClick(view: View) {
                val p = stringList.size
                stringList.add(" A -> $p")
                testAdapter?.notifyItemInserted(p)
            }
        }
    }
}