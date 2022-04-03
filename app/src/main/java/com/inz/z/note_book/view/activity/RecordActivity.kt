package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityRecordBinding
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.activity.adapter.RecordListRvAdapter
import com.inz.z.note_book.view.dialog.RecordSearchDialog
import com.inz.z.note_book.view.dialog.SearchDialog
import com.inz.z.note_book.viewmodel.RecordViewModel

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

    /**
     * 搜索内容
     */
    private var searchRecordContent = ""
    private var binding: ActivityRecordBinding? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_record
    }

    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityRecordBinding.inflate(layoutInflater)
            .apply {
                setContentView(root)
            }
    }

    override fun initView() {
        recordListRvAdapter = RecordListRvAdapter(mContext)
        recordListRvAdapter?.apply {
            this.listener = RecordListRvAdapterListenerImpl()
        }
        binding?.let {
            it.recordContentRv.apply {
                this.layoutManager = LinearLayoutManager(mContext)
                this.adapter = recordListRvAdapter
            }
            it.recordAddFab.setOnClickListener {
                startActivity(Intent(mContext, NewRecordActivity::class.java))
            }
            it.recordContentSrl.setOnRefreshListener {
                recordViewModel?.refreshRecordList()
            }
            it.recordTopSearchRl.setOnClickListener {
                showRecordSearchDialog()
            }
        }
    }

    override fun initData() {

        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        recordViewModel?.refreshRecordList()
    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        binding = null
    }

    override fun onDestroyData() {
        super.onDestroyData()
        recordViewModel = null
    }

    /**
     * 初始化View Model
     */
    private fun initViewModel() {
        recordViewModel = ViewModelProvider.NewInstanceFactory().create(RecordViewModel::class.java)
        recordViewModel?.getRecordInfoList()?.observe(
            this,
            Observer {
                binding?.recordContentSrl?.isRefreshing = false
                if (!TextUtils.isEmpty(searchRecordContent)) {
                    binding?.recordTopSearchTv?.text = searchRecordContent
                } else {
                    binding?.recordTopSearchTv?.text =
                        mContext.getString(R.string.search_record_content)
                }
                recordListRvAdapter?.refreshData(it, searchRecordContent)
            }
        )
        recordViewModel?.getRecordInfoListSize()?.observe(
            this,
            Observer {
                binding?.recordContentSrl?.isRefreshing = false
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

    /**
     * 显示记录搜索弹窗
     */
    private fun showRecordSearchDialog() {
        if (mContext == null) {
            L.i(TAG, "showRecordSearchDialog: mContext is null. ")
            return
        }
        var recordSearchDialog =
            supportFragmentManager.findFragmentByTag("RecordSearchDialog") as RecordSearchDialog?
        if (recordSearchDialog == null) {
            recordSearchDialog = RecordSearchDialog.getInstance(
                "",
                object : SearchDialog.SearchDialogListener {
                    override fun onSearchClick(search: String, v: View?) {
                        L.i(TAG, "showRecordSearchDialog: onSearchClick: -- ")
                        searchRecordContent = search
                        recordViewModel?.queryRecordList(search, true)
                    }

                    override fun onSearchContentChange(search: CharSequence?) {
                        val str = search?.toString() ?: ""
                        L.i(TAG, "showRecordSearchDialog: onSearchContentChange: -- $str ")
//                        if (!TextUtils.isEmpty(str)) {
//                            recordViewModel?.queryRecordList(str, false)
//                        }
                    }
                }
            )
        }
        if (!recordSearchDialog.isAdded && !recordSearchDialog.isVisible) {
            recordSearchDialog.show(supportFragmentManager, "RecordSearchDialog")
        }
    }

}