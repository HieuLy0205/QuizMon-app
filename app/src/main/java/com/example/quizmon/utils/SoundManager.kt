package com.example.quizmon.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.example.quizmon.R

/**
 * Quản lý âm thanh hiệu ứng, nhạc nền và rung cho toàn bộ ứng dụng.
 */
object SoundManager {

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private var click = 0
    private var correct = 0
    private var wrong = 0
    private var bonus = 0
    private var coin = 0
    private var victory = 0
    private var complete = 0
    private var treasure = 0
    private var flipCard = 0
    private var spinWheel = 0
    
    private var isSoundEnabled = true
    private var isMusicEnabled = true
    private var isVibrateEnabled = true
    
    private var currentMusicResId: Int = -1

    fun init(context: Context) {
        if (soundPool != null) return

        val appContext = context.applicationContext
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.let { pool ->
            click = pool.load(appContext, R.raw.click, 1)
            correct = pool.load(appContext, R.raw.correct, 1)
            wrong = pool.load(appContext, R.raw.wrong, 1)
            bonus = pool.load(appContext, R.raw.bonus, 1)
            coin = pool.load(appContext, R.raw.coin, 1)
            victory = pool.load(appContext, R.raw.victory, 1)
            complete = pool.load(appContext, R.raw.complete, 1)
            treasure = pool.load(appContext, R.raw.treasure, 1)
            flipCard = pool.load(appContext, R.raw.flip_card, 1)
            spinWheel = pool.load(appContext, R.raw.spin_wheel, 1)
        }
        
        vibrator = ContextCompat.getSystemService(appContext, Vibrator::class.java)
        
        val prefs = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        isSoundEnabled = prefs.getBoolean("sound", true)
        isMusicEnabled = prefs.getBoolean("music", true)
        isVibrateEnabled = prefs.getBoolean("vibrate", true)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }
    
    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (!enabled) {
            pauseMusic()
        } else {
            resumeMusic()
        }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        isVibrateEnabled = enabled
    }

    fun playClick() {
        play(click)
        vibrate(50)
    }
    
    fun playCorrect() {
        play(correct)
        vibrate(100)
    }
    
    fun playWrong() {
        play(wrong)
        vibrate(300)
    }
    
    fun playBonus() = play(bonus)
    fun playCoin() = play(coin)
    fun playVictory() = play(victory)
    fun playComplete() = play(complete)
    fun playTreasure() = play(treasure)
    fun playFlipCard() = play(flipCard)
    fun playSpinWheel() = play(spinWheel)

    private fun play(id: Int) {
        if (isSoundEnabled && id != 0) {
            soundPool?.play(id, 1f, 1f, 1, 0, 1f)
        }
    }

    private fun vibrate(duration: Long) {
        if (isVibrateEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }
        }
    }

    // --- MUSIC METHODS ---

    fun playMusic(context: Context, resId: Int, isLooping: Boolean = true) {
        if (currentMusicResId == resId) {
            resumeMusic()
            return
        }

        stopMusic()
        
        currentMusicResId = resId
        mediaPlayer = MediaPlayer.create(context.applicationContext, resId)
        mediaPlayer?.isLooping = isLooping
        mediaPlayer?.setVolume(0.4f, 0.4f)
        
        if (isMusicEnabled) {
            mediaPlayer?.start()
        }
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (isMusicEnabled && mediaPlayer != null && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stopMusic() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
        currentMusicResId = -1
    }
}
