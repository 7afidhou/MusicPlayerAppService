package com.example.testproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "play" -> {
                MainActivity.isPlaying = true
                MainActivity.playMusic(context)
            }
            "pause" -> {
                MainActivity.isPlaying = false
                MainActivity.pauseMusic()
            }
        }
        MainActivity.updateNotification(context, "4 Chiffres", "Flenn")
    }
}
