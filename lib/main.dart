import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
import 'package:just_audio_background/just_audio_background.dart';
import 'package:audio_session/audio_session.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await JustAudioBackground.init(
    androidNotificationChannelId: 'com.example.testproject.channel.audio',
    androidNotificationChannelName: 'Audio Playback',
    androidNotificationOngoing: true,
  );

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MusicPage(),
    );
  }
}

class MusicPage extends StatefulWidget {
  const MusicPage({super.key});

  @override
  State<MusicPage> createState() => _MusicPageState();
}

class _MusicPageState extends State<MusicPage> {
  late AudioPlayer _player;
  bool _isPlaying = false;

  final playlist = ConcatenatingAudioSource(
    children: [
      AudioSource.asset(
        'assets/audios/Song1.mp3',
        tag: MediaItem(
          id: 'music1',
          album: 'Flou',
          title: '4 Chiffres',
          artist: 'Flenn',
          artUri: Uri.parse(
              'https://images.genius.com/96cdf7a98bb88ac02d8cc8a02e7506a1.1000x1000x1.jpg'),
        ),
      ),
      AudioSource.asset(
        'assets/audios/Song2.mp3',
        tag: MediaItem(
          id: 'music2',
          album: 'Sans visa',
          title: 'Balader',
          artist: 'Soolking ft. Niska',
          artUri: Uri.parse(
              'https://images.genius.com/cb485c80dea67d47c633646ea9d361d6.1000x1000x1.jpg'),
        ),
      ),
    ],
  );

  @override
  void initState() {
    super.initState();
    _initAudio();
  }

  Future<void> _initAudio() async {
    _player = AudioPlayer();
    final session = await AudioSession.instance;
    await session.configure(const AudioSessionConfiguration.music());

    await _player.setAudioSource(playlist);
    _player.setLoopMode(LoopMode.all);
    setState(() {});
  }

  Future<void> _startPlayback() async {
    await _player.play();
    setState(() {
      _isPlaying = true;
    });
  }

  @override
  void dispose() {
    _player.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey.shade900,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            StreamBuilder<SequenceState?>(
              stream: _player.sequenceStateStream,
              builder: (context, snapshot) {
                final current = snapshot.data?.currentSource?.tag as MediaItem?;
                return Column(
                  children: [
                    if (current?.artUri != null)
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Image.network(
                          current!.artUri.toString(),
                          width: 150,
                          height: 150,
                          fit: BoxFit.cover,
                        ),
                      ),
                    Text(
                      current != null ? current.title : 'No song playing',
                      style: const TextStyle(fontSize: 20, color: Colors.white),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      current != null ? current.artist ?? '' : '',
                      style: const TextStyle(fontSize: 16, color: Colors.white70),
                      textAlign: TextAlign.center,
                    ),
                  ],
                );
              },
            ),
            const SizedBox(height: 30),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(
                  icon: const Icon(Icons.skip_previous, color: Colors.white),
                  iconSize: 36,
                  onPressed: () => _player.seekToPrevious(),
                ),
                ElevatedButton.icon(
                  onPressed: _isPlaying ? null : _startPlayback,
                  icon: const Icon(Icons.play_arrow),
                  label: const Text("Play"),
                ),
                IconButton(
                  icon: const Icon(Icons.skip_next, color: Colors.white),
                  iconSize: 36,
                  onPressed: () => _player.seekToNext(),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
