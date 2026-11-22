package org.petero.droidfish.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.petero.droidfish.R
import org.petero.droidfish.gamemode.GameClockManager
import org.petero.droidfish.gamemode.TimeControl
import org.petero.droidfish.sound.SoundManager
import org.petero.droidfish.theme.ThemeManager

/**
 * Main activity with modern Chess.com-inspired UI.
 * Preserves all original DroidFish engine functionality (UCI, OEX, network engines).
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var txtTopClock: TextView
    private lateinit var txtBottomClock: TextView
    private lateinit var gameClock: GameClockManager
    private lateinit var soundManager: SoundManager
    private var isAnalysisMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        ThemeManager.initialize(this)
        
        txtTopClock = findViewById(R.id.txtTopClock)
        txtBottomClock = findViewById(R.id.txtBottomClock)
        
        soundManager = SoundManager(this)
        soundManager.loadSounds()
        
        startNewGame(TimeControl.RAPID_10_0)
        
        findViewById<View>(R.id.fabNewGame)?.setOnClickListener {
            showTimeControlDialog()
        }
    }
    
    private fun startNewGame(timeControl: TimeControl) {
        gameClock = GameClockManager(timeControl)
        updateClockDisplay()
        gameClock.startGame()
        soundManager.playGameStartSound()
        startClockUpdater()
    }
    
    private fun startClockUpdater() {
        lifecycleScope.launch {
            while (!isFinishing) {
                updateClockDisplay()
                if (gameClock.isGameOver()) {
                    handleTimeOut()
                    break
                }
                kotlinx.coroutines.delay(100)
            }
        }
    }
    
    private fun updateClockDisplay() {
        txtTopClock.text = gameClock.blackClock.getFormattedTime()
        txtBottomClock.text = gameClock.whiteClock.getFormattedTime()
    }
    
    private fun handleTimeOut() {
        gameClock.pauseGame()
        soundManager.playGameEndSound()
        MaterialAlertDialogBuilder(this)
            .setTitle("Игра окончена")
            .setMessage("Время вышло!")
            .setPositiveButton("Новая игра") { _, _ -> showTimeControlDialog() }
            .setNegativeButton("Закрыть", null)
            .show()
    }
    
    fun showTimeControlDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_time_control, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()
        
        dialogView.findViewById<View>(R.id.cardBullet1)?.setOnClickListener {
            startNewGame(TimeControl.BULLET_1_0); dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cardBullet2)?.setOnClickListener {
            startNewGame(TimeControl.BULLET_2_1); dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cardBlitz1)?.setOnClickListener {
            startNewGame(TimeControl.BLITZ_3_0); dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cardBlitz2)?.setOnClickListener {
            startNewGame(TimeControl.BLITZ_5_3); dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cardRapid1)?.setOnClickListener {
            startNewGame(TimeControl.RAPID_10_0); dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cardRapid2)?.setOnClickListener {
            startNewGame(TimeControl.RAPID_15_10); dialog.dismiss()
        }
        
        dialog.show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
