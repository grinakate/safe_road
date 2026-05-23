import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../data/models/learning/section.dart';
import '../../data/models/learning/topic.dart';
import '../../data/models/learning/topic_status.dart';
import '../../data/services/learning_service.dart';
import '../../logic/providers/game_profile_provider.dart';
import '../../logic/providers/learning_provider.dart';
import '../../logic/providers/learning_state.dart';
import '../../logic/providers/topic_provider.dart';
import '../styles/topic_style.dart';
import '../theme/app_theme.dart';
import '../widgets/learning/road_header.dart';
import '../widgets/learning/section_header.dart';

class RoadMapScreen extends StatefulWidget {
  const RoadMapScreen({super.key});

  @override
  State<RoadMapScreen> createState() => _RoadMapScreenState();
}

class _RoadMapScreenState extends State<RoadMapScreen> {
  bool _isActionInProgress = false; // Блокировка кнопок при переходе/загрузке
  final LearningService _quizService = GetIt.instance<LearningService>();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<GameProfileProvider>().loadProfile();
      context.read<LearningProvider>().loadRoadMap();
    });
  }

  /// --- ЛОГИКА ЗАПУСКА ТЕСТА ---
  Future<void> _startQuiz({int? topicId, required int sectionId}) async {
    if (_isActionInProgress) return;
    setState(() => _isActionInProgress = true);

    try {
      // Декларативная логика: если topicId == null, это тест секции
      final startResp = (topicId != null)
          ? await _quizService.startTest(topicId)
          : await _quizService.startSectionTest(sectionId);

      final questions = await _quizService.getTestQuestions(
        startResp.sessionId,
      );

      if (!mounted) return;

      if (questions.isEmpty) {
        _showSnackBar('Вопросы для данного теста не найдены');
      } else {
        // Сохраняем контекст в провайдер (теперь передаем и то, и другое)
        context.read<LearningProvider>().setLastResult(
          correct: 0,
          total: 0,
          experience: 0,
          topicId: topicId,
          // Если в модели LearningProvider добавишь sectionId, будет еще лучше
        );
        context.push('/quiz/${startResp.sessionId}');
      }
    } catch (e) {
      _showSnackBar('Ошибка запуска: $e');
    } finally {
      if (mounted) setState(() => _isActionInProgress = false);
    }
  }

  // --- ЛОГИКА ТЕОРИИ ---

  Future<void> _openTheory(Topic topic) async {
    if (_isActionInProgress) return;
    setState(() => _isActionInProgress = true);

    final topicProv = context.read<TopicProvider>();

    try {
      if (topicProv.getCached(topic.id) == null) {
        _showLoadingDialog(); // Показываем лоадер, если темы нет в кэше
        await topicProv.getTopic(topic.id);
        if (mounted) Navigator.of(context).pop(); // Убираем лоадер
      }
      if (mounted) context.push('/topic/${topic.id}');
    } catch (e) {
      if (mounted && Navigator.of(context).canPop()) {
        Navigator.of(context).pop();
      }
      _showSnackBar('Не удалось загрузить теорию');
    } finally {
      if (mounted) setState(() => _isActionInProgress = false);
    }
  }

  /// --- ОБРАБОТКА НАЖАТИЯ НА ТОПИК ---
  Future<void> _handleTopicTap(
    Topic topic,
    int sectionId,
    bool isFinalAvailable,
  ) async {
    // Проверяем, является ли этот топик "Финальным тестом"
    // (например, по ID == 0 или по заголовку, если ID заняты)
    final bool isSectionTest = (topic.id == 0);

    final choice = await showDialog<String>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(topic.title),
        content: Text(
          isSectionTest
              ? 'Начать итоговое тестирование по разделу?'
              : 'Выберите действие',
        ),
        actions: [
          if (!isSectionTest)
            TextButton(
              onPressed: () => Navigator.pop(ctx, 'theory'),
              child: const Text('Теория'),
            ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, 'test'),
            child: const Text('Тест'),
          ),
        ],
      ),
    );

    if (choice == 'test') {
      // Вызываем унифицированный метод
      _startQuiz(
        topicId: isSectionTest ? null : topic.id, // null для теста секции
        sectionId: sectionId,
      );
    } else if (choice == 'theory') {
      _openTheory(topic);
    }
  }

  /// --- UI BUILDING ---
  @override
  Widget build(BuildContext context) {
    final profile = context.select<GameProfileProvider, bool>(
      (p) => p.profile != null,
    );
    final learningState = context.select<LearningProvider, LearningState>(
      (p) => p.state,
    );

    if (!profile || learningState.status == LearningStatus.loading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    return Scaffold(
      appBar: AppBar(title: const Text("Безопасная дорога"), elevation: 0),
      body: RefreshIndicator(
        onRefresh: () =>
            context.read<LearningProvider>().loadRoadMap(forceRefresh: true),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: Column(
            children: [
              const RoadHeader(),
              Stack(
                children: [
                  Positioned(
                    child: Image.asset(
                      "assets/images/plant5.png",
                      opacity: const AlwaysStoppedAnimation(.3),
                    ),
                  ),
                  Column(
                    children: _buildRoadmapContent(learningState.sections),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  List<Widget> _buildRoadmapContent(List<Section> sections) {
    List<Widget> content = [];

    for (var section in sections) {
      content.add(SectionHeader(section: section));

      final isFinalAvailable = section.topics.every(
        (t) => t.status == TopicStatus.COMPLETED,
      );

      // Создаем список для отображения
      List<Topic> displayTopics = List<Topic>.from(section.topics);

      // Добавляем финальный тест как объект с null ID
      final finalTest = Topic(
        id: 0,
        // Или null, если позволяет модель. Если id обязателен, используем 0 как константу
        title: 'Итоговый тест',
        orderIndex: displayTopics.length + 1,
        status: isFinalAvailable ? TopicStatus.UNLOCKED : TopicStatus.LOCKED,
      );
      displayTopics.add(finalTest);

      final rows = _chunkTopics(displayTopics, 3, 2);
      for (int i = 0; i < rows.length; i++) {
        content.add(
          _buildSnakeRow(rows[i], section.id, i % 2 == 0, isFinalAvailable),
        );
      }
      content.add(const SizedBox(height: 30));
    }
    return content;
  }

  Widget _buildSnakeRow(
    List<Topic> topics,
    int sectionId,
    bool isLTR,
    bool isFinalAvailable,
  ) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 20),
      child: Row(
        mainAxisAlignment: isLTR
            ? MainAxisAlignment.start
            : MainAxisAlignment.end,
        children: (isLTR ? topics : topics.reversed).map((topic) {
          return _buildTopicItem(topic, sectionId, isFinalAvailable);
        }).toList(),
      ),
    );
  }

  Widget _buildTopicItem(Topic topic, int sectionId, bool isFinalAvailable) {
    final bool isSectionFinalTest = (topic.id == 0);

    final statusUi = TopicStyle.resolve(
      status: topic.status,
      isFinalTest: isSectionFinalTest,
      isFinalTestAvailable: isFinalAvailable,
    );

    return Container(
      width: 90,
      margin: const EdgeInsets.symmetric(horizontal: 8),
      child: Column(
        children: [
          GestureDetector(
            onTap: (statusUi.isTapEnabled && !_isActionInProgress)
                ? () => _handleTopicTap(topic, sectionId, isFinalAvailable)
                : null,
            child: _buildTopicCircle(topic, statusUi),
          ),
          const SizedBox(height: 8),
          Text(
            topic.title,
            textAlign: TextAlign.center,
            style: AppTextStyles.topicName.copyWith(fontSize: 12),
            maxLines: 2,
          ),
        ],
      ),
    );
  }

  Widget _buildTopicCircle(Topic topic, TopicStyle ui) {
    return Container(
      width: 60,
      height: 60,
      decoration: BoxDecoration(
        color: ui.circleColor,
        shape: BoxShape.circle,
        border: Border.all(color: ui.borderColor, width: ui.borderWidth),
        boxShadow: ui.showShadow
            ? [
                BoxShadow(
                  color: ui.shadowColor.withValues(alpha: 0.3),
                  blurRadius: 8,
                ),
              ]
            : [],
      ),
      child: Icon(
        ui.centerIcon ?? (ui.showOrderIndex ? null : Icons.book),
        color: Colors.white,
      ),
    );
  }

  /// --- УТИЛИТЫ ---
  List<List<Topic>> _chunkTopics(List<Topic> data, int first, int second) {
    List<List<Topic>> chunks = [];
    int i = 0;
    bool isFirst = true;
    while (i < data.length) {
      int size = isFirst ? first : second;
      chunks.add(
        data.sublist(i, (i + size > data.length) ? data.length : i + size),
      );
      i += size;
      isFirst = !isFirst;
    }
    return chunks;
  }

  void _showSnackBar(String msg) =>
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(msg)));

  void _showLoadingDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const Center(child: CircularProgressIndicator()),
    );
  }
}
