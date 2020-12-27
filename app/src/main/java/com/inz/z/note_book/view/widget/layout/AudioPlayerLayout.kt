package com.inz.z.note_book.view.widget.layout

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class AudioPlayerLayout : LinearLayout {

    companion object {
        private const val TAG = "AudioPlayerLayout"

        private const val PLAY_STATE_IDLE = 0x0001
        private const val PLAY_STATE_PLAYING = 0x0002
        private const val PLAY_STATE_PAUSE = 0x0003
        private const val PLAY_STATE_END= 0x0004
    }

    private var mContext: Context? = null
    private var mView: View? = null

    private var mediaPlayer: MediaPlayer? = null

    /**
     * 播放状态
     */
    private var playState: Int = 0

    /**
     * 是否循环播放
     */
    private var isLooper: Boolean = false


    constructor(context: Context?) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initViewStyle()
        initView()

    }

    /**
     * 初始化 视图样式
     */
    private fun initViewStyle() {

    }

    /**
     * 初始化布局
     */
    private fun initView() {

    }

    private fun initMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
            .apply {
                this.isLooping = isLooper
                this.setOnCompletionListener {
                    // TODO: 2020/12/5 play completion
                }
                this.setOnErrorListener { mp, what, extra ->
                    // TODO: 2020/12/5 play error mp what extra
                    return@setOnErrorListener true
                }
                this.setOnPreparedListener {
                    // TODO: 2020/12/5 play is prepared
                }
            }
    }

    /**
     * 准备播放
     */
    private fun prepareMedia(audioSrc: String) {
        if (mediaPlayer == null || mediaPlayer?.isPlaying ?: false) {
            initMediaPlayer()
        }
        mediaPlayer?.apply {
            this.setDataSource(audioSrc)
            this.prepareAsync()
        }
    }

    /**
     * 切换 播放状态
     */
    private fun targetState() {

    }

}