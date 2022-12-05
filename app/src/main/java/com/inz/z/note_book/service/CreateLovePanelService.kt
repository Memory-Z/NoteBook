package com.inz.z.note_book.service

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import com.inz.z.base.util.*
import com.inz.z.note_book.R
import com.inz.z.note_book.service.create_image.CreateDayImageListener
import com.inz.z.note_book.service.create_image.CreateDayImageRunnable
import com.inz.z.note_book.service.create_image.CreateImageRunnable
import com.inz.z.note_book.util.Constants
import java.io.File
import java.util.*
import kotlin.math.min

/**
 * 创建 爱 画板 服务
 *
 * ====================================================
 * Create by 11654 in 2022/5/26 21:11
 */
class CreateLovePanelService : Service() {

    companion object {
        private const val TAG = "CreateLovePanelService"
    }

    private val baseDate: Calendar = Calendar.getInstance(Locale.getDefault())
        .apply { set(2022, 4, 24) }

    /**
     * 是否第一次创建
     */
    private var isFirstCreateImage = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val currentDate = Calendar.getInstance(Locale.getDefault())
        //val num = currentDate.get(Calendar.MILLISECOND) - baseDate.get(Calendar.DATE)
        val num: Int = BaseTools.getDiffDay(currentDate, baseDate)
        L.i(TAG, "onStartCommand: date num = $num --- ${baseDate.get(Calendar.DATE)}")

