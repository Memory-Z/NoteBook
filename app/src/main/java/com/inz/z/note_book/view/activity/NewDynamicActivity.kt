package com.inz.z.note_book.view.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inz.z.base.entity.BaseChooseFileBean
import com.inz.z.base.entity.Constants
import com.inz.z.base.util.L
import com.inz.z.base.view.activity.ChooseFileActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.bean.response.LoginResponse
import com.inz.z.note_book.http.BaseHttpResponseListener
import com.inz.z.note_book.http.HttpModel
import com.inz.z.note_book.http.observer.MyHttpResponseException
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.activity.adapter.NewDynamicRvAdapter
import com.inz.z.note_book.view.dialog.ChooseImageDialog
import com.inz.z.note_book.view.widget.mdel.DynamicItemDecoration
import kotlinx.android.synthetic.main.activity_new_dynamic.*

/**
 * 新动态  界面 -
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 09:41.
 */
class NewDynamicActivity : BaseNoteActivity() {

    companion object {
        const val TAG = "NewDynamicActivity"
        private const val CHOOSE_IMAGE_REQUEST_CODE = 0x00E0
    }

    private var layoutManager: LinearLayoutManager? = null
    private var dynamicRvAdapter: NewDynamicRvAdapter? = null

    override fun initWindow() {}
    override fun getLayoutId(): Int {
        return R.layout.activity_new_dynamic
    }

    override fun initView() {
        new_dynamic_top_submit_tv.setOnClickListener {
            HttpModel.loginByPwd("", "",
                object : BaseHttpResponseListener<LoginResponse> {
                    override fun onStart() {
                        L.i(TAG, "loginByPwd: onStart: ")
                    }

                    override fun onSuccess(data: LoginResponse) {
                        L.i(TAG, "loginByPwd: onSuccess: $data")
                    }

                    override fun onFailure(e: MyHttpResponseException) {
                        L.i(
                            TAG,
                            "loginByPwd: onFailure ================================  ${e.errorCode}"
                        )
                        L.e(TAG, "loginByPwd: onFailure", e.originThrowable)
                    }

                    override fun onFinish() {
                        L.i(TAG, "loginByPwd: onFinish")
                    }
                }
            )
        }

        setSupportActionBar(new_dynamic_top_btal.toolbar)

        layoutManager = LinearLayoutManager(mContext).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
        }
        dynamicRvAdapter = NewDynamicRvAdapter(mContext).apply {
            this.listener = DynamicRvAdapterListenerImpl()
        }
        new_dynamic_content_rv.apply {
            this.addItemDecoration(DynamicItemDecoration(mContext))
            this.layoutManager = this@NewDynamicActivity.layoutManager
            adapter = this@NewDynamicActivity.dynamicRvAdapter
        }
    }

    override fun initData() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CHOOSE_IMAGE_REQUEST_CODE -> {
                if (resultCode == Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_CODE) {
                    data?.extras?.let {
                        val fileList: List<BaseChooseFileBean>? =
                            it.getParcelableArrayList(Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_LIST_TAG)
                        fileList?.let {
                            addDynamicImageList(it)
                        }
                        val fileSize =
                            it.getInt(Constants.ChooseFileConstants.CHOOSE_FILE_RESULT_SIZE_TAG, 0)
                        L.i(
                            TAG,
                            "onActivityResult: CHOOSE_IMAGE_REQUEST_CODE: $fileList == $fileSize"
                        )
                    }
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 添加更多文件项
     */
    private fun addDynamicImageList(list: List<BaseChooseFileBean>) {
        dynamicRvAdapter?.loadMoreData(list)
    }

    /**
     * 动态适配器
     */
    private inner class DynamicRvAdapterListenerImpl :
        NewDynamicRvAdapter.NewDynamicRvAdapterListener {
        override fun addItem(v: View?) {
            showChoseImageDialog()
        }

        override fun removeItem(v: View?, position: Int) {
            dynamicRvAdapter?.removeItem(position)
        }
    }

    /**
     * 显示选择图片弹窗
     */
    private fun showChoseImageDialog() {
        if (mContext == null) {
            L.w(TAG, "showChoseImageDialog: mContext is null. ")
            return
        }
        val manager = supportFragmentManager
        var chooseImageDialog = manager.findFragmentByTag("ChooseImageDialog") as ChooseImageDialog?
        if (chooseImageDialog == null) {
            chooseImageDialog = ChooseImageDialog.getInstance(CHOOSE_IMAGE_REQUEST_CODE)
        }
        if (!chooseImageDialog.isVisible) {
            chooseImageDialog.show(manager, "ChooseImageDialog")
        }
    }
}