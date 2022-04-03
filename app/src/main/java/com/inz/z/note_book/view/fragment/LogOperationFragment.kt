package com.inz.z.note_book.view.fragment

import android.view.View
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.database.controller.LogController
import com.inz.z.note_book.databinding.FragmentLogOperationBinding
import com.inz.z.note_book.view.fragment.adapter.LogOperationRowTitleRvAdapter
import com.inz.z.note_book.view.fragment.adapter.LogOperationRvAdapter
import com.inz.z.note_book.view.fragment.bean.LogOperationSlideTableBean
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers

/**
 * 操作日志界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/02 11:53.
 */
class LogOperationFragment : AbsBaseFragment() {
    companion object {
        private const val TAG = "LogOperationFragment"

        fun getInstant(): LogOperationFragment {
            val fragment = LogOperationFragment()
            return fragment
        }
    }

    private var logOperationAdapter: LogOperationRvAdapter? = null
    private var logOperationHeaderRvAdapter: LogOperationRvAdapter? = null
    private var logOperationRowTitleRvAdapter: LogOperationRowTitleRvAdapter? = null

    private var binding: FragmentLogOperationBinding? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_log_operation
    }

    override fun useViewBinding(): Boolean = true

    override fun getViewBindingView(): View? {
        binding = FragmentLogOperationBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {
        logOperationAdapter = LogOperationRvAdapter(mContext)
        logOperationHeaderRvAdapter = LogOperationRvAdapter(mContext)
        logOperationRowTitleRvAdapter =
            LogOperationRowTitleRvAdapter(mContext, logOperationAdapter)

        val bean = LogOperationSlideTableBean().apply {
            isHeaderRow = true
        }
        logOperationHeaderRvAdapter?.setContentData(mutableListOf(bean))

        binding?.fmLogOperationStv?.apply {
            setRowHeaderAdapter(logOperationHeaderRvAdapter)
            setRowTitleRvAdapter(logOperationRowTitleRvAdapter)
            setRowContentAdapter(logOperationAdapter)
        }

    }

    override fun initData() {
        loadLocalOperationLog(0, 10)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private inner class LogOperationRvAdapterListenerImpl :
        LogOperationRvAdapter.LogOperationRvAdapterListener {

    }


    private fun loadLocalOperationLog(page: Int, pageSize: Int) {
        Observable
            .create(ObservableOnSubscribe<List<LogOperationSlideTableBean>> {
                val list = LogController.query()
                val tableBeanList = ArrayList<LogOperationSlideTableBean>()
                list?.forEach {
                    val bean = LogOperationSlideTableBean()
                    bean.data = it
                    tableBeanList.add(bean)
                }
                it.onNext(tableBeanList)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : DefaultObserver<List<LogOperationSlideTableBean>>() {
                override fun onNext(t: List<LogOperationSlideTableBean>) {
                    logOperationAdapter?.setContentData(t)
                    logOperationRowTitleRvAdapter?.refreshTitleList(t)
                }

                override fun onError(e: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onComplete() {
                    TODO("Not yet implemented")
                }
            })

    }

}