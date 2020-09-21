package com.inz.z.note_book.database.controller

import androidx.annotation.NonNull
import com.inz.z.note_book.database.SearchContentInfoDao
import com.inz.z.note_book.database.bean.SearchContentInfo
import com.inz.z.note_book.database.util.GreenDaoHelper
import java.util.*

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/16 10:03.
 */
object SearchContentInfoController {

    private fun getSearchContentDao(): SearchContentInfoDao? {
        return GreenDaoHelper.getInstance().getDaoSession()?.searchContentInfoDao
    }

    /**
     * 添加搜索内容
     */
    fun insertSearchContent(@NonNull searchContentInfo: SearchContentInfo) {
        getSearchContentDao()?.apply {
            val info = findById(searchContentInfo.id)
            if (info == null) {
                insert(searchContentInfo)
                LogController.log("INSERT", searchContentInfo, "添加查询内容", this.tablename)
            } else {
                updateSearchContent(searchContentInfo)
            }
        }
    }

    /**
     * 更新搜索内容
     */
    fun updateSearchContent(@NonNull searchContentInfo: SearchContentInfo) {
        getSearchContentDao()?.apply {
            val info = findById(searchContentInfo.id)
            if (info == null) {
                insertSearchContent(searchContentInfo)
            } else {
                update(searchContentInfo)
                LogController.log("UPDATE", searchContentInfo, "更新查询内容", this.tablename)
            }
        }
    }

    /**
     * 通过ID查询
     */
    fun findById(searchContentId: Long): SearchContentInfo? {
        getSearchContentDao()?.apply {
            return this.queryBuilder()
                .where(
                    SearchContentInfoDao.Properties.Id.eq(searchContentId)
                )
                .unique()
        }
        return null
    }

    /**
     * 通过 内容 查询相关内容
     */
    fun findByContentReal(
        searchContent: String,
        @SearchContentInfo.SearchType searchType: Int
    ): SearchContentInfo? {
        getSearchContentDao()?.apply {
            val queryBuilder = this.queryBuilder()
            val list = queryBuilder
                .where(
                    queryBuilder.and(
                        SearchContentInfoDao.Properties.SearchContent.eq(searchContent),
                        SearchContentInfoDao.Properties.SearchType.eq(searchType)
                    )
                )
                .orderDesc(SearchContentInfoDao.Properties.UpdateDate)
                .list()
            return if (list == null || list.size > 0) null else list.get(0)
        }
        return null
    }

    /**
     * 通过 内容 查询相关内容
     */
    fun findByContentLike(
        searchContent: String,
        @SearchContentInfo.SearchType searchType: Int
    ): MutableList<SearchContentInfo>? {
        getSearchContentDao()?.apply {
            val queryBuilder = this.queryBuilder()
            return queryBuilder
                .where(
                    queryBuilder.and(
                        SearchContentInfoDao.Properties.SearchType.eq(searchType),
                        SearchContentInfoDao.Properties.SearchContent.like(searchContent)
                    )
                )
                .orderDesc(SearchContentInfoDao.Properties.UpdateDate)
                .list()
        }
        return null
    }

    /**
     * 获取 查询内容 实际
     */
    fun getSearchContentReal(
        searchContent: String,
        @SearchContentInfo.SearchType searchType: Int
    ): SearchContentInfo {
        var searchContentInfo = findByContentReal(searchContent, searchType)
        val calendar = Calendar.getInstance(Locale.getDefault())
        if (searchContentInfo == null) {
            searchContentInfo = SearchContentInfo()
                .apply {
                    this.createDate = calendar.time
                    this.searchType = searchType
                    this.searchContent = searchContent
                    this.enable = 1
                }
        }
        searchContentInfo.apply {
            this.repeatCount += 1
            this.updateDate = calendar.time
        }
        return searchContentInfo
    }

}