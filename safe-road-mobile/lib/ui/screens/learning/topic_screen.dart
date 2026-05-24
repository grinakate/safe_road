import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../../data/models/learning/topic_content.dart';
import '../../../data/services/learning_service.dart';
import '../../../logic/providers/learning_provider.dart';
import '../../../logic/providers/topic_provider.dart';
import '../../theme/app_theme.dart';
import '../../widgets/learning/topic_content_renderer.dart';

class TopicScreen extends StatefulWidget {
  final int topicId;

  const TopicScreen({super.key, required this.topicId});

  @override
  State<TopicScreen> createState() => _TopicScreenState();
}

class _TopicScreenState extends State<TopicScreen> {
  bool _isLoading = false;
  bool _isRefreshing = false;
  bool _isQuizStarting = false;
  TopicContent? _topic;

  @override
  void initState() {
    super.initState();
    // Инициализируем загрузку после первого кадра
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadTopic();
    });
  }

  /// Метод загрузки данных: сначала показывает кэш, потом обновляет из сети
  Future<void> _loadTopic() async {
    final provider = context.read<TopicProvider>();
    final cached = provider.getCached(widget.topicId);

    if (cached != null) {
      // Шаг 1: Если кэш есть, мгновенно выводим его на экран
      setState(() {
        _topic = cached;
      });

      // Шаг 2: В фоне отправляем запрос за свежими данными
      setState(() => _isRefreshing = true);
      try {
        final loaded = await provider.getTopic(widget.topicId);
        if (mounted) {
          setState(() {
            _topic = loaded;
          });
        }
      } catch (e) {
        _showSnackBar('Не удалось обновить теорию: ${e.toString()}');
      } finally {
        if (mounted) {
          setState(() => _isRefreshing = false);
        }
      }
    } else {
      // Шаг 3: Если кэша нет, включаем полноценный лоадер и ждем ответа
      setState(() => _isLoading = true);
      try {
        final loaded = await provider.getTopic(widget.topicId);
        if (mounted) {
          setState(() {
            _topic = loaded;
          });
        }
      } catch (e) {
        _showSnackBar('Не удалось загрузить теорию: ${e.toString()}');
      } finally {
        if (mounted) {
          setState(() => _isLoading = false);
        }
      }
    }
  }

  /// Метод запуска тестирования по текущей теме
  Future<void> _startQuiz() async {
    if (_isQuizStarting) return;
    setState(() => _isQuizStarting = true);

    try {
      final service = GetIt.I<LearningService>();
      final startResp = await service.startTest(widget.topicId);
      final sessionId = startResp.sessionId;

      if (!mounted) return;

      context.read<LearningProvider>().setLastResult(
        correct: 0,
        total: 0,
        experience: 0,
        topicId: widget.topicId,
      );

      // Переходим на экран квиза
      context.push('/quiz/$sessionId');
    } catch (e) {
      _showSnackBar('Ошибка запуска теста: ${e.toString()}');
    } finally {
      if (mounted) {
        setState(() => _isQuizStarting = false);
      }
    }
  }

  void _showSnackBar(String text) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(text)));
    }
  }

  @override
  Widget build(BuildContext context) {
    // 1. Полноэкранный лоадер, если данных вообще еще нет
    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    // 2. Обработка ошибки загрузки (если бэкенд упал и кэша нет)
    if (_topic == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Теория')),
        body: const Center(child: Text('Теория временно недоступна.')),
      );
    }

    final topic = _topic!;
    return Scaffold(
      appBar: AppBar(title: Text(topic.title)),
      body: SafeArea(
        child: Column(
          children: [
            if (_isRefreshing)
              const LinearProgressIndicator(
                minHeight: 3,
                color: AppColors.primaryGreen,
              ),

            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [TopicContentRenderer(blocks: topic.blocks)],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
