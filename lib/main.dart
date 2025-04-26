import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: MusicPlayerScreen());
  }
}

class MusicPlayerScreen extends StatefulWidget {
  @override
  State<MusicPlayerScreen> createState() => _MusicPlayerScreenState();
}

class _MusicPlayerScreenState extends State<MusicPlayerScreen> {
  static const platform = MethodChannel('music_service');

  final List<String> songs = ['Song1.mp3', 'Song2.mp3'];
  int currentIndex = 0;

  Future<void> playMusic(int index) async {
    try {
      await platform.invokeMethod('playMusic', {'filename': songs[index]});
    } on PlatformException catch (e) {
      print("Failed to play music: ${e.message}");
    }
  }

  Future<void> pauseMusic() async {
    try {
      await platform.invokeMethod('pauseMusic');
    } on PlatformException catch (e) {
      print("Failed to pause music: ${e.message}");
    }
  }

  Future<void> stopMusic() async {
    try {
      await platform.invokeMethod('stopMusic');
    } on PlatformException catch (e) {
      print("Failed to stop music: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Music Player')),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text('Now playing: ${songs[currentIndex]}'),
          const SizedBox(height: 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              IconButton(
                icon: const Icon(Icons.skip_previous),
                onPressed: () {
                  setState(() {
                    currentIndex = (currentIndex - 1 + songs.length) % songs.length;
                  });
                  playMusic(currentIndex);
                },
              ),
              IconButton(
                icon: const Icon(Icons.play_arrow),
                onPressed: () => playMusic(currentIndex),
              ),
              IconButton(
                icon: const Icon(Icons.pause),
                onPressed: pauseMusic,
              ),
              IconButton(
                icon: const Icon(Icons.stop),
                onPressed: stopMusic,
              ),
              IconButton(
                icon: const Icon(Icons.skip_next),
                onPressed: () {
                  setState(() {
                    currentIndex = (currentIndex + 1) % songs.length;
                  });
                  playMusic(currentIndex);
                },
              ),
            ],
          )
        ],
      ),
    );
  }
}
