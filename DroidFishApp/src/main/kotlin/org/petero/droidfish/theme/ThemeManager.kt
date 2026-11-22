package org.petero.droidfish.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Theme manager for board themes, piece sets, and sound sets (Lichess-style).
 */
object ThemeManager {
    enum class BoardTheme(val displayName: String) {
        GREEN("Зелёная"), BLUE("Голубая"), BROWN("Коричневая"),
        PURPLE("Фиолетовая"), IC("IC"), WOOD("Дерево"),
        MARBLE("Мрамор"), DARK("Тёмная")
    }
    
    enum class PieceSet(val displayName: String) {
        CBURNITT("Cburnitt"), MERIDA("Merida"), ALPHA("Alpha"),
        CHESSNUT("Chessnut"), LEIPZIG("Leipzig"), PIXEL("Pixel"),
        COMPANION("Companion"), RIOH("Rioh"), KOSAL("Kosal"),
        FANTASY("Fantasy")
    }
    
    enum class SoundSet(val displayName: String) {
        STANDARD("Стандартный"), PIANO("Пианино"), NES("NES"),
        FUTURISTIC("Футуристичный"), LISP("Lisp")
    }
    
    private lateinit var prefs: SharedPreferences
    
    fun initialize(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }
    
    fun getBoardTheme(): BoardTheme {
        val name = prefs.getString("board_theme", BoardTheme.GREEN.name)
        return BoardTheme.values().find { it.name == name } ?: BoardTheme.GREEN
    }
    
    fun setBoardTheme(theme: BoardTheme) {
        prefs.edit().putString("board_theme", theme.name).apply()
    }
    
    fun getPieceSet(): PieceSet {
        val name = prefs.getString("piece_set", PieceSet.CBURNITT.name)
        return PieceSet.values().find { it.name == name } ?: PieceSet.CBURNITT
    }
    
    fun setPieceSet(set: PieceSet) {
        prefs.edit().putString("piece_set", set.name).apply()
    }
    
    fun getSoundSet(): SoundSet {
        val name = prefs.getString("sound_set", SoundSet.STANDARD.name)
        return SoundSet.values().find { it.name == name } ?: SoundSet.STANDARD
    }
    
    fun setSoundSet(set: SoundSet) {
        prefs.edit().putString("sound_set", set.name).apply()
    }
}
