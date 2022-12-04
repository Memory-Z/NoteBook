package com.inz.z.note_book.view.activity

import android.view.MenuItem
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityBalanceBinding
import com.inz.z.note_book.view.BaseNoteActivity

/**
 * 余额 界面
 * ====================================================
 * Create by 11654 in 2022/8/11 18:41
 */
class BalanceActivity : BaseNoteActivity() {

    companion object {
        private const val TAG = "BalanceActivity"
    }

    private var binding: ActivityBalanceBinding? = null

    override fun initWindow() {
    }

    override fun useViewBinding(): Boolean = true
    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityBalanceBinding.inflate(layoutInflater)
            .apply {
                setContentView(root)
            }
    }

    override fun getLayoutId(): Int = R.layout.activity_balance

    override fun initView() {
        binding?.let {
            setSupportActionBar(it.toolbarBalanceTop)
        }
    }

    override fun initData() {
        // TODO: 2022/8/11 Update Balance.
    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // back
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}