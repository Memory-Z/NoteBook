package com.inz.z.note_book.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseFragment
import com.inz.z.note_book.R
import com.inz.z.note_book.base.FragmentContentType
import com.inz.z.note_book.base.FragmentContentTypeValue
import com.inz.z.note_book.databinding.ActivityBaseAddContentBinding
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.fragment.add_content.NoteTagFragment

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
        fun getInstanceBundle(@FragmentContentType contentType: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(Constants.FragmentParams.PARAMS_CONTENT_TYPE, contentType)
            return bundle
        }

        /**
         * 启动 添加内容 界面
         * @param activity Activity
         * @param contentType 内容类型
         * @param linkedId 关联 ID
         * @param requestCode 请求Code
         */
        fun startActivityForResult(
            activity: Activity,
            @FragmentContentType contentType: Int,
            linkedId: String?,
            requestCode: Int
        ) {
            val intent = Intent(activity, AddContentActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(Constants.FragmentParams.PARAMS_CONTENT_TYPE, contentType)
            bundle.putString(Constants.FragmentParams.PARAMS_TAG_LINK_ID, linkedId)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private var activityBaseAddContentBinding: ActivityBaseAddContentBinding? = null

    /**
     * 右上角设置按钮文字
     */
    private var finishTextStr = ""

    /**
     * 自定义 Fragment
     */
    var customFragment: AbsBaseFragment? = null

    /**
     * 内容类型
     */
    @FragmentContentType
    private var mContentType: Int = FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_OTHER

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_base_add_content
    }

    override fun useViewBinding(): Boolean = true
    override fun setViewBinding() {
        super.setViewBinding()
        activityBaseAddContentBinding = ActivityBaseAddContentBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }
    }

    override fun initView() {
        setSupportActionBar(activityBaseAddContentBinding?.baseAddContentTopToolbar)
    }

    override fun initData() {
        val bundle = intent?.extras
        bundle?.let {
            mContentType = it.getInt(
                Constants.FragmentParams.PARAMS_CONTENT_TYPE,
                FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_OTHER
            )
        }
        // 切换 显示 内容
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
     * @see FragmentContentTypeValue
     */
    private fun targetContentView(@FragmentContentType contentType: Int) {
        val tag = "ADD_CONTENT_FRAGMENT_$contentType"
        L.i(TAG, "targetContentView: TAG = $tag")
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(tag) as AbsBaseFragment?
        if (fragment == null) {
            fragment = getFragment(contentType)
        }
        if (fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.base_add_content_fl, fragment, tag)
            transaction.commitAllowingStateLoss()
        }

    }

    /**
     * 获取 Fragment
     * @param contentType  内容 类型
     */
    private fun getFragment(@FragmentContentType contentType: Int): AbsBaseFragment? {
        return when (contentType) {
            FragmentContentTypeValue.FRAGMENT_CONTENT_TYPE_NOTE_TAG -> {
                NoteTagFragment.getInstance()
            }
            else -> {
                customFragment
            }
        }
    }

}