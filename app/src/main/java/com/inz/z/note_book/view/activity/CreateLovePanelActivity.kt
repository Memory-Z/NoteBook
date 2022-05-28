package com.inz.z.note_book.view.activity

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import androidx.appcompat.widget.ViewUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.inz.z.base.util.FileUtils
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.databinding.ActivityCreateLovePanelBinding
import com.inz.z.note_book.service.CreateLovePanelService
import com.inz.z.note_book.util.ClickUtil
import com.inz.z.note_book.util.ViewUtil
import com.inz.z.note_book.view.BaseNoteActivity
import java.io.File

/**
 * 创建 爱的 画板
 *
 * ====================================================
 * Create by 11654 in 2022/5/26 21:03
 */
class CreateLovePanelActivity : BaseNoteActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "CreateLovePanelActivity"
    }

    private var binding: ActivityCreateLovePanelBinding? = null

    override fun initWindow() {
    }

    override fun getLayoutId(): Int = R.layout.activity_create_love_panel

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun setViewBinding() {
        super.setViewBinding()
        binding = ActivityCreateLovePanelBinding.inflate(layoutInflater)
            .apply {
                setContentView(this.root)
            }
    }

    override fun initView() {
        binding?.let {
            it.createLovePanelCreateBtn.setOnClickListener(this)
            it.createLovePanelLoadBtn.setOnClickListener(this)
            it.createLovePanelDayTv.typeface =
                Typeface.createFromAsset(assets, "fonts/RobotoSlab-Light.ttf")
        }
    }

    override fun initData() {
    }

    override fun onDestroyTask() {
        binding?.createLovePanelDayTv?.typeface = null

        binding = null
        super.onDestroyTask()

    }


    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick(v)) {
            return
        }
        binding?.let { bind ->
            when (v?.id) {
                bind.createLovePanelCreateBtn.id -> {
                    sendToSaveLove()
                }
                bind.createLovePanelLoadBtn.id -> {
                    loadSaveLove()
                }
            }
        }
    }

    private fun sendToSaveLove() {
        val intent = Intent(mContext, CreateLovePanelService::class.java)
        startService(intent)
    }

    private var request = RequestOptions()
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .onlyRetrieveFromCache(false)

    private fun loadSaveLove() {
        var filePath = FileUtils.getExternalDCIMPath(mContext)
        if (filePath == null) {
            filePath = FileUtils.getFileImagePath(mContext)
        }
        val fileName = "TEST1.jpg"
        filePath = filePath + File.separatorChar + fileName
        L.i(TAG, "loadSaveLove: filePath = $filePath ")
        binding?.let {
            Glide.with(mContext).clear(it.createLovePanelCreateIv)
            Glide.with(mContext).load(filePath).apply(request).into(it.createLovePanelCreateIv)
        }
    }
}