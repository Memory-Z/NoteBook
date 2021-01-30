package com.inz.z.note_book.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.transition.*
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.local.LocalImageInfo
import com.inz.z.note_book.util.PermissionUtil
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.dialog.BaseDialogFragment
import com.inz.z.note_book.view.fragment.BaseSysFileFragment
import com.inz.z.note_book.view.fragment.ImageDetailFragment
import com.inz.z.note_book.view.fragment.SysFileAudioFragment
import com.inz.z.note_book.view.fragment.SysFileImageFragment
import kotlinx.android.synthetic.main.activity_system_file.*
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

        private const val REQUEST_CODE = 0x0A01

        private val contentFragmentMap: Map<Int, String> =
            mapOf(
                CONTENT_TYPE_IMAGE to "SYS_FILE_IMAGE_FRAGMENT",
                CONTENT_TYPE_AUDIO to "SYS_FILE_AUDIO_FRAGMENT"
            )
    }

    private val requestPermissionsArray = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )


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
        // 判断是否有存储读取权限
        if (!PermissionUtil.checkReadStoragePermission(mContext)) {
            showRequestPermissionDialog()
        }
        targetContentFragment(CONTENT_TYPE_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentFragment?.refreshView()
            }
        }
    }

    /**
     * 切换显示布局内容
     * @param contentType 内容类型
     */
    private fun targetContentFragment(@ContentType contentType: Int) {
        // 当前界面 TAG
        val currentFragmentTag = currentFragment?.tag ?: ""
        // 新界面 的 TAG
        val newTag = contentFragmentMap[contentType] ?: "TempBaseSysFragment"
        if (currentFragmentTag.equals(newTag)) {
            // 界面已显示 不用处理
            L.i(TAG, "targetContentFragment: -- $newTag")
            return
        }
        var clazz: Class<out BaseSysFileFragment>? = null
        when (contentType) {
            CONTENT_TYPE_IMAGE -> {
                clazz = SysFileImageFragment::class.java
                val fragment = SysFileImageFragment.getInstance(SysFileImageFragmentListenerImpl())
                targetBaseSysFileFragment(fragment, newTag)
                return
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

    /**
     * 切换显示 界面
     */
    private fun targetBaseSysFileFragment(fragment: BaseSysFileFragment, newTag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.sys_file_content_fl, fragment, newTag)
        transaction.commitAllowingStateLoss()
        currentFragment = fragment
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

    private inner class SysFileImageFragmentListenerImpl :
        SysFileImageFragment.SysFileImageFragmentListener {
        override fun showImageDetail(imageView: ImageView, imageInfo: LocalImageInfo) {
            showImageDetailFragment(imageView, imageInfo)
        }
    }


    /**
     * 显示 图片详情界面
     */
    private fun showImageDetailFragment(@Nullable imageView: ImageView, imageInfo: LocalImageInfo) {
        if (mContext == null) {
            return
        }
        val manager = supportFragmentManager
        var imageDetailFragment =
            manager.findFragmentByTag("ImageDetailFragment") as ImageDetailFragment?
        if (imageDetailFragment == null) {
            imageDetailFragment =
                ImageDetailFragment.getInstance(imageInfo.localImagePath, imageInfo.id)
                    .apply {
                        this.sharedElementEnterTransition = object : TransitionSet() {
                            init {
                                setDuration(1000)
                                setOrdering(ORDERING_TOGETHER)
                                addTransition(ChangeBounds())
                                addTransition(ChangeTransform())
                                addTransition(ChangeImageTransform())
                            }
                        }
                        this.sharedElementReturnTransition = object : TransitionSet() {
                            init {
                                setDuration(1000)
                                setOrdering(ORDERING_TOGETHER)
                                addTransition(ChangeBounds())
                                addTransition(ChangeTransform())
                                addTransition(ChangeImageTransform())
                            }
                        }
                        this.exitTransition = Fade()
                        this.exitTransition = Fade()
                    }
        }
        val tran = manager.beginTransaction()
        if (!imageDetailFragment.isAdded) {
            tran.addSharedElement(imageView, mContext.getString(R.string.base_image))
            tran.add(R.id.sys_file_content_fl, imageDetailFragment, "ImageDetailFragment")
            tran.addToBackStack(null)
        }
//        if (!imageDetailFragment.isVisible) {
//            tran.show(imageDetailFragment)
//        }
        tran.commitAllowingStateLoss()
    }

    /**
     * 显示权限申请弹窗
     */
    private fun showRequestPermissionDialog() {
        if (mContext == null) {
            L.i(TAG, "showRequestPermissionDialog: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var dialog = manager.findFragmentByTag("RequestPermissionDialog") as BaseDialogFragment?
        if (dialog == null) {
            dialog = BaseDialogFragment.Builder()
                .setTitle(mContext.getString(R.string._tips))
                .setCenterMessage(mContext.getString(R.string.request_permission_storage_hint))
                .setLeftButton(
                    mContext.getString(R.string.cancel),
                    View.OnClickListener {
                        showToast(mContext.getString(R.string.permission_to_open))
                        hideRequestPermissionDialog()
                        finish()
                    }
                )
                .setRightButton(
                    mContext.getString(R.string._position),
                    View.OnClickListener {
                        PermissionUtil.requestPermission(
                            this,
                            requestPermissionsArray,
                            REQUEST_CODE
                        )
                        hideRequestPermissionDialog()
                    }
                )
                .build()
        }
        if (!dialog.isVisible) {
            dialog.show(manager, "RequestPermissionDialog")
        }
    }

    /**
     * 隐藏权限申请弹窗
     */
    private fun hideRequestPermissionDialog() {
        if (mContext == null) {
            L.i(TAG, "hideRequestPermissionDialog: mContext is null ")
            return
        }
        val dialog =
            supportFragmentManager.findFragmentByTag("RequestPermissionDialog") as BaseDialogFragment?
        dialog?.dismissAllowingStateLoss()
    }

}