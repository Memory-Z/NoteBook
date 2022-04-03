package com.inz.z.note_book.view.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.L
import com.inz.z.note_book.database.bean.SearchContentInfo
import com.inz.z.note_book.database.controller.SearchContentInfoController
import com.inz.z.note_book.view.dialog.adapter.RecordSearchRvAdapter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 记录搜索弹窗
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/15 11:37.
 */
class RecordSearchDialog private constructor() : SearchDialog() {

    var recordSearchRvAdapter: RecordSearchRvAdapter? = null

    var publishSubject: PublishSubject<String>? = null

    companion object {
        private const val TAG = "RecordSearchDialog"

        fun getInstance(
            searchContent: String?,
            searchDialogListener: SearchDialogListener?
        ): RecordSearchDialog {
            val dialog = RecordSearchDialog()
            val bundle = Bundle()
            bundle.putString("searchContent", searchContent)
            dialog.arguments = bundle
            dialog.searchDialogListener = searchDialogListener
            return dialog
        }
    }

    override fun initView() {
        super.initView()

        recordSearchRvAdapter = RecordSearchRvAdapter(mContext)
        recordSearchRvAdapter?.recordSearchRvAdapterListener = RecordSearchRvAdapterListenerImpl()

        binding?.dialogSearchContentRv?.apply {
            this.layoutManager = LinearLayoutManager(mContext)
            this.adapter = recordSearchRvAdapter
        }
    }

    override fun initData() {
        super.initData()
        initPublishSub()
    }

    private fun initPublishSub() {
        publishSubject = PublishSubject.create()
        publishSubject?.apply {
            this.debounce(2000, TimeUnit.MILLISECONDS)
//                .filter {
//                    return@filter !TextUtils.isEmpty(it)
//                }
                .switchMap(
                    Function<String, ObservableSource<List<SearchContentInfo>>> {
                        return@Function getSearchObservable(it)
                    }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    object : DefaultObserver<List<SearchContentInfo>>() {
                        override fun onNext(data: List<SearchContentInfo>) {
                            L.i(TAG, "publishSubject: onNext:  $data")
                            recordSearchRvAdapter?.refreshData(data.toMutableList(), searchContent)
                        }

                        override fun onError(e: Throwable) {
                            L.e(TAG, "publishSubject: onError: ", e)
                        }

                        override fun onComplete() {
                            L.i(TAG, "publishSubject: onComplete: ")
                        }
                    }
                )
        }
    }

    override fun afterTextChanged(s: Editable?) {
        super.afterTextChanged(s)
        s?.let {
            if (publishSubject == null || publishSubject?.hasComplete() ?: false) {
                initPublishSub()
            }
            if (!(publishSubject?.hasObservers() ?: false)) {
                initPublishSub()
            }
            // publish subject 对象重置，~
            publishSubject?.onNext(it.toString())
        }
    }

    private var searchContent = ""

    /**
     * 获取查询
     */
    private fun getSearchObservable(searchContent: String?): Observable<List<SearchContentInfo>> {
        return Observable
            .create(
                ObservableOnSubscribe<List<SearchContentInfo>> {
                    this.searchContent = searchContent ?: ""
                    L.i(TAG, "getSearchObservable: ObservableOnSubscribe: ${this.searchContent}")
                    if (!TextUtils.isEmpty(searchContent)) {
                        val searchContentList =
                            SearchContentInfoController.findByContentLike(
                                this.searchContent,
                                SearchContentInfo.SEARCH_TYPE_RECORD
                            )
                        if (searchContentList == null || searchContentList.isEmpty()) {
                            it.onNext(mutableListOf())
                        } else {
                            it.onNext(searchContentList)
                        }
                    } else {
                        it.onNext(mutableListOf())
                    }
                    it.onComplete()
                }
            )
            .subscribeOn(Schedulers.io())


    }

    /**
     * 记录搜索查询 适配器监听
     */
    private inner class RecordSearchRvAdapterListenerImpl :
        RecordSearchRvAdapter.RecordSearchRvAdapterListener {
        override fun onDeleteClick(v: View?, position: Int) {
            val searchContentInfo = recordSearchRvAdapter?.list?.get(position)
            searchContentInfo?.let {
                it.enable = 0
                val calendar = Calendar.getInstance(Locale.getDefault())
                it.updateDate = calendar.time
                SearchContentInfoController.deleteSearchContent(it)
                recordSearchRvAdapter?.apply {
                    list.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }

        override fun onItemClick(v: View?, position: Int) {
            val searchContentInfo = recordSearchRvAdapter?.list?.get(position)
            searchContentInfo?.apply {
                searchDialogListener?.onSearchClick(this.searchContent, v)
                dismissAllowingStateLoss()
            }
        }
    }
}