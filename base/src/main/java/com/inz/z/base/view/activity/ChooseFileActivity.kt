package com.inz.z.base.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.R
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.base.view.activity.adapter.ChooseFileRvAdapter
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

        const val TAG = "ChooseFileActivity"

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(activity: Activity, requestCode: Int) {
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

    private val requestPermissionCode = 0x0002

    private var chooseFileTableRvAdapter: ChooseFileRvAdapter? = null
    private var chooseFileListRvAdapter: ChooseFileRvAdapter? = null


    @ShowMode
    private var showMode = MODE_LIST


    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.base_activity_choose_file
    }

    override fun initView() {
//        base_choose_file_top_r_more_iv?.setOnClickListener { createMorePopupMenu() }
        setSupportActionBar(base_choose_file_top_btal.toolbar)

        mLayoutManager = LinearLayoutManager(mContext)
        chooseFileTableRvAdapter = ChooseFileRvAdapter(mContext, MODE_TABLE)
        chooseFileListRvAdapter = ChooseFileRvAdapter(mContext, MODE_LIST)
        base_choose_file_content_rv.apply {
            this.layoutManager = mLayoutManager
            this.adapter = chooseFileListRvAdapter
        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_choose_file, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        val menuItem = menu.findItem(R.id.menu_choose_file_mode_item)
        var modeShowName = mContext.getString(R.string.list_mode)
        if (showMode == MODE_LIST) {
            modeShowName = mContext.getString(R.string.table_mode)
        }
        menuItem.setTitle(modeShowName)
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_choose_file_mode_item -> {
                var mode = MODE_LIST
                when (showMode) {
                    MODE_LIST -> {
                        mLayoutManager = GridLayoutManager(mContext, 2)
                        mode = MODE_TABLE
                        base_choose_file_content_rv.adapter = chooseFileTableRvAdapter
                    }
                    MODE_TABLE -> {
                        mLayoutManager = LinearLayoutManager(mContext)
                        mode = MODE_LIST
                        base_choose_file_content_rv.adapter = chooseFileListRvAdapter
                    }
                }
                base_choose_file_content_rv.layoutManager = mLayoutManager
                showMode = mode

            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)

    }

    /**
     * 创建弹窗
     */
    private fun createMorePopupMenu() {
        if (mContext == null) {
            return
        }
        val moreMenu = PopupMenu(mContext, base_choose_file_top_btal)
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
                            mLayoutManager = LinearLayoutManager(mContext)
                        }
                        MODE_TABLE -> {
                            mLayoutManager = GridLayoutManager(mContext, 2)
                        }
                    }

                    base_choose_file_content_rv.layoutManager = mLayoutManager
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
                    if (!TextUtils.isEmpty(filePath)) {
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
                        L.i(TAG, "queryFileList ${Thread.currentThread().name} -- ${t.size} -- $t")
                        chooseFileListRvAdapter?.refreshData(t)
                        chooseFileTableRvAdapter?.refreshData(t)
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