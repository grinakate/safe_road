import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:provider/provider.dart';
import 'package:go_router/go_router.dart';
import 'package:safe_road/models/topic_content.dart';

import '../providers/learning_provider.dart';
import '../providers/topic_provider.dart';
import '../services/learning_service.dart';

class TopicScreen extends StatefulWidget {
  final int topicId;

  const TopicScreen({super.key, required this.topicId});

  @override
  State<TopicScreen> createState() => _TopicScreenState();
}

class _TopicScreenState extends State<TopicScreen> {
  bool _isLoading = false;
  bool _isRefreshing = false;
  TopicContent? _topic;

  Future<void> _startQuiz() async {
    if (_isLoading) return;
    setState(() => _isLoading = true);

    try {
      final service = GetIt.I<LearningService>();
      final startResp = await service.startTest(widget.topicId);
      final sessionId = startResp.sessionId;

      // Save last test topic id in provider
      try {
        context.read<LearningProvider>().setLastTestTopicId(widget.topicId);
      } catch (_) {}

      if (!mounted) return;
      // navigate to quiz route with session id; QuizScreen will load questions
      context.push('/quiz/$sessionId');
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Ошибка загрузки теста: ${e.toString()}')));
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  void initState() {
    super.initState();
    // Load topic content after first frame to avoid notifyListeners during build
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      final provider = context.read<TopicProvider>();
      try {
        final cached = provider.getCached(widget.topicId);
        if (cached != null) {
          // Показываем кэш немедленно и запускаем фоновое обновление
          setState(() {
            _topic = cached;
          });

          // фоновая подгрузка, не блокируем UI
          () async {
            setState(() => _isRefreshing = true);
            try {
              final loaded = await provider.getTopic(widget.topicId);
              if (!mounted) return;
              setState(() {
                _topic = loaded;
              });
            } catch (e) {
              if (!mounted) return;
              ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Не удалось обновить теорию: ${e.toString()}')));
            } finally {
              if (mounted) setState(() => _isRefreshing = false);
            }
          }();

          return;
        }

        // Нет кэша — показываем прогресс и ожидаем загрузки
        setState(() {
          _isLoading = true;
        });
        final loaded = await provider.getTopic(widget.topicId);
        if (!mounted) return;
        setState(() {
          _topic = loaded;
          _isLoading = false;
        });
      } catch (e) {
        if (!mounted) return;
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Не удалось загрузить теорию: ${e.toString()}')));
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }
    if (_topic == null) {
      return const Scaffold(body: Center(child: Text('Теория не найдена')));
    }

    final topic = _topic!;
    return Scaffold(
      appBar: AppBar(title: Text(topic.title)),
      body: SafeArea(
        child: Column(
          children: [
            if (_isRefreshing) const LinearProgressIndicator(minHeight: 3),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    _buildContent(context, topic),
                    const SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: _isLoading ? null : _startQuiz,
                      child: _isLoading
                          ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                          : const Text('Перейти к тестированию'),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContent(BuildContext context, TopicContent topic) {
    final List<Widget> widgets = [];

    // Group consecutive list_item blocks into a single bulleted list
    List<ContentBlock> buffer = [];

    void flushBuffer() {
      if (buffer.isEmpty) return;
      widgets.add(_buildBulletedList(buffer));
      buffer = [];
    }

    for (final block in topic.blocks) {
      switch (block.type) {
        case 'list_item':
          buffer.add(block);
          break;
        case 'heading':
          flushBuffer();
          widgets.add(Padding(
            padding: const EdgeInsets.only(top: 12.0, bottom: 6.0),
            child: Text(block.text ?? '', style: Theme.of(context).textTheme.titleLarge),
          ));
          break;
        case 'paragraph':
          flushBuffer();
          widgets.add(Padding(
            padding: const EdgeInsets.symmetric(vertical: 6.0),
            child: Text(block.text ?? '', style: Theme.of(context).textTheme.bodyMedium),
          ));
          break;
        case 'image':
          flushBuffer();
          widgets.add(Padding(
            padding: const EdgeInsets.symmetric(vertical: 10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                AspectRatio(
                  aspectRatio: 16 / 9,
                  child: Image.network(
                    block.url ?? '',
                    fit: BoxFit.cover,
                    errorBuilder: (c, e, s) => Container(
                      color: Colors.grey[200],
                      child: const Center(child: Icon(Icons.broken_image)),
                    ),
                  ),
                ),
                if ((block.caption ?? '').isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.only(top: 8.0),
                    child: Text(block.caption!, style: Theme.of(context).textTheme.bodySmall),
                  ),
              ],
            ),
          ));
          break;
        default:
          flushBuffer();
          // Unknown block, render raw text if present
          if ((block.text ?? '').isNotEmpty) {
            widgets.add(Padding(
              padding: const EdgeInsets.symmetric(vertical: 6.0),
              child: Text(block.text ?? '', style: Theme.of(context).textTheme.bodyMedium),
            ));
          }
      }
    }

    flushBuffer();

    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: widgets);
  }

  Widget _buildBulletedList(List<ContentBlock> items) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: items.map((item) {
          return Padding(
            padding: const EdgeInsets.symmetric(vertical: 2.0),
            child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('• ', style: TextStyle(fontSize: 18)),
              Expanded(child: Text(item.text ?? '', style: const TextStyle(fontSize: 16))),
            ]),
          );
        }).toList(),
      ),
    );
  }
}
