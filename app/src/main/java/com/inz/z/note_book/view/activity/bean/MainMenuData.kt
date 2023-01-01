package com.inz.z.note_book.view.activity.bean

import android.content.Context
import com.inz.z.note_book.R
import com.inz.z.note_book.util.Constants

/**
 *
 *
 * ====================================================
 * Create by 11654 in 2023/1/1 16:24
 */
object MainMenuData {

    val mainMenuData = arrayOf(
        intArrayOf(
            R.string._main_page,
            R.drawable.ic_home_black_24dp,
            Constants.Base.MAIN_MENU_TYPE_HOME
        ),
        intArrayOf(
            R.string._application,
            R.drawable.ic_alarm_add_black_24dp,
            Constants.Base.MAIN_MENU_TYPE_SCHEDULE
        ),
        intArrayOf(
            R.string._schedule,
            R.drawable.ic_android_black_24dp,
            Constants.Base.MAIN_MENU_TYPE_APPLICATION
        ),
        intArrayOf(
            R.string._record,
            R.drawable.ic_baseline_event_note_24,
            Constants.Base.MAIN_MENU_TYPE_RECORD
        ),
        intArrayOf(
            R.string._log,
            R.drawable.ic_baseline_bug_report_24,
            Constants.Base.MAIN_MENU_TYPE_LOG
        ),
        intArrayOf(
            R.string._message,
            R.drawable.ic_baseline_message_24,
            Constants.Base.MAIN_MENU_TYPE_MESSAGE
        ),
        intArrayOf(
            R.string.sys_file_list,
            R.drawable.ic_baseline_equalizer_24,
            Constants.Base.MAIN_MENU_TYPE_SYS_FILE
        ),
        intArrayOf(
            R.string.system_wallpaper,
            R.drawable.ic_baseline_wallpaper_24,
            Constants.Base.MAIN_MENU_TYPE_SYS_WALLPAPER
        ),
        intArrayOf(
            R.string.create_love_panel,
            R.drawable.ic_baseline_wallpaper_24,
            Constants.Base.MAIN_MENU_TYPE_CREATE_DAY_IMAGE
        )
    )


    /**
     * Get Main Menu Data.
     */
    fun getMainMenuData(context: Context): List<MainMenuInfo> {
        val list = mutableListOf<MainMenuInfo>()
        for (intArray in mainMenuData) {
            val info = MainMenuInfo(
                intArray[1],
                context.getString(intArray[0]),
                intArray[2]
            )
            list.add(info)
        }
        return list
    }
}