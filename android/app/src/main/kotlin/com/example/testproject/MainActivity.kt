package com.example.testproject
import com.example.testproject.service.MusicService
import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import android.util.Log

class MainActivity : FlutterActivity() {
    private val CHANNEL = "music_service"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MethodChannel(flutterEngine?.dartExecutor?.binaryMessenger as io.flutter.plugin.common.BinaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "playMusic" -> {
                        val filename = call.argument<String>("filename")
                        val intent = Intent(this, MusicService::class.java).apply {
                            putExtra("songName", filename)
                        }
                        startService(intent)
                        result.success("Started playing $filename")
                    }
                    "pauseMusic" -> {
                        val intent = Intent(this, MusicService::class.java).apply {
                            putExtra("action", "pause")
                        }
                        startService(intent)
                        result.success("Paused music")
                    }
                    "stopMusic" -> {
                        stopService(Intent(this, MusicService::class.java))
                        result.success("Stopped music")
                    }
                    else -> result.notImplemented()
                }
            }
    }
}
