package com.inz.z.note_book.view.activity.bean

import androidx.annotation.DrawableRes
import com.inz.z.note_book.util.Constants

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2023/1/1 15:28
 */
data class MainMenuInfo(@DrawableRes val iconId: Int, val menuTitle: String, @Constants.MainMenuType val menuType: Int)

