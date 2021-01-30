package com.inz.z.note_book.view.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.inz.z.base.util.BaseTools
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.database.bean.RecordInfo
import com.inz.z.note_book.database.controller.RecordInfoController
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.activity_new_record.*
import java.util.*

/**
 * 新记录 界面
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/09/11 09:36.
 */
class NewRecordActivity : BaseNoteActivity() {

    companion object {
        private const val TAG = "NewRecordActivity"
    }

    private var recordInfo: RecordInfo? = null

    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_new_record
    }

    override fun initView() {
        new_record_done_fab.setOnClickListener {
            readEditTextContent()
        }
        new_record_content_et.addTextChangedListener(ContentTextWatcher())
    }

    override fun initData() {
        setSupportActionBar(new_record_toolbar)
        val bundle = intent?.extras
        bundle?.let {
            val recordInfoId = it.getString("recordInfoId", "")
            if (!TextUtils.isEmpty(recordInfoId)) {
                recordInfo = RecordInfoController.findById(recordInfoId)
            }
        }
        val calendar = Calendar.getInstance(Locale.getDefault())
        initRecordInfo(calendar)
        new_record_content_et?.setText(recordInfo?.recordContent ?: "")
        new_record_content_title_et?.setText(recordInfo?.recordTitle ?: "")

        new_record_time_content_title_tv?.text = BaseTools.getBaseDateFormat().format(calendar.time)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (checkRecordChange()) {
                    showSaveRecordDialog()
                } else {
                    finish()
                }
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
            if (checkRecordChange()) {
                showSaveRecordDialog()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun initViewModel() {

    }

    /**
     * 初始化记录
     */
    private fun initRecordInfo(calendar: Calendar) {
        if (recordInfo == null) {
            recordInfo = RecordInfo()
            recordInfo?.apply {
                this.id = BaseTools.getUUID()
                this.createDate = calendar.time
                this.updateDate = calendar.time
                this.recordDate = calendar.time
                this.enable = RecordInfo.ENABLE_STATE_USE
            }
        }
    }

    /**
     * 内容监听
     */
    private inner class ContentTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val haveText = s?.length ?: 0 > 0
            new_record_done_fab?.apply {
                if (haveText) {
                    this.show()
                } else {
                    this.hide()
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    /**
     * 读取填写内容
     */
    private fun readEditTextContent() {
        val content = new_record_content_et?.text?.toString() ?: ""
        val title = new_record_content_title_et?.text?.toString() ?: ""
        if (!TextUtils.isEmpty(title)) {
            saveRecordInfo(title, content)
        } else {
            showToast(mContext.getString(R.string.please_input_record_title))
        }
    }


    /**
     * 检测记录是否被修改
     */
    private fun checkRecordChange(): Boolean {
        val title = new_record_content_title_et?.text?.toString() ?: ""
        val content = new_record_content_et?.text?.toString() ?: ""
        recordInfo?.let {
            if (!title.equals(it.recordTitle) || !content.equals(it.recordContent)) {
                return true
            }
        }
        return false
    }

    /**
     * 保存记录信息
     */
    private fun saveRecordInfo(title: String, content: String) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        recordInfo?.apply {
            this.recordTitle = title
            this.recordContent = content
            this.updateDate = calendar.time
            RecordInfoController.insertRecord(this)
            showToast(getString(R.string._save_record_success))
            return
        }
        showToast(getString(R.string._save_record_failure))
    }

    /**
     * 显示保存记录痰喘
     */
    private fun showSaveRecordDialog() {
        if (mContext == null) {
            L.w(TAG, "showSaveDialog: mContext is null. ")
            return
        }
        val fragmentManager = supportFragmentManager
        var saveRecordDialog =
            fragmentManager.findFragmentByTag("saveRecordDialog") as BaseDialogFragment?
        if (saveRecordDialog == null) {
            saveRecordDialog = BaseDialogFragment.Builder()
                .setCenterMessage(getString(R.string.record_info_have_change_do_exit))
                .setLeftButton(
                    getString(R.string._quit),
                    View.OnClickListener {
                        hideSaveRecordDialog()
                        finish()
                    }
                )
                .setRightButton(
                    getString(R.string._save),
                    View.OnClickListener {
                        readEditTextContent()
                        hideSaveRecordDialog()
                    }
                )
                .build()
        }
        if (!saveRecordDialog.isAdded && !saveRecordDialog.isVisible) {
            saveRecordDialog.show(fragmentManager, "saveRecordDialog")
        }
    }

    /**
     * 隐藏保存弹窗
     */
    private fun hideSaveRecordDialog() {
        if (mContext == null) {
            L.w(TAG, "hideSaveRecordDialog: mContext is null. ")
            return
        }
        val saveRecordDialog =
            supportFragmentManager.findFragmentByTag("saveRecordDialog") as BaseDialogFragment?
        saveRecordDialog?.dismissAllowingStateLoss()
    }

}