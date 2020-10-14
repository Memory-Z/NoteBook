package com.inz.z.base.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inz.z.base.R
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.BaseChooseFileNavBean
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.FileTypeHelper
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.base.view.activity.adapter.ChooseFileNavRvAdapter
import com.inz.z.base.view.activity.adapter.ChooseFileRvAdapter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_activity_choose_file.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

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

        const val SHOW_TYPE_DIR = 0x000A01
        const val SHOW_TYPE_IMAGE = 0x000A02
        const val SHOW_TYPE_AUDIO = 0x000A03
        const val SHOW_TYPE_VIDEO = 0x000A04

        const val CHOOSE_FILE_RESULT_CODE = 0x010001

        const val CHOOSE_FILE_LIST_TAG = "choosedFileList"
        const val CHOOSE_FILE_SIZE_TAG = "choosedFileSize"


        private const val DEFAULT_MAX_CHOOSE_FILE_COUNT = 10
        private const val DEFAULT_TABLE_COLUMNS = 2

        const val TAG = "ChooseFileActivity"

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(activity: Activity, requestCode: Int) {
            gotoChooseFileActivity(activity, requestCode, MODE_LIST, DEFAULT_TABLE_COLUMNS)
        }

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(
            activity: Activity,
            requestCode: Int,
            @ShowMode showMode: Int,
            tableColumn: Int
        ) {
            gotoChooseFileActivity(activity, requestCode, showMode, SHOW_TYPE_DIR, tableColumn)
        }

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(
            activity: Activity,
            requestCode: Int,
            @ShowMode showMode: Int,
            @ShowType showType: Int,
            tableColumn: Int
        ) {
            gotoChooseFileActivity(
                activity,
                requestCode,
                showMode,
                showType,
                tableColumn,
                DEFAULT_MAX_CHOOSE_FILE_COUNT
            )
        }

        /**
         * 跳转至选择文件界面
         */
        fun gotoChooseFileActivity(
            activity: Activity,
            requestCode: Int,
            @ShowMode showMode: Int,
            @ShowType showType: Int,
            tableColumn: Int,
            maxChooseFileSize: Int
        ) {
            val intent = Intent(activity, ChooseFileActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("showMode", showMode)
            bundle.putInt("showType", showType)
            bundle.putInt("tableColumn", tableColumn)
            bundle.putInt("maxChooseFile", maxChooseFileSize)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @IntDef(MODE_LIST, MODE_TABLE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShowMode

    @IntDef(SHOW_TYPE_DIR, SHOW_TYPE_IMAGE, SHOW_TYPE_AUDIO, SHOW_TYPE_VIDEO)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShowType

    private val permissionArray = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionCode = 0x0002

    private var chooseFileTableRvAdapter: ChooseFileRvAdapter? = null
    private var chooseFileListRvAdapter: ChooseFileRvAdapter? = null

    private var chooseFileNavRvAdapter: ChooseFileNavRvAdapter? = null

    /**
     * 根目录地址
     */
    private var mRootPath = ""

    /**
     * max file size  can choose .
     */
    private var maxChooseFileCount = DEFAULT_MAX_CHOOSE_FILE_COUNT

    @ShowType
    private var showType = SHOW_TYPE_DIR

    @ShowMode
    private var showMode = MODE_LIST

    /**
     * 表单 模式下 显示列数
     */
    private var showTableModeColumn = DEFAULT_TABLE_COLUMNS

    /**
     * 选中的文件列表
     */
    private var chooseFileList: ArrayList<BaseChooseFileBean> = arrayListOf()

    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var navLayoutManager: LinearLayoutManager? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.base_activity_choose_file
    }

    override fun initView() {
//        base_choose_file_top_r_more_iv?.setOnClickListener { createMorePopupMenu() }
        setSupportActionBar(base_choose_file_top_btal.toolbar)

        navLayoutManager = LinearLayoutManager(mContext)
        navLayoutManager?.orientation = LinearLayoutManager.HORIZONTAL
        chooseFileNavRvAdapter = ChooseFileNavRvAdapter(mContext)
        chooseFileNavRvAdapter?.listener = ChooseFileNavRvAdapterListenerImpl()
        base_choose_file_nav_rv?.apply {
            this.layoutManager = navLayoutManager
            this.adapter = chooseFileNavRvAdapter
        }


    }

    override fun initData() {
        val bundle = intent?.extras
        bundle?.let {
            showType = it.getInt("showType", SHOW_TYPE_DIR)
            showMode = it.getInt("showMode", MODE_LIST)
            showTableModeColumn = it.getInt("tableColumn", DEFAULT_TABLE_COLUMNS)
            maxChooseFileCount = it.getInt("maxChooseFile", DEFAULT_MAX_CHOOSE_FILE_COUNT)
        }

        initContentRv()

        updateFileSizeOnView(0L)

        mRootPath = FileUtils.getSDPath()
        addNavBody(mRootPath, getString(R.string.root_directory))
        base_choose_file_content_rv.postDelayed(
            {
                if (checkHavePermission()) {
                    checkQueryType(mRootPath)
                }
            },
            100
        )

    }

    private fun initContentRv() {
        chooseFileTableRvAdapter = ChooseFileRvAdapter(mContext, MODE_TABLE)
        chooseFileTableRvAdapter?.listener = ChooseFileRvAdapterListenerImpl()
        chooseFileListRvAdapter = ChooseFileRvAdapter(mContext, MODE_LIST)
        chooseFileListRvAdapter?.listener = ChooseFileRvAdapterListenerImpl()
        targetContentType()
    }

    private fun targetContentType() {
        when (showMode) {
            MODE_LIST -> {
                mLayoutManager = LinearLayoutManager(mContext)
                base_choose_file_content_rv.apply {
                    this.layoutManager = mLayoutManager
                    this.adapter = chooseFileListRvAdapter
                }
            }
            MODE_TABLE -> {
                mLayoutManager = GridLayoutManager(mContext, showTableModeColumn)
                base_choose_file_content_rv.apply {
                    this.layoutManager = mLayoutManager
                    this.adapter = chooseFileTableRvAdapter
                }
            }
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
                checkQueryType(mRootPath)
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
                showMode = if (showMode == MODE_LIST) MODE_TABLE else MODE_LIST
                targetContentType()
            }
            android.R.id.home -> {
                chooseFileList.clear()
                closeChooseFileView()
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 关闭选择文件界面
     */
    private fun closeChooseFileView() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelableArrayList(CHOOSE_FILE_LIST_TAG, chooseFileList)
        bundle.putInt(CHOOSE_FILE_SIZE_TAG, chooseFileList.size)
        intent.putExtras(bundle)
        setResult(CHOOSE_FILE_RESULT_CODE, intent)
        finish()
    }

    /**
     * 导航栏 适配器监听实现
     */
    private inner class ChooseFileNavRvAdapterListenerImpl :
        ChooseFileNavRvAdapter.ChooseFileNavRvAdapterListener {
        override fun onNavClick(position: Int, v: View?) {
            val bean = chooseFileNavRvAdapter?.list?.get(position)
            bean?.let {
                chooseFileNavRvAdapter?.chooseNav(position)
                checkQueryType(it.path)
            }
        }
    }

    /**
     * 选择文件监听实现
     */
    private inner class ChooseFileRvAdapterListenerImpl :
        ChooseFileRvAdapter.ChooseFileRvAdapterListener {
        override fun addChoseFile(position: Int, view: View) {
            L.i(TAG, "addChoseFile: $position")
            if (chooseFileList.size >= maxChooseFileCount) {
                return
            }
            val bean = chooseFileListRvAdapter?.list?.get(position)
            bean?.let {
                addChooseFileBean(it)
            }
            view.postDelayed({ updateFilePreviewCountOnView(chooseFileList.size) }, 100)
            view.postDelayed({ updateChooseFileSize() }, 100)
        }

        override fun removeChoseFile(position: Int, view: View) {
            L.i(TAG, "removeChoseFile: $position")
            val bean = chooseFileListRvAdapter?.list?.get(position)
            bean?.let {
                removeChooseFileBean(it)
            }
            view.postDelayed({ updateFilePreviewCountOnView(chooseFileList.size) }, 100)
            view.postDelayed({ updateChooseFileSize() }, 100)
        }

        override fun showFullImage(position: Int, view: View) {
            L.i(TAG, "showFullImage: $position")
        }

        override fun openFileDirectory(position: Int, view: View) {
            L.i(TAG, "openFileDirectory : $position")
            var bean: BaseChooseFileBean? = null
            when (showMode) {
                MODE_LIST -> {
                    bean = chooseFileListRvAdapter?.list?.get(position)
                }
                MODE_TABLE -> {
                    bean = chooseFileTableRvAdapter?.list?.get(position)
                }
            }
            bean?.let {
                addNavBody(it.filePath, it.fileName)
                checkQueryType(it.filePath)
            }
        }
    }

    /**
     * 添加导航体
     */
    private fun addNavBody(filePath: String, fileName: String) {
        val bean = BaseChooseFileNavBean()
        bean.title = fileName
        bean.path = filePath
        chooseFileNavRvAdapter?.addChooseFileNav(bean)
    }

    /**
     * 检测 查询类型
     */
    private fun checkQueryType(filePath: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            queryFileList(filePath, showType)
        }
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun queryFileList(filePath: String?, @ShowType showType: Int) {
        Observable
            .create(
                ObservableOnSubscribe<MutableList<BaseChooseFileBean>>() {
                    var list: MutableList<BaseChooseFileBean> = mutableListOf()
                    when (showType) {
                        SHOW_TYPE_DIR -> {
                            list = ProviderUtil.queryFileListByDir(filePath)
                        }
                        SHOW_TYPE_AUDIO -> {
                            list = ProviderUtil.queryFileAudioWithContentProvider(mContext)
                        }
                        SHOW_TYPE_IMAGE -> {
                            list = ProviderUtil.queryFileImageWithContextProvider(mContext)
                        }
                        SHOW_TYPE_VIDEO -> {

                        }
                        else -> {
                            it.onError(IllegalArgumentException("no find $showType this's data list"))
                            return@ObservableOnSubscribe
                        }
                    }

                    list.forEach { listIt ->
                        run checkEach@{
                            chooseFileList.forEach { chooseFileListIt ->
                                if (listIt.filePath.equals(chooseFileListIt.filePath)) {
                                    listIt.checked = true
                                    return@checkEach
                                }
                            }
                        }
                    }
                    val fileTypeHelper = FileTypeHelper(mContext)
                    list.forEach {
                        if (!it.fileIsDirectory) {
                            val file = File(it.filePath)
                            val isImage = fileTypeHelper.isImageWithFile(file)
                            if (isImage) {
                                it.fileType = Constants.FileType.FILE_TYPE_IMAGE
                            }
                        }
                    }
                    it.onNext(list)
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
                        L.e(TAG, "onError: ----> ", e)
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

    /**
     * 更新选中文件大小
     *
     */
    private fun updateChooseFileSize() {
        Observable
            .create(
                ObservableOnSubscribe<Long> {
                    var totalFileSize = 0L
                    L.i(TAG, "setChooseFileSize: size = ${chooseFileList.size}")
                    for (bean in chooseFileList) {
                        val file = File(bean.filePath)
                        totalFileSize += file.length()
                    }
                    it.onNext(totalFileSize)
                }
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                object : DefaultObserver<Long>() {
                    override fun onNext(t: Long) {
                        L.i(TAG, "setChooseFileSize: --- $t")
                        updateFileSizeOnView(t)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                }
            )

    }

    /**
     * 更新选中文件大小 ；
     */
    private fun updateFileSizeOnView(fileSize: Long) {
        mContext?.apply {
            val size = fileSize / 1024.0
            val fileSizeStr = this.getString(R.string.file_size_k_format)
                .format(Locale.getDefault(), String.format("%.2f", size))
            base_choose_file_bottom_total_size_tv?.text = fileSizeStr
        }
    }

    /**
     * 更新文件预览数
     */
    private fun updateFilePreviewCountOnView(chooseFileSize: Int) {
        mContext?.apply {
            val fileSizeStr = if (chooseFileSize == 0) {
                mContext.getString(R.string._preview)
            } else {
                mContext.getString(R.string.preview_format).format(chooseFileSize)
            }
            base_choose_file_bl_preview_tv?.apply {
                if (chooseFileSize == 0) {
                    setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.text_white_50_color
                            )
                        )
                    )
                } else {
                    setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.colorAccent
                            )
                        )
                    )
                }
                text = fileSizeStr
            }
        }
    }

    /**
     * 添加选中文件
     */
    private fun addChooseFileBean(bean: BaseChooseFileBean) {
        var addedFileBeanPosition = -1
        if (chooseFileList.size > 0) {
            for (index in 0..chooseFileList.size - 1) {
                val fileBean = chooseFileList.get(index)
                if (fileBean.filePath.equals(bean.filePath)) {
                    addedFileBeanPosition = index
                    break
                }
            }
        }
        if (addedFileBeanPosition == -1) {
            chooseFileList.add(bean)
        }

    }

    /**
     * 移除选中文件
     */
    private fun removeChooseFileBean(bean: BaseChooseFileBean) {
        var addedFileBeanPosition = -1
        if (chooseFileList.size > 0) {
            for (index in 0..chooseFileList.size - 1) {
                val fileBean = chooseFileList.get(index)
                if (fileBean.filePath.equals(bean.filePath)) {
                    addedFileBeanPosition = index
                    break
                }
            }
        }
        if (addedFileBeanPosition != -1) {
            chooseFileList.removeAt(addedFileBeanPosition)
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////


}