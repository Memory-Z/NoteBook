package com.inz.z.note_book.view.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.local.LocalImageInfo
import com.inz.z.note_book.util.LocalMediaHelper
import com.inz.z.note_book.view.fragment.adapter.SysFileImageRvAdapter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sys_file_image.*
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 *
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 15:05.
 */
class SysFileImageFragment : BaseSysFileFragment() {
    companion object {
        private const val TAG = "SysFileImageFragment"
    }

    private val currentPage = AtomicInteger(0)

    override fun getInstance(): BaseSysFileFragment {
        val fragment = SysFileImageFragment()
        return fragment
    }

    private var imageAdapter: SysFileImageRvAdapter? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sys_file_image
    }

    override fun initView() {
        imageAdapter = SysFileImageRvAdapter(mContext)
            .apply {
                this.listener = SysFileImageRvAdapterListenerImpl()
            }
        fm_sys_file_image_rv?.apply {
            this.adapter = imageAdapter
            this.layoutManager = LinearLayoutManager(mContext)
        }
        fm_sys_file_image_srl?.setOnRefreshListener {
            fm_sys_file_image_srl?.isRefreshing = false
            currentPage.set(0)
            loadImageList(currentPage.get())
        }
    }

    override fun initData() {
        loadImageList(currentPage.get())
    }

    private fun loadImageList(page: Int) {
        Observable.create(
            ObservableOnSubscribe<List<LocalImageInfo>> {
                L.i(TAG, "loadImageList: ----${Thread.currentThread().name}")
                val list = LocalMediaHelper.getLocalPicture(mContext, page)
                it.onNext(list)
            }
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : DefaultObserver<List<LocalImageInfo>>() {
                override fun onNext(t: List<LocalImageInfo>) {
                    L.i(TAG, "loadImageList: ----${Thread.currentThread().name}")
                    val p = currentPage.addAndGet(1)
                    if (p > 1) {
                        imageAdapter?.loadMoreData(t.toMutableList())
                    } else {
                        imageAdapter?.refreshData(t)
                    }
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })
    }

    private inner class SysFileImageRvAdapterListenerImpl :
        SysFileImageRvAdapter.SysFileImageRvAdapterListener {
        override fun onLoadMore(v: View?, position: Int) {
            val page = currentPage.get()
            L.i(TAG, "onLoadMore:  ---- $page")
            loadImageList(page)
        }
    }
}