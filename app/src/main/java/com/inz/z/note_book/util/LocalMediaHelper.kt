package com.inz.z.note_book.util

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.inz.z.note_book.database.bean.local.LocalImageInfo
import java.util.*

/**
 * 本地媒体文件 Helper .
 * ====================================================
 * Create by 11654 in 2020/12/26 23:02
 */
object LocalMediaHelper {

    private const val TAG = "LocalMediaHelper"

    private val IMAGE_TYPE_LIST = listOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_MODIFIED,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.ALBUM,
        MediaStore.Images.Media.CONTENT_TYPE,
        MediaStore.Images.Media.MIME_TYPE
    )
    private const val LOCAL_LIST_PAGE_SIZE = 20

    /**
     * 获取本地图片列表
     * @param context 上下文
     * @param page 页数
     */
    fun getLocalPicture(context: Context, page: Int): List<LocalImageInfo> {
        Log.i(TAG, "getLocalPicture: ------------------>> $page")


        val offset = page * LOCAL_LIST_PAGE_SIZE
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT $LOCAL_LIST_PAGE_SIZE OFFSET $offset "
        )
        val imageInfoList = mutableListOf<LocalImageInfo>()
        val localTime = Calendar.getInstance(Locale.getDefault())
        cursor?.let {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[0]))
                // get image display  name
                val name = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[1]))
                // get image create date
                val modifiedDate = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[2]))
                // get image path
                val path = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[3]))
                // get image size
                val size = it.getLong(it.getColumnIndex(IMAGE_TYPE_LIST[4]))
//                val album = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[5]))
                val contentType = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[6]))
                val mimeType = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[7]))
                val imageInfo = LocalImageInfo()
                    .apply {
                        localImageId = id
                        localImageName = name
                        localImageDate = modifiedDate
                        localImagePath = path
                        localImageSize = size
                        createDate = localTime.time
                    }

                Log.i(
                    TAG,
                    "getLocalPicture: ---->  ${imageInfo.localImagePath} +CONTENT: $contentType +MIME:  $mimeType"
                )
                imageInfoList.add(imageInfo)
            }
        }
        cursor?.close()
        return imageInfoList
    }


}