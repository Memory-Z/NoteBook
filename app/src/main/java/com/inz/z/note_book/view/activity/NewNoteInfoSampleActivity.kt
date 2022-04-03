package com.inz.z.note_book.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.NewNoteInfoSampleLayoutBinding
import com.inz.z.note_book.view.BaseNoteActivity
import com.inz.z.note_book.view.app_widget.util.WidgetBroadcastUtil
import com.inz.z.note_book.view.fragment.NoteInfoAddDialogFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

/**
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/11/12 13:46.
 */
class NewNoteInfoSampleActivity : BaseNoteActivity() {

    companion object {
        const val TAG = "NewNoteInfoSampleActivity"
        private const val LAUNCH_TYPE_BASE = 0x11
    }

    /**
     * 当前组 ID
     */
    private var currentGroupId = ""

    /**
     * 启动类型: 0: 普通启动；1：新建笔记弹窗；2：改变笔记组弹窗
     */
    private var launchType: Int = 0

    private var binding: NewNoteInfoSampleLayoutBinding? = null


    override fun initWindow() {

    }

    override fun getLayoutId(): Int {
        return R.layout.new_note_info_sample_layout
    }

    override fun useViewBinding(): Boolean = true

    override fun setViewBinding() {
        super.setViewBinding()
        binding = NewNoteInfoSampleLayoutBinding.inflate(layoutInflater)
            .apply {
                setContentView(root)
            }
    }

    override fun initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.statusBarColor = ContextCompat.getColor(mContext, R.color.card_second_color)

    }


    val checkRunnable = Runnable {
        Thread {
            if (addNoteInfoAddDialogFragment != null) {
                while (addNoteInfoAddDialogFragment!!.isVisible) {
                    try {
                        Thread.sleep(100)
                        L.i(TAG, "---------------")
                    } catch (e: Exception) {
                        L.e(TAG, "Thread sleep. ", e)
                    }
                }
            }
            try {
                Thread.sleep(300)
            } catch (e: Exception) {
                L.e(TAG, "thread sleep . dialog Fragment is gone. ", e)
            }
            runOnUiThread {
                this@NewNoteInfoSampleActivity.finish()
            }
        }.start()
    }


    override fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            val launchTypeInt = bundle.getInt("launchType", 0)
            launchType = launchTypeInt.and(LAUNCH_TYPE_BASE)
            currentGroupId = bundle.getString("groupId", "")

        }
    }

    override fun onResume() {
        super.onResume()
        isSaveStated = false
        val bundle = intent.extras
        if (bundle != null) {
            val launchTypeInt = bundle.getInt("launchType", 0)
            launchType = launchTypeInt.and(LAUNCH_TYPE_BASE)
            when (launchType) {
                0 -> {
                    // 普通模式
                }
                1 -> {
                    // 新建笔记弹窗
                    binding?.newNoteInfoSampleLayoutFl?.postDelayed({
                        showNowAddNoteInfoDialog()
                        binding?.newNoteInfoSampleLayoutFl?.postDelayed(checkRunnable, 100)
                    }, 300)
                }
                2 -> {
                    // 改变分组弹窗
                }
                else -> {
                    // 其他模式启动，
                }
            }
        }
    }

    /**
     * 是否保存了状态
     */
    private var isSaveStated = false

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        isSaveStated = true
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onDestroyTask() {
        super.onDestroyTask()
        binding = null
    }

    private var addNoteInfoAddDialogFragment: NoteInfoAddDialogFragment? = null

    private fun showNowAddNoteInfoDialog() {
        val manager = supportFragmentManager
        addNoteInfoAddDialogFragment =
            manager.findFragmentByTag("NoteInfoAddDialogFragment") as NoteInfoAddDialogFragment?
        if (addNoteInfoAddDialogFragment == null) {
            addNoteInfoAddDialogFragment = NoteInfoAddDialogFragment()
            val bundle = Bundle()
            bundle.putString("groupId", currentGroupId)
            addNoteInfoAddDialogFragment!!.arguments = bundle
            addNoteInfoAddDialogFragment!!.setNoteInfoAddDialogListener(object :
                NoteInfoAddDialogFragment.NoteInfoAddDialogListener {
                override fun onCommitNote(groupId: String) {
                    WidgetBroadcastUtil.updateNoteWidget(mContext)
                }

                override fun onDestroy() {

                }
            })
        }
        if (addNoteInfoAddDialogFragment!!.isAdded) {
            L.w(TAG, "NoteInfoAddDialogFragment is added . don't deal. ")
            return
        }
        if (!isSaveStated) {
            manager.beginTransaction()
                .add(addNoteInfoAddDialogFragment!!, "NoteInfoAddDialogFragment")
                .commitAllowingStateLoss()
        }
    }


}