import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/screens/quiz_screen.dart';
import 'package:safe_road/models/topic_content.dart';

import '../providers/learning_provider.dart';
import '../services/learning_service.dart';

class TopicScreen extends StatefulWidget {
  final TopicContent topic;

  const TopicScreen({super.key, required this.topic});

  @override
  State<TopicScreen> createState() => _TopicScreenState();
}

class _TopicScreenState extends State<TopicScreen> {
  bool _isLoading = false;

  Future<void> _startQuiz() async {
    if (_isLoading) return;
    setState(() => _isLoading = true);

    try {
      final service = GetIt.I<LearningService>();
      final startResp = await service.startTest(widget.topic.id);
      final sessionId = startResp.sessionId;
      final quizData = await service.getTestQuestions(sessionId);

      // Save last test topic id in provider
      try {
        context.read<LearningProvider>().setLastTestTopicId(widget.topic.id);
      } catch (_) {}

      if (!mounted) return;
      if (quizData.isEmpty) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Для этой темы пока нет вопросов.')));
      } else {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (c) => QuizScreen(sessionId: sessionId, quizData: quizData)),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Ошибка загрузки теста: ${e.toString()}')));
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final topic = widget.topic;
    return Scaffold(
      appBar: AppBar(title: Text(topic.title)),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _buildContent(context),
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
    );
  }

  Widget _buildContent(BuildContext context) {
    final List<Widget> widgets = [];

    // Group consecutive list_item blocks into a single bulleted list
    List<ContentBlock> buffer = [];

    void flushBuffer() {
      if (buffer.isEmpty) return;
      widgets.add(_buildBulletedList(buffer));
      buffer = [];
    }

    for (final block in widget.topic.blocks) {
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


