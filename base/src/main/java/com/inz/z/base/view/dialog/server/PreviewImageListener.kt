package com.inz.z.base.view.dialog.server

/**
 *
 * 预览图片监听
 * ====================================================
 * Create by 11654 in 2021/1/30 23:02
 */
interface PreviewImageListener<T> {

    /**
     * 更新数据
     * @param data 数据
     */
    fun updateDataList(data: List<T>)

    /**
     * 更改当前数据项
     * @param position 位置
     */
    fun changeCurrentImage(position: Int)

    /**
     * 加载数据项
     * @param data 数据列表
     */
    fun addData(data: List<T>)

    /**
     * 移除数据项
     * @param data 数据项信息
     */
    fun removeData(data: List<T>)

    /**
     * 移除数据项，
     * @param position 数据项 位置
     */
    fun removeData(position: Int)
}