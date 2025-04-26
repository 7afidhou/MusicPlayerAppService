package com.example.testproject.service

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.testproject.R

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel", "Music Service", NotificationManager.IMPORTANCE_LOW
            )
            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val songName = intent.getStringExtra("songName") ?: "Song1.mp3" // Default song
        Log.d("MusicService", "Playing: $songName")

        try {
            // Try to load the song from assets
            val afd = assets.openFd("audios/$songName")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }

            // Start foreground service with notification
            val notification = createNotification(songName)
            startForeground(1, notification)

        } catch (e: Exception) {
            Log.e("MusicService", "Error playing file: $songName", e)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    private fun createNotification(songName: String): Notification {
        val playPauseAction = if (mediaPlayer?.isPlaying == true) {
            // Pause action
            val pauseIntent = Intent(this, MusicService::class.java).apply {
                putExtra("action", "pause")
            }
            PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            // Play action
            val playIntent = Intent(this, MusicService::class.java).apply {
                putExtra("action", "play")
            }
            PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        return NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("Playing: $songName")
            .setContentText("Music is playing in the background")
            .setSmallIcon(R.drawable.ic_music_note) // Add your own icon
            .addAction(R.drawable.ic_play, "Play/Pause", playPauseAction)
            .setOngoing(true)
            .build()
    }
}
