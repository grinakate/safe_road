import 'package:flutter/material.dart';

import '../core/service_locator.dart';
import '../models/section.dart';
import '../models/topic.dart';
import '../models/topic_status.dart';
import '../services/map_service.dart';
import '../widgets/road_header.dart';
import '../widgets/road_painter.dart';

class MapScreen extends StatefulWidget {
  @override
  _MapScreenState createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  late Future<List<Section>> _mapFuture;
  final MapService _mapService = getIt<MapService>();

  @override
  void initState() {
    super.initState();
    _mapFuture = _mapService.getMapForUser();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0x54D55BFF), // Светло-зеленый фон "травы"
      body: SafeArea(
        child: Column(
          children: [
            RoadHeader(),
            Expanded(
              child: FutureBuilder<List<Section>>(
                future: _mapFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }
                  if (snapshot.hasError) {
                    return Center(
                      child: Text("Ошибка загрузки карты: ${snapshot.error}"),
                    );
                  }
                  if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    return const Center(
                      child: Text("Нет данных для отображения карты."),
                    );
                  }

                  final sections = snapshot.data!;
                  int globalTopicIndex = 0;

                  return SingleChildScrollView(
                    child: Stack(
                      children: [
                        CustomPaint(
                          size: Size(
                            double.infinity,
                            _calculateTotalHeight(sections),
                          ),
                          painter: RoadPainter(),
                        ),

                        Column(
                          children: sections.expand((section) {
                            List<Widget> sectionWidgets = [
                              _buildSectionHeader(section),
                              // Добавляем отступы между секциями для плавности дороги
                              // const SizedBox(height: 30),
                            ];
                            for (var topic in section.topics) {
                              sectionWidgets.add(
                                _buildTopicItem(topic, globalTopicIndex),
                              );
                              globalTopicIndex++;
                            }
                            return sectionWidgets;
                          }).toList(),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  double _calculateTotalHeight(List<Section> sections) {
    double height = 0;
    height += sections.length * (80 + 40);
    height += sections.fold(
      0,
      (sum, section) => sum + section.topics.length * (80 + 30),
    );
    return height <
            MediaQuery.of(context).size.height *
                1.5 // Минимум 1.5 высоты экрана
        ? MediaQuery.of(context).size.height * 1.5
        : height;
  }

  Widget _buildSectionHeader(Section section) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 30, horizontal: 24),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: const Color(0xFF558B2F), // Темно-зеленый
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 10,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Text(
              section.name,
              style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
              overflow: TextOverflow.ellipsis,
            ),
          ),
          const SizedBox(width: 10),
          Text(
            "${section.progressPercent}%",
            style: const TextStyle(color: Colors.white70, fontSize: 18),
          ),
        ],
      ),
    );
  }

  Widget _buildTopicItem(Topic topic, int globalIndex) {
    final alignments = [
      Alignment.center,
      Alignment.centerRight,
      Alignment.center,
      Alignment.centerLeft,
    ];
    Alignment currentAlignment = alignments[globalIndex % alignments.length];

    Color circleColor;
    IconData iconData;

    switch (topic.status) {
      case TopicStatus.COMPLETED:
        circleColor = Colors.orange; // Оранжевый для пройденных
        iconData = Icons.check;
        break;
      case TopicStatus.UNLOCKED:
        circleColor = Colors.green; // Зеленый для открытых
        iconData = Icons.play_arrow;
        break;
      case TopicStatus.LOCKED:
        circleColor = Colors.grey[400]!; // Серый для заблокированных
        iconData = Icons.lock;
        break;
    }

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16),
      child: Align(
        alignment: currentAlignment,
        child: GestureDetector(
          onTap: () {
            if (topic.status != TopicStatus.LOCKED) {
              print("Нажали на тему ${topic.name} (ID: ${topic.id})");
              // TODO: Переход на экран с деталями темы / тестом
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Открываем тему: ${topic.name}')),
              );
            } else {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Тема "${topic.name}" заблокирована')),
              );
            }
          },
          child: Column(
            children: [
              Container(
                width: 70,
                height: 70,
                decoration: BoxDecoration(
                  color: circleColor,
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.white, width: 4),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.3),
                      blurRadius: 8,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Icon(iconData, color: Colors.white, size: 32),
              ),
              const SizedBox(height: 8),
              SizedBox(
                width: 120, // Ограничиваем ширину для многострочного текста
                child: Text(
                  topic.name,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                    color: Colors.black87,
                  ),
                  maxLines: 2, // Разрешаем две строки для длинных названий
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
