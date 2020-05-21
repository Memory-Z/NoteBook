package com.inz.z.note_book.view.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.adapter.ApplicationListRvAdapter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_launcher.*

/**
 * 启动程序
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 16:48.
 */
class ApplicationListActivity : AbsBaseActivity() {

    companion object {
        private const val TAG = "ApplicationListActivity"
    }

    private var requestCode = 0
    private var applicationListRvAdapter: ApplicationListRvAdapter? = null

    override fun initWindow() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_launcher
    }

    override fun initView() {

        applicationListRvAdapter = ApplicationListRvAdapter(mContext)
        applicationListRvAdapter?.apply {
            listener = ApplicationInfoListRvAdapterListenerImpl()
        }
        fm_launcher_rv?.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = applicationListRvAdapter
        }
    }

    override fun initData() {
        requestCode = intent?.getIntExtra(Constants.APPLICATION_LIST_REQUEST_CODE_FLAG, 0) ?: 0
        loadApplicationData()
    }

    /**
     * 应用列表适配器 -- 监听实现
     */
    inner class ApplicationInfoListRvAdapterListenerImpl :
        ApplicationListRvAdapter.ApplicationListRvAdapterListener {
        override fun onItemClick(v: View?, position: Int) {
            if (requestCode == Constants.APPLICATION_LIST_REQUEST_CODE) {
                val packageInfo = applicationListRvAdapter?.list?.get(position)
                val intent = Intent()
                val bundle = Bundle()
                bundle.putParcelable("PackageInfo", packageInfo)
                intent.putExtras(bundle)

                this@ApplicationListActivity.setResult(
                    Constants.APPLICATION_LIST_REQUEST_CODE,
                    intent
                )
                this@ApplicationListActivity.finish()
            }
        }
    }

    private fun loadApplicationData() {
        L.i(TAG, "loadApplicationData")
        Observable
            .create(ObservableOnSubscribe<List<PackageInfo>> {
                val packageInfoList: MutableList<PackageInfo> =
                    LauncherHelper.getApplicationList(mContext)
                it.onNext(packageInfoList)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : DefaultObserver<List<PackageInfo>>() {
                override fun onComplete() {

                }

                override fun onNext(t: List<PackageInfo>) {
                    applicationListRvAdapter?.refreshData(t)
                }

                override fun onError(e: Throwable) {

                }
            })
    }
}