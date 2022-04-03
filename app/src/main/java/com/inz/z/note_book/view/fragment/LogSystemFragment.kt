package com.inz.z.note_book.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.FragmentLogSystemBinding
import com.inz.z.note_book.view.fragment.adapter.LogSystemRvAdapter
import com.inz.z.note_book.view.fragment.bean.LogSystemInfo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/29 13:57.
 */
class LogSystemFragment : AbsBaseFragment() {
    companion object {
        private const val TAG = "LogSystemFragment"

        fun getInstant(): LogSystemFragment {
            val fragment = LogSystemFragment()
            val bundle = Bundle()
            fragment.arguments = bundle;
            return fragment
        }

    }

    private var layoutManager: LinearLayoutManager? = null
    private var logSystemAdapter: LogSystemRvAdapter? = null
    private var binding: FragmentLogSystemBinding? = null


    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_log_system
    }

    override fun useViewBinding(): Boolean = true

    override fun getViewBindingView(): View? {
        binding = FragmentLogSystemBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun initView() {
        layoutManager = LinearLayoutManager(mContext)
        logSystemRvAdapterLister = LogSystemRvAdapterListenerImpl()
        logSystemAdapter = LogSystemRvAdapter(mContext)
            .apply {
                listener = logSystemRvAdapterLister
            }
        binding?.fmLogSysRv?.apply {
            this.layoutManager = this@LogSystemFragment.layoutManager
            this.adapter = logSystemAdapter
        }
    }

    override fun initData() {
        loadSystemLog(mContext)
    }

    override fun onDetach() {
        super.onDetach()
        logSystemRvAdapterLister = null
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    /**
     * 加载
     */
    private fun loadSystemLog(mContext: Context) {
        Observable
            .create(ObservableOnSubscribe<List<LogSystemInfo>> {
                val rootLogFilePath = FileUtils.getFileLogPath(mContext)
                val logFileList = FileUtils.getFileListByFilePath(rootLogFilePath)
                val rootCrashFilePath = FileUtils.getFileCrash(mContext)
                val crashFileList = FileUtils.getFileListByFilePath(rootCrashFilePath)
                val infoList = ArrayList<LogSystemInfo>()
                val totalSize = logFileList.size + crashFileList.size
                logFileList.forEachIndexed { index, file ->
                    val info = LogSystemInfo()
                        .apply {
                            fileName = file.name
                            filePath = file.absolutePath
                            this.lastItem = index == totalSize - 1
                            this.loadMore = false
                        }
                    infoList.add(info)
                }
                crashFileList.forEachIndexed { index, file ->
                    val info = LogSystemInfo().apply {
                        fileName = file.name
                        filePath = file.absolutePath
                        this.lastItem = (index + logFileList.size) == totalSize - 1
                        this.loadMore = false
                    }
                    infoList.add(info)
                }
                it.onNext(infoList)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : DefaultObserver<List<LogSystemInfo>>() {

                override fun onNext(t: List<LogSystemInfo>) {
                    L.i(TAG, "onNext: $t")
                    logSystemAdapter?.refreshData(t)
                }

                override fun onError(e: Throwable) {
                    L.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    L.i(TAG, "onComplete ")
                }
            })


    }


    private var logSystemRvAdapterLister: LogSystemRvAdapter.LogSystemRvAdapterListener? = null

    /**
     * 日志适配器监听实现
     */
    private inner class LogSystemRvAdapterListenerImpl :
        LogSystemRvAdapter.LogSystemRvAdapterListener {
        override fun onItemMoreClick(v: View?, position: Int) {

        }
    }
}