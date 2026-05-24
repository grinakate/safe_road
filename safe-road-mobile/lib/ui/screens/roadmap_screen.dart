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
  final LearningService _quizService = GetIt.I<LearningService>();

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

      if (!mounted) {
        return;
      }

      if (questions.isEmpty) {
        _showSnackBar('Вопросы для данного теста не найдены');
      } else {
        context.read<LearningProvider>().setLastResult(
          correct: 0,
          total: 0,
          experience: 0,
          topicId: topicId,
        );
        context.push('/quiz/${startResp.sessionId}');
      }
    } catch (e) {
      _showSnackBar('Ошибка запуска: $e');
    } finally {
      if (mounted) {
        setState(() => _isActionInProgress = false);
      }
    }
  }

  /// --- ОБРАБОТКА НАЖАТИЯ НА ТОПИК ---
  Future<void> _handleTopicTap(
    Topic topic,
    int sectionId,
    bool isFinalAvailable,
  ) async {
    final bool isSectionTest = (topic.id == 0);
    _startQuiz(
      topicId: isSectionTest ? null : topic.id,
      sectionId: sectionId,
    );
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
                      opacity: const AlwaysStoppedAnimation(.8),
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

      // Добавляем финальный тест
      final finalTest = Topic(
        id: 0,
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
      padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 20),
      child: Container(
        margin: EdgeInsets.symmetric(horizontal: isLTR ? 32 : 78),
        child: Row(
          children: (isLTR ? topics : topics.reversed).map((topic) {
            return _buildTopicItem(topic, sectionId, isFinalAvailable);
          }).toList(),
        ),
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

    return Expanded(
      child: Container(
        width: 120,
        margin: const EdgeInsets.symmetric(horizontal: 2),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            GestureDetector(
              onTap: (statusUi.isTapEnabled && !_isActionInProgress)
                  ? () => _handleTopicTap(topic, sectionId, isFinalAvailable)
                  : null,
              child: _buildTopicCircle(topic, statusUi),
            ),
            const SizedBox(height: 5),
            SizedBox(
              height: AppTextStyles.topicName.fontSize! * 2,
              child: Text(
                topic.title,
                textAlign: TextAlign.center,
                style: AppTextStyles.topicName,
                overflow: TextOverflow.ellipsis,
                maxLines: 2,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTopicCircle(Topic topic, TopicStyle topicStyle) {
    return Container(
      width: 64,
      height: 64,
      decoration: BoxDecoration(
        color: topicStyle.circleColor,
        shape: BoxShape.circle,
        border: Border.all(
          color: topicStyle.borderColor,
          width: topicStyle.borderWidth,
        ),
        boxShadow: topicStyle.showShadow
            ? [BoxShadow(color: topicStyle.shadowColor, blurRadius: 9)]
            : [],
      ),
      child: Stack(
        alignment: AlignmentDirectional.center,
        children: [
          if (topicStyle.showOrderIndex)
            Text(topic.orderIndex.toString(), style: AppTextStyles.topicNumber),
          ?topicStyle.content,
        ],
      ),
    );
  }

  /// --- УТИЛИТЫ ---
  List<List<Topic>> _chunkTopics(
    List<Topic> data,
    int firstRowSize,
    int secondRowSize,
  ) {
    List<List<Topic>> chunks = [];
    int i = 0;
    bool isFirst = true;
    while (i < data.length) {
      int size = isFirst ? firstRowSize : secondRowSize;
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

}
