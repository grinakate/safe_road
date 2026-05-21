import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/models/topic.dart';
import 'package:safe_road/screens/quiz_screen.dart';
import 'package:safe_road/widgets/road_header.dart';

import '../models/question.dart';
import '../models/topic_status.dart';
import '../models/topic_status_ui.dart';
import '../providers/game_profile_provider.dart';
import '../providers/learning_provider.dart';
import '../services/learning_service.dart';
import '../theme.dart';
import '../widgets/section_header.dart';

class RoadMapScreen extends StatefulWidget {
  const RoadMapScreen({super.key});

  @override
  State<RoadMapScreen> createState() => _RoadMapScreenState();
}

class _RoadMapScreenState extends State<RoadMapScreen> {
  bool _isQuizLoading = false;

  @override
  void initState() {
    super.initState();
    // Загружаем профиль и разделы через провайдер
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<GameProfileProvider>().loadProfile();
      context.read<LearningProvider>().loadSections();
    });
  }

  // Асинхронный метод, который выполняет все загрузки через провайдеры — см. initState

  final LearningService _quizService = GetIt.instance<LearningService>();

  Future<void> _fetchAndStartQuizForTheme(int themeId) async {
    if (_isQuizLoading) return;

    setState(() {
      _isQuizLoading = true;
    });

    try {
      final startResp = await _quizService.startTest(themeId);
      final String sessionId = startResp.sessionId;
      final List<Question> quizData = await _quizService.getTestQuestions(
        sessionId,
      );

      if (mounted) {
        if (quizData.isEmpty) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Для этой темы пока нет вопросов.')),
          );
        } else {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) =>
                  QuizScreen(sessionId: sessionId, quizData: quizData),
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

  Future<void> _fetchAndStartQuizForSection(int sectionId) async {
    if (_isQuizLoading) return;

    setState(() {
      _isQuizLoading = true;
    });

    try {
      final startResp = await _quizService.startSectionTest(sectionId);
      final String sessionId = startResp.sessionId;
      final List<Question> quizData = await _quizService.getTestQuestions(
        sessionId,
      );

      if (mounted) {
        if (quizData.isEmpty) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Для этой темы пока нет вопросов.')),
          );
        } else {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) =>
                  QuizScreen(sessionId: sessionId, quizData: quizData),
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
    final gp = context.watch<GameProfileProvider>();
    final lp = context.watch<LearningProvider>();

    if (gp.profile == null || lp.sections.isEmpty) {
      return const Scaffold(
        body: SafeArea(child: Center(child: CircularProgressIndicator())),
      );
    }

    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              AppBar(title: Text("Безопасная дорога")),
              RoadHeader(),
              Stack(
                children: [
                  Image.asset("assets/images/plant5.png"),
                  Column(children: _buildRoadmapContent()),
                ],
              ),
            ],
          ),
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
            onPressed: () {
              context.read<GameProfileProvider>().loadProfile();
              context.read<LearningProvider>().loadSections();
            },
            child: const Text("Повторить"),
          ),
        ],
      ),
    );
  }

  List<Widget> _buildRoadmapContent() {
    final sections = context.watch<LearningProvider>().sections;
    List<Widget> roadmapWidgets = [];

    for (int i = 0; i < sections.length; i++) {
      final section = sections[i];

      // 1. Добавляем виджет раздела (прямоугольник)
      roadmapWidgets.add(SectionHeader(section: section));

      // 2. Обрабатываем топики этого раздела
      var firstRowLength = 3;
      var secondRowLength = 2;
      final isFinalTestAvailable = section.topics.isNotEmpty &&
          section.topics.every((topic) => topic.status == TopicStatus.COMPLETED);
      // Добавляем искусственную тему в конец секции — итоговый тест
      final topicsWithFinal = List<Topic>.from(section.topics);
      // synthetic topic id will be negative of section id to distinguish
      topicsWithFinal.add(Topic(
        id: -section.id,
        title: 'Итоговый тест',
        orderIndex: section.topics.length + 1,
        status: TopicStatus.UNLOCKED,
      ));

      List<List<Topic>> topicsForRows = chunkTopics(
        topicsWithFinal,
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
            isFinalTestAvailable,
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
    bool isFinalTestAvailable,
  ) {
    int emptyBlockCount = maxLength - topics.length;
    if (emptyBlockCount < 0) {
      throw Exception("В ряду недостаточно места");
    }

    List<Widget> widgetsInRow = topics
        .map(
          (topic) => _buildTopicWidget(
            topic,
            sectionId,
            isFinalTestAvailable: isFinalTestAvailable,
          ),
        )
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

  Widget _buildTopicCircle(Topic topic, TopicStatusUiConfig statusUi) {
    return Stack(
      alignment: Alignment.center,
      children: [
        // Основной круглый контейнер
        Container(
          width: 64,
          height: 64,
          decoration: BoxDecoration(
            color: statusUi.circleColor,
            shape: BoxShape.circle,
            border: Border.all(
              color: statusUi.borderColor,
              width: statusUi.borderWidth,
            ),
            boxShadow: statusUi.showShadow
                ? [
                    BoxShadow(
                      color: statusUi.shadowColor.withAlpha(128),
                      blurRadius: 9,
                      offset: const Offset(0, 0),
                    ),
                  ]
                : [],
          ),
        ),
        // Иконка в центре (галочка, кубок и т.д.)
        if (statusUi.centerIcon != null)
          Icon(
            statusUi.centerIcon,
            color: statusUi.centerIconColor ?? AppColors.white,
            size: 28,
          )
        // Номер в центре (если нет центральной иконки)
        else if (statusUi.showOrderIndex)
          Text(
            topic.orderIndex.toString(),
            textAlign: TextAlign.center,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
            style: statusUi.textStyle ?? AppTextStyles.topicNumber,
          ),
        // Бейдж-иконка в правом верхнем углу (замок и т.д.)
        if (statusUi.badgeIcon != null)
          Positioned(
            top: 0,
            right: 0,
            child: Icon(
              statusUi.badgeIcon,
              color: statusUi.badgeIconColor ?? AppColors.white,
              size: 28,
            ),
          ),
      ],
    );
  }

  Widget _buildTopicWidget(
    Topic topic,
    int sectionId, {
    required bool isFinalTestAvailable,
  }) {
    final isFinalTest = topic.id < 0;
    final statusUi = resolveTopicStatusUi(
      topic.status,
      isFinalTest: isFinalTest,
      isFinalTestAvailable: isFinalTestAvailable,
    );

    return Expanded(
      child: Container(
        height: 120,
        margin: const EdgeInsets.symmetric(horizontal: 2),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            GestureDetector(
              onTap: statusUi.isTapEnabled
                  ? (topic.id < 0
                      ? () => _fetchAndStartQuizForSection(sectionId)
                      : () => _fetchAndStartQuizForTheme(topic.id))
                  : null,
              child: _buildTopicCircle(topic, statusUi),
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
