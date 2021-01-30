package com.inz.z.note_book.view.fragment

import android.content.pm.PackageInfo
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.util.L
import com.inz.z.base.util.LauncherHelper
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.view.activity.MainActivityListener
import com.inz.z.note_book.view.adapter.ApplicationListRvAdapter
import com.inz.z.note_book.view.dialog.BaseDialogFragment
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_launcher.*

/**
 * 第三方程序启动
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/12 09:17.
 */
class LauncherApplicationFragment : AbsBaseFragment() {

    companion object {
        private const val TAG = "LauncherApplicationFragment"

        fun getInstant(): LauncherApplicationFragment {
            val fragment = LauncherApplicationFragment()
            return fragment
        }
    }

    private var applicationListRvAdapter: ApplicationListRvAdapter? = null

    val mainListener = MainListenerImpl()

    override fun initWindow() {}
    override fun getLayoutId(): Int {
        return R.layout.fragment_launcher
    }

    override fun initView() {
        applicationListRvAdapter = ApplicationListRvAdapter(mContext)
        applicationListRvAdapter?.apply {
            listener = ApplicationInfoListRvAdapterListenerImpl()
        }

        fm_launcher_rv.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = applicationListRvAdapter
        }

    }

    override fun initData() {
        loadApplicationData()
//        initApplicationList()
    }

    private fun initApplicationList() {
        val packageInfoList: MutableList<PackageInfo> = LauncherHelper.getApplicationList(mContext)
        L.i(TAG, "initApplicationList: ----> $packageInfoList")
        applicationListRvAdapter?.refreshData(packageInfoList)
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

    /**
     * 应用列表适配器 -- 监听实现
     */
    inner class ApplicationInfoListRvAdapterListenerImpl :
        ApplicationListRvAdapter.ApplicationListRvAdapterListener {
        override fun onItemClick(v: View?, position: Int) {
            val packageInfo = applicationListRvAdapter?.list?.get(position)
            if (packageInfo != null) {
                showLauncherDialog(
                    packageInfo.applicationInfo.loadLabel(mContext.packageManager).toString(),
                    packageInfo.packageName
                )
            }
        }
    }

    /**
     * 显示运行程序弹窗
     */
    private fun showLauncherDialog(name: String, packageName: String) {
        if (mContext == null) {
            L.w(TAG, "showLauncherDialog: mContext is null. ")
            return
        }
        val manager = childFragmentManager
        var dialogFragment =
            manager.findFragmentByTag("Launcher_BaseDialogFragment") as BaseDialogFragment?
        if (dialogFragment == null) {
            dialogFragment = BaseDialogFragment.Builder()
                .setCenterMessage(getString(R.string.launcher_application_format).format(name))
                .setLeftButton(
                    getString(R.string.cancel),
                    View.OnClickListener {
                        hideLauncherDialog()
                    }
                )
                .setRightButton(
                    getString(R.string._position),
                    View.OnClickListener {
                        hideLauncherDialog()
                        LauncherHelper.launcherPackageName(mContext, packageName)
                    }
                )
                .build()
        }
        if (!dialogFragment.isAdded && !dialogFragment.isVisible) {
            dialogFragment.show(manager, "Launcher_BaseDialogFragment")
        }
    }

    /**
     * 隐藏运行弹窗
     */
    private fun hideLauncherDialog() {
        if (mContext == null) {
            L.w(TAG, "hideLauncherDialog: mContext is null. ")
            return
        }
        val manager = childFragmentManager
        val dialogFragment =
            manager.findFragmentByTag("Launcher_BaseDialogFragment") as BaseDialogFragment?
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss()
        }
    }

    /**
     * MainActivityListener implement
     */
    class MainListenerImpl : MainActivityListener {
        override fun onSearchSubmit(search: String?) {

        }

        override fun onSearchChange(search: String?) {

        }
    }

}