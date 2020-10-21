package com.inz.z.base.view.activity.viewmodel

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.BasePreviewImageBean
import com.inz.z.base.entity.ChooseFileShowType
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.FileTypeHelper
import com.inz.z.base.util.L
import com.inz.z.base.util.ProviderUtil
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DefaultObserver
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/21 09:05.
 */
class ChooseFileViewModel : ViewModel() {

    companion object {
        const val TAG = "ChooseFileViewModel"
    }

    /**
     * 展示文件列表
     */
    private var fileList: MutableLiveData<MutableList<BaseChooseFileBean>>? = null

    /**
     * 选中文件列表
     */
    private var chooseFileList: MutableLiveData<MutableList<BaseChooseFileBean>>? = null

    /**
     * 获取文件列表
     */
    fun getFileList(): MutableLiveData<MutableList<BaseChooseFileBean>> {
        if (fileList == null) {
            fileList = MutableLiveData()
        }
        // TODO: 2020/10/21 selecte file list
        return fileList!!
    }

    /**
     * 获取选中文件
     */
    fun getChooseFileList(): MutableLiveData<MutableList<BaseChooseFileBean>> {
        if (chooseFileList == null) {
            chooseFileList = MutableLiveData()
        }
        return chooseFileList!!
    }

    /**
     * 查询文件列表
     * @param filePath 文件路径
     * @param showType 文件显示类型
     * @param context 上下文
     */
    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun queryFileList(filePath: String, @ChooseFileShowType showType: Int, context: Context?) {
        Observable
            .create(
                ObservableOnSubscribe<List<BaseChooseFileBean>> {
                    var list: MutableList<BaseChooseFileBean> = mutableListOf()
                    when (showType) {
                        Constants.FileShowType.SHOW_TYPE_DIR -> {
                            list = ProviderUtil.queryFileListByDir(filePath)
                        }
                        Constants.FileShowType.SHOW_TYPE_IMAGE -> {
                            if (context != null) {
                                list = ProviderUtil.queryFileImageWithContextProvider(context)
                            }
                        }
                        Constants.FileShowType.SHOW_TYPE_AUDIO -> {
                            if (context != null) {
                                list = ProviderUtil.queryFileAudioWithContentProvider(context)
                            }
                        }
                        Constants.FileShowType.SHOW_TYPE_VIDEO -> {
                        }
                        else -> {
                            it.onError(IllegalArgumentException("not find path: {$filePath} with {$showType} ,this's data list. "))
                        }
                    }
                    list = resetChooseFileList(list.toList(), chooseFileList?.value).toMutableList()
                    if (context != null) {
                        list = resetImageFileType(context, list.toList()).toMutableList()
                    }
                    it.onNext(list)
                }
            )
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefaultObserver<List<BaseChooseFileBean>>() {
                override fun onNext(t: List<BaseChooseFileBean>) {
                    fileList?.value = t.toMutableList()
                }

                override fun onError(e: Throwable) {
                    L.e(TAG, "queryFileList, ", e)
                }

                override fun onComplete() {
                }
            })


    }

    /**
     * 重置选中文件
     * @param fileList 文件列表
     * @param chooseFileList 选中的文件
     * @return 处理后 的文件列表
     */
    private fun resetChooseFileList(
        fileList: List<BaseChooseFileBean>,
        chooseFileList: List<BaseChooseFileBean>?
    ): List<BaseChooseFileBean> {

        fileList.forEach { file ->
            run chooseFileListEach@{
                chooseFileList?.forEach { chooseFile ->
                    if (chooseFile.filePath.equals(file.filePath)) {
                        file.checked = true
                        return@chooseFileListEach
                    }
                }
            }
        }
        return fileList
    }

    /**
     * 重置图片文件类型
     * @param context 上下文
     * @param fileList 需要处理的文件列表
     * @return 处理后文件
     */
    private fun resetImageFileType(
        context: Context,
        fileList: List<BaseChooseFileBean>
    ): List<BaseChooseFileBean> {
        val fileTypeHelper = FileTypeHelper(context)
        fileList.forEach {
            if (!it.fileIsDirectory) {
                val file = File(it.filePath)
                val isImage = fileTypeHelper.isImageWithFile(file)
                if (isImage) {
                    it.fileType = Constants.FileType.FILE_TYPE_IMAGE
                }
            }
        }
        return fileList
    }

}