package org.petero.droidfish.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receiver for Open Exchange (OEX) chess engines.
 * Preserves original DroidFish OEX functionality for third-party engine APKs.
 */
class OEXEngineReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_ADD = "org.asdfjkl.openexchange.ADD"
        const val ACTION_REMOVE = "org.asdfjkl.openexchange.REMOVE"
        const val EXTRA_ENGINE_NAME = "engine_name"
        const val EXTRA_ENGINE_BINARY = "engine_binary"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ADD -> {
                val name = intent.getStringExtra(EXTRA_ENGINE_NAME)
                val binary = intent.getStringExtra(EXTRA_ENGINE_BINARY)
                Log.d("OEXReceiver", "New OEX engine: $name at $binary")
                storeOEXEngine(context, name, binary)
            }
            ACTION_REMOVE -> {
                val name = intent.getStringExtra(EXTRA_ENGINE_NAME)
                Log.d("OEXReceiver", "OEX engine removed: $name")
                removeOEXEngine(context, name)
            }
        }
    }
    
    private fun storeOEXEngine(context: Context, name: String?, binary: String?) {
        if (name == null || binary == null) return
        val prefs = context.getSharedPreferences("oex_engines", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("engine_${name}_binary", binary)
            .apply()
    }
    
    private fun removeOEXEngine(context: Context, name: String?) {
        if (name == null) return
        val prefs = context.getSharedPreferences("oex_engines", Context.MODE_PRIVATE)
        prefs.edit().remove("engine_${name}_binary").apply()
    }
}
