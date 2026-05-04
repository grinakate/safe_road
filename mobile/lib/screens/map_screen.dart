import 'package:flutter/material.dart';
import 'package:safe_road/models/topic.dart';
import 'package:safe_road/widgets/road_header.dart';

import '../core/constants.dart';
import '../core/service_locator.dart';
import '../models/section.dart';
import '../models/topic_status.dart';
import '../models/user_profile.dart';
import '../services/map_service.dart';
import '../services/user_service.dart';
import '../widgets/section_header.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  late Future<void> _loadingFutures;

  UserProfile? _currentUserProfile;
  List<Section>? _sections;

  bool _hasError = false;

  @override
  void initState() {
    super.initState();
    // Вызываем метод загрузки данных при первом открытии экрана
    loadData();
  }

  // Метод для загрузки данных
  void loadData() {
    setState(() {
      _hasError = false; // Сбрасываем ошибку перед новой загрузкой
      // Переинициализируем Future, который будет управлять FutureBuilder
      _loadingFutures = _performDataLoading();
    });
  }

  // Асинхронный метод, который выполняет все загрузки
  Future<void> _performDataLoading() async {
    try {
      // Запускаем операции параллельно
      final Future<UserProfile> profileFuture = getIt<UserService>()
          .getProfile()
          .then((profile) {
            _currentUserProfile = profile;
            return profile;
          });

      final Future<List<Section>> sectionsFuture = getIt<MapService>()
          .getMapForUser()
          .then((sections) {
            _sections = sections;
            return sections;
          });

      // Ждем завершения обеих операций
      await Future.wait([profileFuture, sectionsFuture]);
    } catch (e) {
      // Если произошла какая-либо ошибка при загрузке
      print('Ошибка при загрузке данных: $e');
      setState(() {
        _hasError = true; // Устанавливаем флаг ошибки
      });
      rethrow;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: FutureBuilder(
          future: _loadingFutures,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Center(child: CircularProgressIndicator());
            } else if (_hasError) {
              return _buildRetryButton();
            } else if (snapshot.hasError) {
              return _buildRetryButton();
            } else if (_currentUserProfile == null || _sections == null) {
              throw Exception("Данные не были полностью загружены.");
            } else {
              return SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    AppBar(
                      backgroundColor: Colors.white,
                      title: Text(
                        "Безопасная дорога",
                        style: const TextStyle(
                          color: Colors.brown,
                          fontSize: 22,
                          fontFamily: 'Nunito',
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    RoadHeader(profile: _currentUserProfile!),
                    Stack(
                      children: [
                        Image.asset("assets/images/plant5.png"),
                        Column(children: _buildRoadmapContent()),
                      ],
                    ),
                  ],
                ),
              );
            }
          },
        ),
      ),
    );
  }

  Widget _buildRetryButton() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text("Не удалось загрузить данные."),
          const SizedBox(height: 10),
          ElevatedButton(onPressed: loadData, child: const Text("Повторить")),
        ],
      ),
    );
  }

  List<Widget> _buildRoadmapContent() {
    List<Widget> roadmapWidgets = [];

    for (int i = 0; i < _sections!.length; i++) {
      final section = _sections![i];

      // 1. Добавляем виджет раздела (прямоугольник)
      roadmapWidgets.add(SectionHeader(section: section));

      // 2. Обрабатываем топики этого раздела
      var firstRowLength = 3;
      var secondRowLength = 2;
      List<List<Topic>> topicsForRows = chunkTopics(
        section.topics,
        firstRowLength,
        secondRowLength,
      );
      for (int i = 0; i < topicsForRows.length; i++) {
        var isFirstRow = i % 2 == 0;
        roadmapWidgets.add(
          _buildTopicsRow(
            topicsForRows[i],
            section.id,
            isFirstRow ? firstRowLength : secondRowLength,
            isFirstRow,
          ),
        );
      }
    }
    return roadmapWidgets;
  }

  Widget _buildTopicsRow(
    List<Topic> topics,
    int sectionId,
    int maxLength,
    bool rightDirection,
  ) {
    int emptyBlockCount = maxLength - topics.length;
    if (emptyBlockCount < 0) {
      throw Exception("В ряду недостаточно места");
    }

    List<Widget> widgetsInRow = topics
        .map((topic) => _buildTopicWidget(topic, sectionId))
        .toList();

    for (int i = 0; i < emptyBlockCount; i++) {
      widgetsInRow.add(_buildEmptyTopicWidget());
    }

    if (!rightDirection) {
      widgetsInRow = widgetsInRow.reversed.toList();
    }

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 20),
      child: Container(
        margin: EdgeInsets.symmetric(horizontal: rightDirection ? 32 : 78),
        child: Row(children: widgetsInRow),
      ),
    );
  }

  Widget _buildEmptyTopicWidget() {
    return Expanded(
      child: Container(
        height: 100,
        margin: const EdgeInsets.symmetric(horizontal: 10),
      ),
    );
  }

  Widget _buildTopicWidget(Topic topic, int sectionId) {
    // TODO: Стиль кружка с названием топика
    final status = topic.status;
    final color = status == TopicStatus.LOCKED
        ? Colors.grey.shade400
        : AppConstants.greenTestColor;
    final icon = status == TopicStatus.LOCKED
        ? Icons.lock
        : (status == TopicStatus.COMPLETED ? Icons.check : null);
    final iconColor = icon == Icons.lock ? Colors.brown : Colors.white;

    return Expanded(
      child: Container(
        height: 120,
        margin: const EdgeInsets.symmetric(horizontal: 2),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            GestureDetector(
              onTap: status == TopicStatus.LOCKED
                  ? null
                  : () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Открываем: ${topic.name}')),
                      );
                    },
              child: Stack(
                alignment: Alignment.center, // Центрируем элементы по умолчанию
                children: [
                  // Сам круглый Container
                  Container(
                    width: 64,
                    height: 64,
                    decoration: BoxDecoration(
                      color: color,
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: icon == Icons.lock
                              ? Colors.black.withOpacity(0.25)
                              : Colors.white.withOpacity(0.5),
                          blurRadius: 9,
                          offset: const Offset(0, 0),
                        ),
                      ],
                    ),
                  ),
                  // Иконка, позиционированная в верхнем правом углу
                  icon == Icons.lock
                      ? Positioned(
                          top: 0, // Сдвигаем иконку на 0px сверху
                          right: 0, // Сдвигаем иконку на 0px справа
                          child: Icon(icon, color: iconColor, size: 28),
                        )
                      : Icon(icon, color: iconColor, size: 28),
                  Text(
                    topic.orderIndex.toString(),
                    textAlign: TextAlign.center,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 22,
                      fontFamily: 'Nunito',
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 5),
            SizedBox(
              child: Text(
                topic.name,
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  color: AppConstants.borderColor,
                  fontSize: 14,
                  fontFamily: 'Nunito',
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

List<List<Topic>> chunkTopics(
  List<Topic> data,
  int firstRowLength,
  int secondRowLength,
) {
  List<List<Topic>> rows = [];
  int i = 0;
  bool isFirst = true;

  while (i < data.length) {
    int count = isFirst ? firstRowLength : secondRowLength;
    // Берем подсписок, но не больше, чем осталось элементов
    var subList = data.sublist(
      i,
      (i + count > data.length) ? data.length : i + count,
    );
    rows.add(subList);
    i += count;

    isFirst = !isFirst; // Меняем флаг для следующего ряда
  }
  return rows;
}
