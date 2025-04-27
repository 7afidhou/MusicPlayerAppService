package com.example.testproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.content.Intent
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "music_notification"
    private val NOTIFICATION_ID = 1
    private val NOTIFICATION_CHANNEL_ID = "MUSIC_CHANNEL"

    private var isPlaying = false

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "showNotification" -> {
                    val title = call.argument<String>("title") ?: "Unknown Title"
                    val artist = call.argument<String>("artist") ?: "Unknown Artist"
                    showMusicNotification(title, artist)
                    result.success(null)
                }
                "cancelNotification" -> {
                    cancelMusicNotification()
                    result.success(null)
                }
                "updateNotificationAction" -> {
                    val action = call.argument<String>("action")
                    updateNotificationAction(action)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun showMusicNotification(title: String, artist: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                getPendingIntent("pause")
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                getPendingIntent("play")
            )
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(playPauseAction)
            .setStyle(MediaStyle().setShowActionsInCompactView(0))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getPendingIntent(action: String) =
        PendingIntent.getBroadcast(this, 0, Intent(action), PendingIntent.FLAG_IMMUTABLE)

    private fun updateNotificationAction(action: String?) {
        if (action == "play") {
            isPlaying = true
        } else if (action == "pause") {
            isPlaying = false
        }

        showMusicNotification("Awesome Song", "Cool Artist")
    }

    private fun cancelMusicNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
