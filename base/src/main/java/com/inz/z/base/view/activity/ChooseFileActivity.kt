package com.inz.z.base.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.widget.PopupMenu
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.R
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.view.AbsBaseActivity
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_activity_choose_file.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/08/20 11:31.
 */
class ChooseFileActivity : AbsBaseActivity() {

    companion object {
        const val MODE_LIST = 0x000901
        const val MODE_TABLE = 0x000902

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(activity: AppCompatActivity, requestCode: Int) {
            val intent = Intent(activity, ChooseFileActivity::class.java)
            val bundle = Bundle()
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @IntDef(MODE_LIST, MODE_TABLE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShowMode

    private val permissionArray = arrayListOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionCode = 0xEEE002


    @ShowMode
    private var showMode = MODE_LIST


    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.base_activity_choose_file
    }

    override fun initView() {
        base_choose_file_top_r_more_iv?.setOnClickListener { createMorePopupMenu() }
    }

    override fun initData() {
        if (checkHavePermission()) {
            queryFileList(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    queryFileList(null)
                }
            }
        }
    }

    private fun createMorePopupMenu() {
        if (mContext == null) {
            return
        }
        val moreMenu = PopupMenu(mContext, base_choose_file_top_bnl)
        moreMenu.menuInflater.inflate(R.menu.menu_choose_file, moreMenu.menu)
        val modeMenuItem = moreMenu.menu.findItem(R.id.menu_choose_file_mode_item)
        var modeShowName = mContext.getString(R.string.table_mode)
        if (showMode == MODE_LIST) {
            modeShowName = mContext.getString(R.string.list_mode)
        }
        modeMenuItem.setTitle(modeShowName)
        moreMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_choose_file_mode_item -> {

                    when (showMode) {
                        MODE_LIST -> {
                            layoutManager = LinearLayoutManager(mContext)
                        }
                        MODE_TABLE -> {
                            layoutManager = GridLayoutManager(mContext, 2)
                        }
                    }

                    base_choose_file_content_rv.layoutManager = layoutManager
                }
                else -> {

                }
            }
            return@setOnMenuItemClickListener true
        }
        moreMenu.show()
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun queryFileList(filePath: String?) {
        Observable
            .create(
                ObservableOnSubscribe<MutableList<BaseChooseFileBean>>() {
                    if (TextUtils.isEmpty(filePath)) {
                        val list = ProviderUtil.queryFileListByDir(filePath)
                        it.onNext(list)
                    } else {
                        val list = ProviderUtil.queryFileImageWithContextProvider(mContext)
                        it.onNext(list)
                    }
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                object : DefaultObserver<MutableList<BaseChooseFileBean>>() {
                    override fun onNext(t: MutableList<BaseChooseFileBean>) {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                    }
                }
            )

    }

    /**
     * 检测是否拥有权限
     */
    private fun checkHavePermission(): Boolean {
        val needRequestPermissionArray = arrayListOf<String>()
        for (p in permissionArray) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    p
                ) == PackageManager.PERMISSION_DENIED
            ) {
                needRequestPermissionArray.add(p)
            }
        }
        if (needRequestPermissionArray.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                needRequestPermissionArray.toTypedArray(),
                requestPermissionCode
            )
            return false
        } else {
            return true
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


}