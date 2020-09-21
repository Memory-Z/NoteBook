package com.inz.z.note_book.view.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.dialog_search.*
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

        dialog_search_content_rv.apply {
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
                .filter {
                    return@filter !TextUtils.isEmpty(it)
                }
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
            publishSubject?.apply {
                if (!this.hasObservers()) {
                    initPublishSub()
                }
                this.onNext(it.toString())
            }
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
                    val searchContentList = SearchContentInfoController.findByContentLike(
                        this.searchContent,
                        SearchContentInfo.SEARCH_TYPE_RECORD
                    )
                    if (searchContentList == null || searchContentList.isEmpty()) {
                        it.onError(NoSuchElementException("not find this search content . "))
                    } else {
                        it.onNext(searchContentList)
                    }
                    it.onComplete()
                }
            )
            .subscribeOn(Schedulers.io())


    }
}