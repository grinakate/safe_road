import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:safe_road/models/topic.dart';
import 'package:safe_road/screens/quiz_screen.dart';
import 'package:safe_road/widgets/road_header.dart';

import '../core/service_locator.dart';
import '../models/question.dart';
import '../models/section.dart';
import '../models/topic_status.dart';
import '../models/game_profile.dart';
import '../services/road_map_service.dart';
import '../services/quiz_service.dart';
import '../services/game_profile_service.dart';
import '../widgets/section_header.dart';
import '../theme.dart';

class RoadMapScreen extends StatefulWidget {
  const RoadMapScreen({super.key});

  @override
  State<RoadMapScreen> createState() => _RoadMapScreenState();
}

class _RoadMapScreenState extends State<RoadMapScreen> {
  late Future<void> _loadingFutures;

  GameProfile? _currentUserProfile;
  List<Section>? _sections;

  bool _hasError = false;
  bool _isQuizLoading = false;

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
      final Future<GameProfile> profileFuture = getIt<GameProfileService>()
          .getProfile()
          .then((profile) {
            _currentUserProfile = profile;
            return profile;
          });

      final Future<List<Section>> sectionsFuture = getIt<LearningService>()
          .getRoadMap()
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

  final QuizService _quizService = GetIt.instance<QuizService>();

  Future<void> _fetchAndStartQuizForTheme(int themeId) async {
    if (_isQuizLoading) return;

    setState(() {
      _isQuizLoading = true;
    });

    try {
      final List<Question> quizData = await _quizService.fetchQuizData(themeId);

      if (mounted) {
        if (quizData.isEmpty) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Для этой темы пока нет вопросов.')),
          );
        } else {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => QuizScreen(quizData: quizData),
            ),
          );
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Ошибка загрузки вопросов: ${e.toString()}')),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isQuizLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
                      title: Text(
                        "Безопасная дорога",
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
          Text("Не удалось загрузить данные.", style: AppTextStyles.bodyLarge),
          const SizedBox(height: 10),
          ElevatedButton(
            onPressed: loadData,
            child: const Text("Повторить"),
          ),
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
    final status = topic.status;
    final color = status == TopicStatus.LOCKED
        ? AppColors.greyBorder
        : AppColors.primaryGreen;
    final icon = status == TopicStatus.LOCKED
        ? Icons.lock
        : (status == TopicStatus.COMPLETED ? Icons.check : null);
    final iconColor = icon == Icons.lock ? AppColors.darkBrownText : AppColors.white;

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
                  : () => _fetchAndStartQuizForTheme(topic.id),
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
                              ? AppColors.darkBrownText.withAlpha(64)
                              : AppColors.white.withAlpha(128),
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
                    style: AppTextStyles.topicNumber,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 5),
            SizedBox(
              child: Text(
                topic.title,
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: AppTextStyles.topicName,
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
