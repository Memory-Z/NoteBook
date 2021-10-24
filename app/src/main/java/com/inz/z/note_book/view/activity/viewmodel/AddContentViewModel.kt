package com.inz.z.note_book.view.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inz.z.note_book.database.bean.TagInfo
import com.inz.z.note_book.database.controller.TagLinkController
import com.inz.z.note_book.database.controller.TagNoteLinkController

/**
 *
 * 添加 内容 ViewModel
 * ====================================================
 * Create by 11654 in 2021/10/24 19:49
 */
class AddContentViewModel : ViewModel() {

    /**
     * 标签 列表
     */
    private var tagInfoListLiveData: MutableLiveData<List<TagInfo>>? = null

    /**
     * 获取 标签 列表
     * @param linkedId 关联 ID
     */
    fun getTagInfoList(linkedId: String?): MutableLiveData<List<TagInfo>>? {
        if (tagInfoListLiveData == null) {
            tagInfoListLiveData = MutableLiveData()
        }
        findTagInfoList(linkedId)
        return tagInfoListLiveData
    }

    fun findTagInfoList(linkedId: String?) {
        if (linkedId != null) {
            val list = TagLinkController.findTagInfoListByScheduleId(linkedId)
            if (list.isNullOrEmpty()) {
                tagInfoListLiveData?.postValue(list)
            }
        } else {
            tagInfoListLiveData?.postValue(null)
        }
    }

}