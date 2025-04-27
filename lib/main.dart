import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await _requestNotificationPermission();
  runApp(const MyApp());
}

Future<void> _requestNotificationPermission() async {
  var status = await Permission.notification.status;
  if (!status.isGranted) {
    await Permission.notification.request();
  }
}

class MusicNotification {
  static const MethodChannel _channel = MethodChannel('music_notification');

  static Future<void> showNotification(String title, String artist) async {
    try {
      await _channel.invokeMethod('showNotification', {
        'title': title,
        'artist': artist,
      });
    } catch (e) {
      print("Error showing notification: $e");
    }
  }

  static Future<void> cancelNotification() async {
    try {
      await _channel.invokeMethod('cancelNotification');
    } catch (e) {
      print("Error cancelling notification: $e");
    }
  }

  static Future<void> updateNotificationAction(String action) async {
    try {
      await _channel.invokeMethod('updateNotificationAction', {
        'action': action,
      });
    } catch (e) {
      print("Error updating notification action: $e");
    }
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Music Notification Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MusicHomePage(),
    );
  }
}

class MusicHomePage extends StatelessWidget {
  const MusicHomePage({super.key});

  void _showMusicNotification() {
    MusicNotification.showNotification("Awesome Song", "Cool Artist");
  }

  void _cancelMusicNotification() {
    MusicNotification.cancelNotification();
  }

  void _updateNotificationAction(String action) {
    MusicNotification.updateNotificationAction(action);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Music Notification Demo'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: _showMusicNotification,
              child: const Text('Show Music Notification'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _cancelMusicNotification,
              child: const Text('Cancel Music Notification'),
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
