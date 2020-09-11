package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.activity.adapter.RecordListRvAdapter
import com.inz.z.note_book.viewmodel.RecordViewModel
import kotlinx.android.synthetic.main.activity_record.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/10 14:09.
 */
class RecordActivity : BaseNoteActivity() {

    companion object {
        private const val TAG = "RecordActivity"


    }

    private var recordListRvAdapter: RecordListRvAdapter? = null

    private var recordViewModel: RecordViewModel? = null


    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_record
    }

    override fun initView() {
        recordListRvAdapter = RecordListRvAdapter(mContext)
        recordListRvAdapter?.apply {
            this.listener = RecordListRvAdapterListenerImpl()
        }
        record_content_rv.apply {
            this.layoutManager = LinearLayoutManager(mContext)
            this.adapter = recordListRvAdapter
        }
        record_add_fab?.setOnClickListener {
            startActivity(Intent(mContext, NewRecordActivity::class.java))
        }

        record_content_srl?.setOnRefreshListener {
            recordViewModel?.refreshRecordList()
        }
    }

    override fun initData() {

        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        recordViewModel?.refreshRecordList()
    }

    /**
     * 初始化View Model
     */
    private fun initViewModel() {
        recordViewModel = ViewModelProvider.NewInstanceFactory().create(RecordViewModel::class.java)
        recordViewModel?.getRecordInfoList()?.observe(
            this,
            Observer {
                record_content_srl?.isRefreshing = false
                recordListRvAdapter?.refreshData(it)
            }
        )
    }

    inner class RecordListRvAdapterListenerImpl : RecordListRvAdapter.RecordListRvAdapterListener {
        override fun onItemClick(v: View?, position: Int) {
            recordListRvAdapter?.apply {
                val recordInfoStatus = this.list.get(position)
                val intent = Intent(mContext, NewRecordActivity::class.java)
                val bundle = Bundle()
                bundle.putString("recordInfoId", recordInfoStatus.id)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }
}