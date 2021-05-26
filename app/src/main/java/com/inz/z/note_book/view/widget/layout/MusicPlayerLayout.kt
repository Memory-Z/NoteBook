package com.inz.z.note_book.view.widget.layout

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.inz.z.base.util.L
import com.inz.z.note_book.R
import com.inz.z.note_book.util.MusicPlayerHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class MusicPlayerLayout : ConstraintLayout, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    companion object {
        private const val TAG = "AudioPlayerLayout"

        private val musicUrlList: MutableList<String> = mutableListOf(
//            "https://webfs.yun.kugou.com/202105251946/897d192d327d1fc0d34ba1674e292783/KGTX/CLTX001/2f24d5394d56483dcd7d63d25e482e64.mp3",
//            "https://webfs.yun.kugou.com/202105251953/5629172245a076a5eda5e176563589ce/KGTX/CLTX001/205f76ea79a7acfac3c814f6ee67ddfb.mp3",
//            "https://webfs.yun.kugou.com/202105251953/d4abfe5e4c0fcc189ece3a262d11f697/KGTX/CLTX001/02f893d078a5f6ea0388fd82fb44e40e.mp3",
//            "https://webfs.yun.kugou.com/202105251947/1ee385843fb550bdc329f055d8b3dd80/part/0/960115/KGTX/CLTX001/2ff4014692ac079a9b8118966c891897.mp3",
//            "https://webfs.yun.kugou.com/202105251950/a1d7a80bae9a737016fe8d7e57fd70f1/part/0/960083/G185/M0B/0C/13/-Q0DAF5Nfl-AGwcXADaWAFwMkd8892.mp3",
//            "https://webfs.yun.kugou.com/202105251956/06d6263f3e3fa10d33207f82c009f61c/part/0/960083/G190/M08/1E/11/_g0DAF5OFGeAaInOAEolWWFdwH4496.mp3",
            "https://mm1.doubanio.com/202105261934/2ef1f65255fc136e26e8a0b8cca156f2/view/musicianmp3/mp3/x19676345.mp3",
            "https://mm1.doubanio.com/202105261937/e5e79651dff1160f4b311ed7f00b4375/view/musicianmp3/mp3/x19676478.mp3",
            "http://mx.djkk.com/mix/2019/2019-3/2019-3-27/20193279936.m4a",
            "https://webfs.yun.kugou.com/202105251950/a1d7a80bae9a737016fe8d7e57fd70f1/part/0/960083/G185/M0B/0C/13/-Q0DAF5Nfl-AGwcXADaWAFwMkd8892.mp3",
            "https://mm1.doubanio.com/202105261937/a603d691f4e43e236287ec3af9e19cb9/view/musicianmp3/mp3/x19676208.mp3",
            "https://mm1.doubanio.com/202105261937/488652ae1a30ff98384394028596ff0f/view/musicianmp3/mp3/x19675222.mp3",
            "https://mm1.doubanio.com/202105261937/e9b667a8bdeedf0d04e1f7fbcd6b8fff/view/musicianmp3/mp3/x19677642.mp3",
            "https://mm1.doubanio.com/202105261937/a7b002190c132ae6e7e2717724662af1/view/musicianmp3/mp3/x19675620.mp3",
            "https://mm1.doubanio.com/202105261937/c55e43452240098d0faa8b9e0f61311a/view/musicianmp3/mp3/x19676806.mp3",
            "https://mm1.doubanio.com/202105261937/a55b9a3a17f21f92f63c188b64df37a5/view/musicianmp3/mp3/x19676345.mp3"
        )

        private const val WHAT_STATUS = 0xA0
        private const val WHAT_PROGRESS = 0xA1
        private const val FLAG_PLAY_STATUS = "playStatus"
        private const val FLAG_PLAY_PROGRESS = "playProgress"
        private const val FLAG_PLAY_DURATION = "playDuration"

    }

    /**
     * 是否触控滑动.
     */
    private val isTrackTouch: AtomicBoolean = AtomicBoolean(false)

    private var mContext: Context? = null
    private var mView: View? = null
    private var playIv: ImageView? = null
    private var previousIv: ImageView? = null
    private var nextIv: ImageView? = null
    private var progressTv: TextView? = null
    private var durationTv: TextView? = null
    private var seekBar: SeekBar? = null

    private var musicHandler: Handler? = null
    private var timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    /**
     * 播放监听.
     */
    private var musicPlayerHelperListener: MusicPlayerHelper.MusicPlayerHelperListener? = null

    /**
     * 播放状态.
     */
    @MusicPlayerHelper.MusicPlayerStatus
    private var musicPlayStatus: Int = MusicPlayerHelper.STATUS_INIT

    /**
     * Music 播放点击监听。
     */
    var musicPlayerLayoutListener: MusicPlayerLayoutListener? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        this.mContext = context
        initViewStyle(attrs)
        initView()
    }

    /**
     * 初始化 视图样式
     */
    private fun initViewStyle(attrs: AttributeSet?) {

    }

    /**
     * 初始化布局
     */
    private fun initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.layout_music_player, this, true)
            mView?.let {
                seekBar = it.findViewById(R.id.music_player_seek_bar)
                seekBar?.setOnSeekBarChangeListener(this)
                progressTv = it.findViewById(R.id.music_player_seek_progress_tv)
                durationTv = it.findViewById(R.id.music_player_seek_duration_tv)
                playIv = it.findViewById(R.id.music_player_controller_play_iv)
                playIv?.setOnClickListener(this)
                previousIv = it.findViewById(R.id.music_player_controller_previous_iv)
                previousIv?.setOnClickListener(this)
                nextIv = it.findViewById(R.id.music_player_controller_next_iv)
                nextIv?.setOnClickListener(this)
            }
        }
        musicHandler = Handler(Looper.getMainLooper(), MusicHandlerCallback())
        musicPlayerHelperListener = MusicPlayerHelperListenerImpl()
        musicPlayerHelperListener?.let {
            MusicPlayerHelper.addListener(it)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        musicHandler?.removeCallbacksAndMessages(null)
        musicHandler = null
        musicPlayerHelperListener?.let {
            MusicPlayerHelper.removeListener(it)
            musicPlayerHelperListener = null
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // 仅滑动时切换状态。
        if (isTrackTouch.get() && fromUser) {
            MusicPlayerHelper.seekMusic(progress)
//            updateProgress(progress, seekBar?.max ?: 1)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isTrackTouch.set(true)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        isTrackTouch.set(false)
    }

    override fun onClick(v: View?) {
        val vId = v?.id ?: -1
        // 点击播放按钮.
        if (vId == R.id.music_player_controller_play_iv) {
            when (musicPlayStatus) {
//                MusicPlayerHelper.STATUS_INIT -> {
//                    L.i(TAG, "onClick: start loading. ")
//                }
                // 播放中，暂停播放
                MusicPlayerHelper.STATUS_PLAYING -> {
                    MusicPlayerHelper.pauseMusic()
                }
                // 播放停止，开始播放，
                MusicPlayerHelper.STATUS_PREPARED,
                MusicPlayerHelper.STATUS_STOP
                -> {
                    MusicPlayerHelper.playMusic(context)
                }
                // 其他状态，重新加载
                else -> {
                    MusicPlayerHelper.loadAndPlayMusic(context, musicUrlList[2])
                }
            }
        }
    }

    /**
     * 更新进度，
     * @param progress 当前进度
     * @param duration 总长度
     */
    @MainThread
    private fun updateProgress(progress: Int, duration: Int) {
        seekBar?.apply {
            this.max = duration
            this.progress = progress
        }
        progressTv?.text = getTimeStr(progress)
        durationTv?.text = getTimeStr(duration)
    }

    /**
     * 切换播放状态.
     * @param status 播放状态
     * @see MusicPlayerHelper.MusicPlayerStatus
     */
    @MainThread
    private fun targetPlayStatus(@MusicPlayerHelper.MusicPlayerStatus status: Int) {
        playIv?.let {
            it.setImageDrawable(
                ContextCompat.getDrawable(
                    it.context,
                    if (status == MusicPlayerHelper.STATUS_PLAYING)
                        R.drawable.ic_baseline_pause_24
                    else R.drawable.ic_baseline_play_arrow_24
                )
            )
        }
        this.musicPlayStatus = status
    }

    /**
     * 格式化时间，
     * @param value 需要格式化值.
     */
    private fun getTimeStr(value: Int): String = timeFormat.format(value)

    private inner class MusicHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            L.i(TAG, "handleMessage: >>> ${msg.what}")
            val bundle = msg.data
            when (msg.what) {
                WHAT_STATUS -> {
                    val status = bundle.getInt(FLAG_PLAY_STATUS, MusicPlayerHelper.STATUS_INIT)
                    // 更新状态
                    targetPlayStatus(status)
                }
                WHAT_PROGRESS -> {
                    val progress = bundle.getInt(FLAG_PLAY_PROGRESS, -1)
                    val duration = bundle.getInt(FLAG_PLAY_DURATION, -1)
                    // 更新进度
                    updateProgress(progress, duration)
                }
            }
            return false
        }
    }

    /**
     * 播放监听实现.
     */
    private inner class MusicPlayerHelperListenerImpl :
        MusicPlayerHelper.MusicPlayerHelperListener {
        override fun onStateChange(status: Int) {
            val message = Message.obtain()
            val bundle = Bundle()
            bundle.putInt(FLAG_PLAY_STATUS, status)
            message.data = bundle
            message.what = WHAT_STATUS
            musicHandler?.sendMessage(message)
        }

        override fun onProgressChange(progress: Int, duration: Int) {
            val message = Message.obtain()
            val bundle = Bundle()
            bundle.putInt(FLAG_PLAY_DURATION, duration)
            bundle.putInt(FLAG_PLAY_PROGRESS, progress)
            message.data = bundle
            message.what = WHAT_PROGRESS
            musicHandler?.sendMessage(message)
        }

        override fun onPlayFinish() {

        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // 对外方法
    ///////////////////////////////////////////////////////////////////////////

    /* ------------------------------ Listener ----------------------------*/

    /**
     * 播放状态监听.
     */
    interface MusicPlayerLayoutListener {
        /**
         * 播放下一曲
         */
        fun playNextMusic(): String?

        /**
         * 播放上一曲
         */
        fun playPreviousMusic(): String?
    }


    /* ------------------------------ Listener ----------------------------*/
}