        // Love Panel
        // createLovePanelImage(num)
        // ------------------------
        // 2022/12/5
        // Day Panel
        createDayImage(num)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        isFirstCreateImage = true
    }

    private fun createLovePanelImage(num: Int) {
        L.i(TAG, "createLovePanelImage: create love panel image ")
        val fileName = baseContext.resources.getString(R.string.love_panel_template).format(num)
        // 检测文件是否存在
        val fileExists = checkFileExists(fileName)
        L.i(TAG, "saveBitmap: fileName = $fileName , exists = $fileExists")
        if (fileExists) {
            L.w(TAG, "createLovePanelImage: file is Exists !!! ")
            val message =
                baseContext.getString(R.string.love_panel_image_is_created_no_duplicate_creation)
            sendCreateLovePanelFailuresBroadcast(message)
            return
        }
        val baseBitmap = BitmapFactory.decodeResource(resources, R.drawable.love_day_0)

        ThreadPoolUtils.getScheduleThread("_create_love_panel").execute(
            CreateImageRunnable(this, baseBitmap, num,
                object : CreateDayImageListener {
                    override fun onSavedBitmap(bitmap: Bitmap) {
                        saveBitmap(bitmap, num, fileName)
                    }
                }
            )
        )
    }

    private fun createDayImage(num: Int) {
        L.i(TAG, "createDayImage: create day image. ")
        val fileName = baseContext.resources.getString(R.string.day_panel_temp).format(num)
        // 检测文件是否存在
        val fileExists = checkFileExists(fileName)
        L.i(TAG, "saveBitmap: fileName = $fileName , exists = $fileExists")
        if (fileExists) {
            L.w(TAG, "createLovePanelImage: file is Exists !!! ")
            val message =
                baseContext.getString(R.string.love_panel_image_is_created_no_duplicate_creation)
            sendCreateLovePanelFailuresBroadcast(message)
            return
        }
        val dayBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_day_unsplash)
        ThreadPoolUtils.getScheduleThread("_create_day_panel").execute(
            CreateDayImageRunnable(this, dayBitmap, num,
                object : CreateDayImageListener {
                    override fun onSavedBitmap(bitmap: Bitmap) {
                        saveBitmap(bitmap, num, fileName)
                    }
                }
            )
        )
    }


    private fun saveBitmap(bitmap: Bitmap, num: Int, fileName: String) {
        // 检测文件是否存在
        val fileExists = checkFileExists(fileName)
        L.i(TAG, "saveBitmap: fileName = $fileName , exists = $fileExists")
        if (fileExists) {
            bitmap.recycle()
            L.w(TAG, "saveBitmap: this file is exists !! ")
            return
        }
        val contentValues = createPictureContentValues(fileName)
        val uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        L.i(TAG, "saveBitmap: uri = $uri ")
        uri?.let {
            val isSaved = ImageUtils.saveBitmap2Uri(bitmap, it, contentResolver)
            if (isSaved) {
                // 更新 媒体库
                contentValues.clear()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.putNull(MediaStore.Images.ImageColumns.DATE_EXPIRES)
                    contentValues.put(MediaStore.Images.ImageColumns.IS_PENDING, 0)
                }
                contentResolver.update(it, contentValues, null, null)

                // 生成小图
                val minFilePath = createMinImage(bitmap, fileName)
                // 发送创建完成广播
                sendCreatedLovePanelBroadcast(num, minFilePath)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    contentResolver.delete(it, null)
                }
                val message =
                    baseContext.getString(R.string.love_panel_image_create_failure_please_retry)
                sendCreateLovePanelFailuresBroadcast(message)
            }
            L.i(TAG, "saveBitmap: isSaved =  $isSaved ")
        }
        bitmap.recycle()
    }

    /**
     * 创建图片信息
     */
    private fun createPictureContentValues(fileName: String): ContentValues {
        val date = BaseTools.getLocalDate()
        val contentValues = ContentValues()
        // 相对路径
        val path = FileUtils.getPictureRelativePath()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, path)
        } else {
            @Suppress("DEPRECATION")
            contentValues.put(MediaStore.Images.ImageColumns.DATA, path)
        }
        // 文件名
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.ImageColumns.TITLE, fileName)
        // 文件类型
        contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
        // 设置时间
        contentValues.put(MediaStore.Images.ImageColumns.DATE_ADDED, date.time / 1000L)
        contentValues.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, date.time / 1000L)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.ImageColumns.IS_PENDING, 1)
        }
        return contentValues
    }


    /**
     * 生成小图
     * @param bitmap 需要生成的原图
     * @param fileName 文件名
     *
     */
    private fun createMinImage(bitmap: Bitmap, fileName: String): String {
        val tempFilePath = FileUtils.getCacheImagePath(baseContext)
        L.i(TAG, "createMinImage: filePath = $tempFilePath , fileName = $fileName")

        val filePath = tempFilePath + File.separatorChar + fileName
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }

        val scaleWidth = 150f / bitmap.width
        val scaleHeight = 150f / bitmap.height
        val scale = min(scaleWidth, scaleHeight)
        val matrix = Matrix()
        matrix.postScale(scale, scale)

        val newBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        val isSaved = ImageUtils.saveBitmap2File(newBitmap, tempFilePath, fileName)
        newBitmap.recycle()
        L.i(TAG, "createMinImage: save min image is success ? $isSaved ")
        return filePath
    }


    /**
     * 检测 文件是否存在
     * @param fileName 文件名
     */
    private fun checkFileExists(fileName: String): Boolean {
        val filePath =
            FileUtils.getPicturesPath() + File.separatorChar + fileName
        val file = File(filePath)
        L.i(TAG, "checkFileExists: filePath = $filePath --- ${file.absolutePath}")
        // 如果文件不存在 删除 相应记录
        if (!file.exists()) {
            ProviderUtil.getUriFromFile(baseContext, file, applicationInfo.packageName)
                ?.let {
                    L.i(TAG, "checkFileExists: delete old uri = $it ")
                    contentResolver.delete(it, null, null)
                }
        }
        return file.exists()
    }

    /**
     * 发送创建 广播
     * @param num 数量
     * @param filePath 文件地址
     */
    private fun sendCreatedLovePanelBroadcast(num: Int, filePath: String) {
        L.i(TAG, "sendCreatedLovePanelBroadcast: num = $num, filePath = $filePath")
        val bundle = Bundle()
        bundle.putInt(Constants.NotificationServiceParams.NOTIFICATION_TAG_TITLE, num)
        bundle.putString(Constants.NotificationServiceParams.NOTIFICATION_TAG_FILE_PATH, filePath)

        val broadcast = Intent()
        broadcast.`package` = packageName
        broadcast.action = Constants.NotificationServiceParams.NOTIFICATION_CREATE_LOVE_PANEL_ACTION
        broadcast.putExtras(bundle)
        sendBroadcast(broadcast)
        L.i(TAG, "sendCreatedLovePanelBroadcast: sending ... ")
    }

    /**
     * 发送失败广播
     */
    private fun sendCreateLovePanelFailuresBroadcast(message: String) {
        if (isFirstCreateImage) {
            isFirstCreateImage = false
            return
        }
        val bundle = Bundle()
        bundle.putString(
            Constants.BaseBroadcastParams.CREATE_LOVE_PANEL_FAILURE_MESSAGE_TAG,
            message
        )
        val broadcast = Intent()
        broadcast.putExtras(bundle)
        broadcast.`package` = packageName
        broadcast.action = Constants.BaseBroadcastParams.CREATE_LOVE_PANEL_FAILURE_ACTION
        sendBroadcast(broadcast)
    }
}