package com.inz.z.note_book.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*

/**
 * 文件选择
 *
 * ====================================================
 * Create by 11654 in 2022/5/3 14:08
 */
class PickImageActivityResultContracts : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_PICK)
            .setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            .putExtra(Intent.EXTRA_MIME_TYPES, input)
            .setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (intent == null || resultCode != Activity.RESULT_OK) return null
        return intent.data
    }

    fun getClipDataUris(intent: Intent): List<Uri?> {
        // Use a LinkedHashSet to maintain any ordering that may be
        // present in the ClipData
        val resultSet = LinkedHashSet<Uri?>()
        if (intent.data != null) {
            resultSet.add(intent.data)
        }
        val clipData = intent.clipData
        if (clipData == null && resultSet.isEmpty()) {
            return emptyList()
        } else if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                if (uri != null) {
                    resultSet.add(uri)
                }
            }
        }
        return ArrayList(resultSet)
    }
}