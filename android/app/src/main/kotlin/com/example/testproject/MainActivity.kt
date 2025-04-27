package com.example.testproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    companion object {
        var isPlaying = false
        var mediaPlayer: MediaPlayer? = null
        const val CHANNEL = "music_notification"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "MUSIC_CHANNEL"

        fun updateNotification(context: Context, title: String, artist: String) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val playPauseAction = if (isPlaying) {
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    getPendingIntent(context, "pause")
                )
            } else {
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Play",
                    getPendingIntent(context, "play")
                )
            }

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
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

        private fun getPendingIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, MusicActionReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun playMusic(context: Context) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.song) // <-- Your song.mp3 here
                mediaPlayer?.isLooping = true
            }
            mediaPlayer?.start()
        }

        fun pauseMusic() {
            mediaPlayer?.pause()
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "showNotification" -> {
                    val title = call.argument<String>("title") ?: "Unknown Title"
                    val artist = call.argument<String>("artist") ?: "Unknown Artist"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                            val channel = NotificationChannel(
                                NOTIFICATION_CHANNEL_ID,
                                "Music Playback",
                                NotificationManager.IMPORTANCE_LOW
                            )
                            notificationManager.createNotificationChannel(channel)
                        }
                    }

                    updateNotification(this, title, artist)
                    result.success(null)
                }
                "cancelNotification" -> {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(NOTIFICATION_ID)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }
}
