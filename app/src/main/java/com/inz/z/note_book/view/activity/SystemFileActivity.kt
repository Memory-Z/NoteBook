package com.inz.z.note_book.view.activity

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.fragment.BaseSysFileFragment
import com.inz.z.note_book.view.fragment.SysFileAudioFragment
import com.inz.z.note_book.view.fragment.SysFileImageFragment
import kotlinx.android.synthetic.main.activity_system_file.*
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * 系统文件 界面
 * ===========================================
 * @author Administrator
 * Create by inz. in 2020/12/27 14:35.
 */
class SystemFileActivity : BaseNoteActivity() {

    companion object {
        private const val CONTENT_TYPE_IMAGE = 0x0101
        private const val CONTENT_TYPE_AUDIO = 0x0102
        private const val CONTENT_TYPE_MOVIE = 0x0103


        private val contentFragmentMap: Map<Int, String> =
            mapOf(
                CONTENT_TYPE_IMAGE to "SYS_FILE_IMAGE_FRAGMENT",
                CONTENT_TYPE_AUDIO to "SYS_FILE_AUDIO_FRAGMENT"
            )
    }


    /**
     * 当前界面
     */
    private var currentFragment: BaseSysFileFragment? = null

    /**
     * 显示模式
     */
    private var showList = AtomicBoolean(true)

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class ContentType

    override fun initWindow() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_system_file
    }

    override fun initView() {
        setSupportActionBar(sys_file_top_btal.toolbar)

    }

    override fun initData() {
        targetContentFragment(CONTENT_TYPE_IMAGE)
    }

    /**
     * 切换显示布局内容
     * @param contentType 内容类型
     */
    fun targetContentFragment(@ContentType contentType: Int) {
        // 当前界面 TAG
        val currentFragmentTag = currentFragment?.tag ?: ""
        // 新界面 的 TAG
        val newTag = contentFragmentMap[contentType]
        if (currentFragmentTag.equals(newTag)) {
            // 界面已显示 不用处理
            L.i(TAG, "targetContentFragment: -- $newTag")
            return
        }
        var clazz: Class<out BaseSysFileFragment>? = null
        when (contentType) {
            CONTENT_TYPE_IMAGE -> {
                clazz = SysFileImageFragment::class.java
            }
            CONTENT_TYPE_AUDIO -> {
                clazz = SysFileAudioFragment::class.java
            }
            CONTENT_TYPE_MOVIE -> {

            }
            else -> {

            }
        }
        val manager = supportFragmentManager
        try {
            val instance = clazz?.newInstance()
            instance?.let {
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.sys_file_content_fl, it, newTag)
                transaction.commitAllowingStateLoss()
                currentFragment = it
            }
        } catch (ignore: Exception) {
            L.e(TAG, "targetContentFragment: ", ignore)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_target_view, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.menu_target_view_mode_item)
        item?.let {
            val isList = showList.get()
            it.isChecked = isList
            it.icon = ContextCompat.getDrawable(
                mContext,
                if (isList)
                    R.drawable.ic_baseline_view_list_24
                else
                    R.drawable.ic_baseline_view_module_24
            )

        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_target_view_mode_item) {
            val state = showList.get()
            item.isChecked = !state
            showList.set(!state)
            currentFragment?.targetShowMode(!state)
            invalidateOptionsMenu()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}