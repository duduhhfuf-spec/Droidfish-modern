package org.petero.droidfish.gamemode

/**
 * Time control system inspired by Chess.com and Lichess.
 * Supports bullet, blitz, rapid, and classical with time + increment.
 */
class TimeControl(
    val initialTimeSeconds: Int,
    val incrementSeconds: Int = 0
) {
    enum class TimeControlType { BULLET, BLITZ, RAPID, CLASSICAL }
    
    val type: TimeControlType
        get() = when {
            initialTimeSeconds < 180 -> TimeControlType.BULLET
            initialTimeSeconds < 600 -> TimeControlType.BLITZ
            initialTimeSeconds < 3600 -> TimeControlType.RAPID
            else -> TimeControlType.CLASSICAL
        }
    
    companion object {
        // Bullet
        val BULLET_1_0 = TimeControl(60, 0)
        val BULLET_1_1 = TimeControl(60, 1)
        val BULLET_2_1 = TimeControl(120, 1)
        // Blitz
        val BLITZ_3_0 = TimeControl(180, 0)
        val BLITZ_3_2 = TimeControl(180, 2)
        val BLITZ_5_0 = TimeControl(300, 0)
        val BLITZ_5_3 = TimeControl(300, 3)
        // Rapid
        val RAPID_10_0 = TimeControl(600, 0)
        val RAPID_10_5 = TimeControl(600, 5)
        val RAPID_15_10 = TimeControl(900, 10)
        val RAPID_30_0 = TimeControl(1800, 0)
        // Classical
        val CLASSICAL_30_20 = TimeControl(1800, 20)
        val CLASSICAL_60_30 = TimeControl(3600, 30)
    }
}

/**
 * Player clock with increment support.
 */
class PlayerClock(private val timeControl: TimeControl) {
    private var remainingTimeMs: Long = timeControl.initialTimeSeconds * 1000L
    private var lastUpdateTime: Long = 0
    private var isRunning: Boolean = false
    
    fun start() {
        if (!isRunning) {
            lastUpdateTime = System.currentTimeMillis()
            isRunning = true
        }
    }
    
    fun stop() {
        if (isRunning) {
            updateRemainingTime()
            isRunning = false
        }
    }
    
    fun onMoveMade() {
        if (isRunning) updateRemainingTime()
        remainingTimeMs += timeControl.incrementSeconds * 1000L
    }
    
    private fun updateRemainingTime() {
        if (isRunning) {
            val now = System.currentTimeMillis()
            remainingTimeMs -= (now - lastUpdateTime)
            lastUpdateTime = now
        }
    }
    
    fun getRemainingTimeMs(): Long {
        if (isRunning) updateRemainingTime()
        return remainingTimeMs
    }
    
    fun isTimeOut(): Boolean = getRemainingTimeMs() <= 0
    
    fun getFormattedTime(): String {
        val totalSeconds = (getRemainingTimeMs() / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    fun reset() {
        remainingTimeMs = timeControl.initialTimeSeconds * 1000L
        isRunning = false
    }
}

/**
 * Manages both white and black clocks.
 */
class GameClockManager(timeControl: TimeControl) {
    val whiteClock = PlayerClock(timeControl)
    val blackClock = PlayerClock(timeControl)
    private var whiteToMove = true
    private var gameStarted = false
    
    fun startGame() {
        gameStarted = true
        if (whiteToMove) whiteClock.start() else blackClock.start()
    }
    
    fun onMove() {
        if (!gameStarted) return
        if (whiteToMove) {
            whiteClock.onMoveMade(); whiteClock.stop()
            blackClock.start()
        } else {
            blackClock.onMoveMade(); blackClock.stop()
            whiteClock.start()
        }
        whiteToMove = !whiteToMove
    }
    
    fun pauseGame() { whiteClock.stop(); blackClock.stop() }
    fun resumeGame() { if (whiteToMove) whiteClock.start() else blackClock.start() }
    fun reset() { 
        whiteClock.reset(); blackClock.reset()
        whiteToMove = true; gameStarted = false 
    }
    fun isGameOver(): Boolean = whiteClock.isTimeOut() || blackClock.isTimeOut()
}
