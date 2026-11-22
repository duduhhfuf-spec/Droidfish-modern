package org.petero.droidfish.sound

import android.content.Context
import android.media.SoundPool
import org.petero.droidfish.theme.ThemeManager

/**
 * Sound manager for chess move sounds and UI sounds.
 * Supports multiple sound sets like Lichess.
 */
class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()
    
    enum class SoundType(val fileName: String) {
        MOVE("move"), CAPTURE("capture"), CHECK("check"),
        CASTLE("castle"), PROMOTE("promote"),
        GAME_START("game_start"), GAME_END("game_end"),
        LOW_TIME("low_time")
    }
    
    init {
        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(
                android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_GAME)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }
    
    fun loadSounds() {
        soundMap.clear()
        val soundSet = ThemeManager.getSoundSet()
        SoundType.values().forEach { soundType ->
            try {
                val path = "${soundSet.name.lowercase()}/${soundType.fileName}.ogg"
                val fd = context.assets.openFd(path)
                val soundId = soundPool?.load(fd.fileDescriptor, fd.startOffset, fd.length, 1)
                if (soundId != null && soundId > 0) {
                    soundMap[soundType.name] = soundId
                }
                fd.close()
            } catch (e: Exception) {
                // Fallback sounds will be used
            }
        }
    }
    
    fun playSound(soundType: SoundType, volume: Float = 1.0f) {
        val soundId = soundMap[soundType.name] ?: return
        soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
    }
    
    fun playMoveSound(isCapture: Boolean = false) {
        playSound(if (isCapture) SoundType.CAPTURE else SoundType.MOVE)
    }
    
    fun playCheckSound() = playSound(SoundType.CHECK)
    fun playCastleSound() = playSound(SoundType.CASTLE)
    fun playPromoteSound() = playSound(SoundType.PROMOTE)
    fun playGameStartSound() = playSound(SoundType.GAME_START)
    fun playGameEndSound() = playSound(SoundType.GAME_END)
    fun playLowTimeWarning() = playSound(SoundType.LOW_TIME, 0.8f)
    
    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}
