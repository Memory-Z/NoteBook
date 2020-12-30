package com.inz.z.note_book.util

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import com.inz.z.base.util.L
import com.inz.z.note_book.database.bean.local.LocalAudioInfo
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
        MediaStore.Images.Media.MIME_TYPE
    )
    const val LOCAL_LIST_PAGE_SIZE = 20

    /**
     * 获取本地图片列表
     * @param context 上下文
     * @param page 页数
     */
    fun getLocalPicture(context: Context, page: Int): List<LocalImageInfo> {
        Log.i(TAG, "getLocalPicture: ------------------>> $page")
        if (!PermissionUtil.checkReadStoragePermission(context)) {
            // not READ_EXTERNAL_STORAGE permission.
            return emptyList()
        }

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
                // get image mime type
                val mimeType = it.getString(it.getColumnIndex(IMAGE_TYPE_LIST[5]))
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
                    "getLocalPicture: ---->  ${imageInfo.localImagePath} +MIME:  $mimeType"
                )
                imageInfoList.add(imageInfo)
            }
        }
        cursor?.close()
        return imageInfoList
    }


    private val AUDIO_TYPE_LIST = listOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.MIME_TYPE

    )

    /**
     * 获取本地 音频数据列表
     * @param context 上下文
     * @param page 页号
     */
    fun getLocalAudioList(context: Context, page: Int): List<LocalAudioInfo> {
        L.i(TAG, "getLocalAudioList: ---- $page")

        if (!PermissionUtil.checkReadStoragePermission(context)) {
            // not  permission.
            return emptyList()
        }
        val offset = page * LOCAL_LIST_PAGE_SIZE
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " LIMIT $LOCAL_LIST_PAGE_SIZE OFFSET $offset "
        )
        val audioList = mutableListOf<LocalAudioInfo>()
        val calendar = Calendar.getInstance(Locale.getDefault())
        cursor?.let {
            while (it.moveToNext()) {
                // get audio id
                val id = it.getString(it.getColumnIndex(AUDIO_TYPE_LIST[0]))
                // get audio name
                val displayName = it.getString(it.getColumnIndex(AUDIO_TYPE_LIST[1]))
                // get audio path
                val path = it.getString(it.getColumnIndex(AUDIO_TYPE_LIST[2]))
                // get audio modified date
                val modifiedDate = it.getLong(it.getColumnIndex(AUDIO_TYPE_LIST[3]))
                // get audio size
                val size = it.getLong(it.getColumnIndex(AUDIO_TYPE_LIST[4]))
                // get audio mime type
                val mimeType = it.getString(it.getColumnIndex(AUDIO_TYPE_LIST[5]))

                val audioInfo = LocalAudioInfo()
                    .apply {
                        this.localAudioId = id
                        this.localAudioName = displayName
                        this.localAudioPath = path
                        this.localAudioModifiedDate = modifiedDate
                        this.localAudioSize = size
                        this.localAudioMimeType = mimeType
                        this.createDate = calendar.time
                    }
                L.i(TAG, "getLocalAudioList: ----> $audioInfo")
                audioList.add(audioInfo)
            }
        }

        cursor?.close()

        return audioList

    }


}