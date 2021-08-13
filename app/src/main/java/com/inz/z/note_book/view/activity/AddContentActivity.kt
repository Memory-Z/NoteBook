package com.inz.z.note_book.view.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.fragment.add_content.NoteTagFragment
import kotlinx.android.synthetic.main.activity_base_add_content.*

/**
 *
 * 基本添加内容 Activity
 * ====================================================
 * Create by 11654 in 2021/4/11 21:12
 */
class AddContentActivity : BaseNoteActivity() {
    // TODO: 2021/4/18 添加内容 Fragment .

    companion object {
        private const val TAG = "AddContentActivity"

        /**
         * 获取Instance Bundle
         */
        fun getInstanceBundle(@ContentType contentType: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(ContentTypeKey, contentType)
            return bundle
        }

        /**
         * 内容类型: 笔记标签
         */
        const val CONTENT_TYPE_NOTE_TAG = 0x20

        /**
         * 内容类型：计划标签
         */
        const val CONTENT_TYPE_SCHEDULE_TAG = 0x21

        /**
         * 内容类型：记录标签
         */
        const val CONTENT_TYPE_RECORD_TAG = 0x22

        /**
         * 内容类型：动态标签
         */
        const val CONTENT_TYPE_DYNAMIC_TAG = 0x23

        /**
         * 内容类型：文件标签
         */
        const val CONTENT_TYPE_FILE_TAG = 0x24

        /**
         * 内容类型：未知（无）
         */
        const val CONTENT_TYPE_OTHER = 0xF0

        @IntDef(CONTENT_TYPE_NOTE_TAG,
            CONTENT_TYPE_SCHEDULE_TAG,
            CONTENT_TYPE_RECORD_TAG,
            CONTENT_TYPE_DYNAMIC_TAG,
            CONTENT_TYPE_FILE_TAG,
            CONTENT_TYPE_OTHER)
        @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
        annotation class ContentType {}

        private const val ContentTypeKey = "contentType"
    }

    /**
     * 右上角设置按钮文字
     */
    private var finishTextStr = ""

    /**
     * 内容类型
     */
    @ContentType
    private var mContentType: Int = CONTENT_TYPE_OTHER

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_base_add_content
    }

    override fun initView() {
        setSupportActionBar(base_add_content_bta_layout.toolbar)
    }

    override fun initData() {
        val bundle = intent?.extras
        bundle?.let {
            mContentType = it.getInt(ContentTypeKey, CONTENT_TYPE_OTHER)
        }
        targetContentView(mContentType)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_content, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val rightFinishItem = menu?.findItem(R.id.menu_add_content_add_item)
        rightFinishItem?.let {
            if (!TextUtils.isEmpty(finishTextStr)) {
                it.title = finishTextStr
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 点击 左上角返回键 ，关闭界面
                finish()
            }
            R.id.menu_add_content_add_item -> {
                // 点击右上角按钮事件
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 替换中间内容
     * @param contentType 内容布局类型
     * @see ContentType
     */
    private fun targetContentView(@ContentType contentType: Int) {
        val tag = "ADD_CONTENT_FRAGMENT_$contentType"
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(tag) as AbsBaseFragment?
        if (fragment == null) {
            fragment = getFragment(contentType)
        }
        if (fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.base_add_content_bat_content_fl, fragment, tag)
            transaction.commitAllowingStateLoss()
        }

    }

    private fun getFragment(@ContentType contentType: Int): AbsBaseFragment? {
        when (contentType) {
            CONTENT_TYPE_NOTE_TAG -> {
                return NoteTagFragment.getInstance()
            }
        }
        return null
    }

}