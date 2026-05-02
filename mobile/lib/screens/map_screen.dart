import 'package:flutter/material.dart';
import '../core/service_locator.dart';
import '../models/section.dart';
import '../models/user_profile.dart';
import '../services/map_service.dart';
import '../services/user_service.dart';
import '../widgets/road_header.dart';
import '../widgets/section_header.dart';
import '../widgets/topic_item.dart';
import '../widgets/road_painter.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  // Мы будем хранить оба результата в одном Future
  late Future<Map<String, dynamic>> _dataFuture;

  @override
  void initState() {
    super.initState();
    _dataFuture = _loadAllData();
  }

  // Загружаем карту и профиль параллельно
  Future<Map<String, dynamic>> _loadAllData() async {
    final results = await Future.wait([
      getIt<MapService>().getMapForUser(),
      getIt<UserService>().getProfile(),
    ]);

    return {
      'sections': results[0] as List<Section>,
      'profile': results[1] as UserProfile,
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Цвет фона "травы"
        backgroundColor: const Color(0xFFE8F5E9),
        body: SafeArea(
            child: FutureBuilder<Map<String, dynamic>>(
                future: _dataFuture,
                builder: (context, snapshot) {
                  // 1. Состояние загрузки
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  // 2. Обработка ошибки
                  if (snapshot.hasError) {
                    return Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.error_outline, color: Colors.red, size: 60),
                          const SizedBox(height: 16),
                          Text("Ошибка: ${snapshot.error}", textAlign: TextAlign.center),
                          ElevatedButton(
                            onPressed: () => setState(() => _dataFuture = _loadAllData()),
                            child: const Text("Повторить"),
                          )
                        ],
                      ),
                    );
                  }
                  if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    return const Center(
                      child: Text("Нет данных для отображения карты."),
                    );
                  }

                  // 3. Данные получены
                  final sections = snapshot.data!['sections'] as List<Section>;
                  final profile = snapshot.data!['profile'] as UserProfile;

                  return Column(
                    children: [
                      // Верхняя панель с прогрессом (котик)
                      RoadHeader(profile: profile),

                      Expanded(
                        child: SingleChildScrollView(
                          child: Stack(
                            children: [
                              // Рисуем дорогу на фоне
                              CustomPaint(
                                size: Size(
                                  MediaQuery.of(context).size.width,
                                  _calculateTotalHeight(sections),
                                ),
                                painter: RoadPainter(),
                              ),

                              // Слой с контентом (Секции и Топики)
                              Column(
                                children: sections.expand((section) {
                                  return [
                                    SectionHeader(section: section),
                                    ...section.topics.map((topic) => TopicItem(topic: topic)),
                                    const SizedBox(height: 40), // Отступ между секциями
                                  ];
                                }).toList(),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  );
                },
            ),
        ),
    );
  }

  // Расчет высоты холста для дороги
  double _calculateTotalHeight(List<Section> sections) {
    int totalTopics = sections.fold(0, (sum, s) => sum + s.topics.length);
    // Примерный расчет: Заголовок ~120px, Топик ~130px
    double height = (sections.length * 120.0) + (totalTopics * 130.0) + 200.0;

    // Дорога не должна быть короче экрана
    double minHeight = MediaQuery.of(context).size.height;
    return height < minHeight ? minHeight : height;
  }
}

