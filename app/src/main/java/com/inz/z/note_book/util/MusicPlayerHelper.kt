package com.inz.z.note_book.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.Log
import androidx.annotation.IntDef
import com.inz.z.base.util.L
import com.inz.z.base.util.ThreadPoolUtils

/**
 *
 * 音乐播放 工具 .
 * ====================================================
 * Create by 11654 in 2021/5/25 20:59
 */
object MusicPlayerHelper : AudioManager.OnAudioFocusChangeListener {

    private const val TAG = "MusicPlayerHelper"
    private const val DEFAULT_WAIT_CHECK_TIME = 200L

    const val STATUS_INIT = 0x0010
    const val STATUS_PREPARED = 0x0011
    const val STATUS_PLAYING = 0x0012
    const val STATUS_STOP = 0x0013
    const val STATUS_FINISH = 0x0014
    const val STATUS_ERROR = 0x0015

    @IntDef(STATUS_INIT, STATUS_PREPARED, STATUS_PLAYING, STATUS_STOP, STATUS_FINISH, STATUS_ERROR)
    @Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY,
        AnnotationTarget.VALUE_PARAMETER)
    annotation class MusicPlayerStatus {}

    @MusicPlayerStatus
    private var mStatus: Int = STATUS_INIT

    private var mMediaPlayer: MediaPlayer? = null
    private val musicPlayerHelperListenerList: MutableList<MusicPlayerHelperListener> =
        mutableListOf()
    private var mAudioAttributes: AudioAttributes
    private var mAudioFocusRequest: AudioFocusRequest

    // 是否加载后播放.
    private var isLoadAndPlay = false

    init {
        mAudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//            .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
//            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        mAudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(mAudioAttributes)
            .setOnAudioFocusChangeListener(this)
            .setWillPauseWhenDucked(false)
            .build()

    }

    /**
     * 初始化 MediaPlayer.
     * @param context 上下文
     * @param urlStr 链接，
     */
    private fun initMediaPlayer(context: Context, urlStr: String): MediaPlayer? {
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(urlStr)
            mediaPlayer.setAudioAttributes(mAudioAttributes)
            mediaPlayer.setOnBufferingUpdateListener(OnBufferingUpdateListenerImpl(context))
            mediaPlayer.setOnCompletionListener(OnCompletionListenerImpl(context))
            mediaPlayer.setOnPreparedListener(OnPreparedListenerImpl(context))
            mediaPlayer.setOnErrorListener(OnErrorListenerImpl(context))
            mediaPlayer.setOnSeekCompleteListener(OnSeekCompleteListenerImpl())
            mediaPlayer.isLooping = false
            mediaPlayer.prepareAsync()
            return mediaPlayer
//            this.mMediaPlayer = mediaPlayer
        } catch (e: Exception) {
            e.printStackTrace()
//            this.mMediaPlayer = null
        }
        return null
    }

    /**
     * 获取 Audio Manager.
     */
    private fun getAudioManager(context: Context): AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

    /**
     * 请求音频焦点.
     */
    private fun requestAudioFocus(context: Context) {
        Log.i(TAG, "requestAudioFocus: ")
        val audioManager = getAudioManager(context)
        val requestFocusResult = audioManager?.requestAudioFocus(mAudioFocusRequest)
        L.i(TAG, "requestAudioFocus: result = $requestFocusResult")
        if (requestFocusResult == AudioManager.AUDIOFOCUS_GAIN) {
            L.i(TAG, "requestAudioFocus: SUCCESS. ")
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer?.let {
                    it.start()
                    updateStatus(STATUS_PLAYING)
                    // 检查播放进度.
                    checkPlayerProgress()
                }
            } else {
                updateStatus(STATUS_ERROR)
                playFinish(context, false)
            }
        } else {
            L.i(TAG, "requestAudioFocus: FAILURE. ")
            updateStatus(STATUS_ERROR)
        }
    }

    /**
     * 取消音频焦点.
     */
    private fun abandonAudioFocus(context: Context) {
        Log.i(TAG, "abandonAudioFocus: ")
        val audioManager = getAudioManager(context)
        audioManager?.abandonAudioFocusRequest(mAudioFocusRequest)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        L.i(TAG, "onAudioFocusChange: -$focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {

            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                L.i(TAG, "onAudioFocusChange: loss audio focus. ")
                pauseMusic()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {

            }
        }
    }

    /**
     * 检测 播放器 进度.
     */
    private fun checkPlayerProgress() {
        ThreadPoolUtils
            .getScheduleThread(TAG + "_check_player_progress")
            .execute(CheckPlayerProgressRunnable())
    }

    /**
     * 更新状态.
     */
    private fun updateStatus(@MusicPlayerStatus status: Int) {
        synchronized(mStatus) {
            this.mStatus = status
            for (listener in musicPlayerHelperListenerList) {
                listener.onStateChange(mStatus)
            }
        }
    }

    /**
     * 更新进度。
     */
    private fun updateProgress(progress: Int, duration: Int) {
        L.i(TAG, "updateProgress: --> $progress / $duration")
        synchronized(musicPlayerHelperListenerList) {
            for (listener in musicPlayerHelperListenerList) {
                listener.onProgressChange(progress, duration)
            }
        }
    }

    /**
     * 播放结束.
     * @param isSuccess 是否成功.
     */
    private fun playFinish(context: Context, isSuccess: Boolean) {
        L.i(TAG, "playFinish: isSuccess >> $isSuccess")
        releaseMediaPlayer()
        abandonAudioFocus(context)
        for (listener in musicPlayerHelperListenerList) {
            listener.onPlayFinish()
        }
    }

    /**
     * 检测播放器状态线程.
     */
    private class CheckPlayerProgressRunnable : Runnable {
        override fun run() {
            L.i(TAG, "run: --->> CheckProgress> $mStatus ")
            // 延迟 检测 。
            try {
                Thread.sleep(DEFAULT_WAIT_CHECK_TIME)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // 仅播放中进行检查，
            if (mStatus == STATUS_PLAYING) {
                mMediaPlayer?.let {
                    val progress = it.currentPosition
                    val duration = it.duration
                    updateProgress(progress, duration)
                }
                // 检查播放进度
                checkPlayerProgress()
            }
        }
    }

    /**
     * 缓存更新 监听实现.
     */
    private class OnBufferingUpdateListenerImpl(val context: Context) :
        MediaPlayer.OnBufferingUpdateListener {
        override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
            L.i(TAG, "onBufferingUpdate: --> $percent")
        }
    }

    /**
     * 播放完成监听 实现.
     */
    private class OnCompletionListenerImpl(val context: Context) :
        MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer?) {
            L.i(TAG, "onCompletion: --> ")
            updateStatus(STATUS_FINISH)
//            val duration = mp?.duration ?: 1
//            // 防止结束时，当前进度与总进度不符。
//            updateProgress(duration, duration)
            // 播放结束
            playFinish(context, true)
        }
    }

    /**
     * 播放就绪监听 实现.
     */
    private class OnPreparedListenerImpl(val context: Context) :
        MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer?) {
            L.i(TAG, "onPrepared: ")
            updateStatus(STATUS_PREPARED)
            mp?.let {
                updateProgress(0, it.duration)
            }
            // 是否加载后立即播放.
            if (isLoadAndPlay) {
                // 请求播放，
                requestAudioFocus(context)
            }
        }
    }

    /**
     * 播放失败监听实现.
     */
    private class OnErrorListenerImpl(val context: Context) : MediaPlayer.OnErrorListener {
        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
            L.i(TAG, "onError: -- >> $what >> $extra")
            updateStatus(STATUS_ERROR)
            playFinish(context, false)
            return false
        }
    }

    /**
     * 滑块 移动完成监听实现.
     */
    private class OnSeekCompleteListenerImpl : MediaPlayer.OnSeekCompleteListener {
        override fun onSeekComplete(mp: MediaPlayer?) {
            L.i(TAG, "onSeekComplete: ")
        }
    }

    /**
     * 释放 MediaPlayer
     */
    private fun releaseMediaPlayer() {
        L.i(TAG, "releaseMediaPlayer: ")
        this.mMediaPlayer?.release()
        this.mMediaPlayer = null
    }

    /**
     * MediaPlayer 监听
     */
    interface MusicPlayerHelperListener {
        /**
         * 状态变化
         * @param status 状态
         */
        fun onStateChange(@MusicPlayerStatus status: Int)

        /**
         * 进度更新
         * @param progress 当前进度
         * @param duration 总长度
         */
        fun onProgressChange(progress: Int, duration: Int)

        /**
         * 播放结束
         */
        fun onPlayFinish()
    }

    /**
     * 添加监听.
     */
    fun addListener(listener: MusicPlayerHelperListener) {
        if (!musicPlayerHelperListenerList.contains(listener)) {
            musicPlayerHelperListenerList.add(listener)
        }
    }

    /**
     * 移除监听.
     */
    fun removeListener(listener: MusicPlayerHelperListener) {
        musicPlayerHelperListenerList.remove(listener)
    }

    fun loadAndPlayMusic(context: Context, urlStr: String) {
        L.i(TAG, "loadAndPlayMusic: >>> $urlStr <<< ")
        if (TextUtils.isEmpty(urlStr)) {
            L.w(TAG, "loadAndPlayMusic: url str is null. ")
            return
        }
        releaseMediaPlayer()
        updateStatus(STATUS_INIT)
        // 加载后立即播放
        isLoadAndPlay = true
        this.mMediaPlayer = initMediaPlayer(context, urlStr)
        if (this.mMediaPlayer == null) {
            L.w(TAG, "loadAndPlayMusic: init media player failure. ")
            // 初始化失败。
            updateStatus(STATUS_ERROR)
        }
    }

    /**
     * 播放 .
     */
    fun playMusic(context: Context) {
        L.i(TAG, "playMusic: ")
        // 播放不为空，且处于非播放 状态，
        if (this.mMediaPlayer != null && this.mMediaPlayer?.isPlaying != true) {
            requestAudioFocus(context)
        }
    }

    /**
     * 暂停.
     */
    fun pauseMusic() {
        L.i(TAG, "pauseMusic: ")
        if (this.mMediaPlayer != null && this.mMediaPlayer?.isPlaying == true) {
            this.mMediaPlayer?.pause()
            updateStatus(STATUS_STOP)
        }
    }

    /**
     * 滑动
     */
    fun seekMusic(progress: Int) {
        L.i(TAG, "seekMusic: >> $progress")
        this.mMediaPlayer?.seekTo(progress)
    }
}