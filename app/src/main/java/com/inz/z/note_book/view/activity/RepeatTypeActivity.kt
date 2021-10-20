package com.inz.z.note_book.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityChooseRepeatTypeBinding
import com.inz.z.note_book.util.Constants

/**
 * remove: # 自定义重复日期
 *
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/05/18 17:57.
 * <br/>
 * update by laptop 3 in 2021/10/20 22:26
 * 重复类型。
 *
 */
class RepeatTypeActivity : AbsBaseActivity() {
    companion object {
        private const val TAG = "RepeatTypeActivity"
    }

    private var activityChooseRepeatTypeBinding: ActivityChooseRepeatTypeBinding? = null


    override fun initWindow() {

    }

    override fun resetBottomNavigationBar(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_choose_repeat_type
    }

    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        activityChooseRepeatTypeBinding = ActivityChooseRepeatTypeBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }

    }

    override fun initView() {
        activityChooseRepeatTypeBinding?.chooseRepeatTypeToolbar?.let {
            setSupportActionBar(it)
        }

    }

    override fun initData() {
        val bundle = intent?.extras
//        bundle?.let {
//            checkedWeek = it.getIntArray("RepeatDate") ?: checkedWeek
//        }
//
//        repeatDataRvAdapter?.refreshData(checkedWeek.toList())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 点击 返回 键
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 保存选中的日期
     */
    private fun saveCheckedDate() {
        val intent = Intent()
        val bundle = Bundle()
//        bundle.putIntArray("CheckWeek", checkedWeek)
        intent.putExtras(bundle)
        setResult(Constants.CUSTOM_DATE_REQUEST_CODE, intent)
        finish()
    }

}