package com.inz.z.note_book.view.fragment

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
        fm_sys_file_image_rv?.apply {
            this.adapter = imageAdapter
            this.layoutManager = LinearLayoutManager(mContext)
        }
    }

    override fun initData() {
        loadImageList()
    }

    private fun loadImageList() {
        Observable.create(
            ObservableOnSubscribe<List<LocalImageInfo>> {
                L.i(TAG, "loadImageList: ----${Thread.currentThread().name}")
                val list = LocalMediaHelper.getLocalPicture(mContext, 0)
                it.onNext(list)
            }
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : DefaultObserver<List<LocalImageInfo>>() {
                override fun onNext(t: List<LocalImageInfo>) {
                    L.i(TAG, "loadImageList: ----${Thread.currentThread().name}")
                    imageAdapter?.refreshData(t)
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })
    }
}