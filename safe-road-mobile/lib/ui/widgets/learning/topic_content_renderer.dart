import 'package:flutter/material.dart';

import '../../../data/models/learning/topic_content.dart';
import '../../theme/app_theme.dart';

class TopicContentRenderer extends StatelessWidget {
  final List<ContentBlock> blocks;

  const TopicContentRenderer({super.key, required this.blocks});

  @override
  Widget build(BuildContext context) {
    final List<Widget> widgets = [];
    List<ContentBlock> listBuffer = [];

    // Вспомогательная функция для отрисовки накопленного списка
    void flushBuffer() {
      if (listBuffer.isEmpty) return;
      widgets.add(_BulletedList(items: listBuffer));
      listBuffer = [];
    }

    for (final block in blocks) {
      switch (block.type) {
        case 'list_item':
          listBuffer.add(block);
          break;
        case 'heading':
          flushBuffer();
          widgets.add(_Heading(text: block.text ?? ''));
          break;
        case 'paragraph':
          flushBuffer();
          widgets.add(_Paragraph(text: block.text ?? ''));
          break;
        case 'image':
          flushBuffer();
          widgets.add(_ImageBlock(url: block.url, caption: block.caption));
          break;
        default:
          flushBuffer();
          if ((block.text ?? '').isNotEmpty) {
            widgets.add(_Paragraph(text: block.text ?? ''));
          }
      }
    }

    flushBuffer();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: widgets,
    );
  }
}

class _Heading extends StatelessWidget {
  final String text;

  const _Heading({required this.text});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 20.0, bottom: 8.0),
      child: Text(
        text,
        style: Theme.of(context).textTheme.titleLarge?.copyWith(
          fontWeight: FontWeight.bold,
          color: AppColors.darkBrownText,
        ),
      ),
    );
  }
}

class _Paragraph extends StatelessWidget {
  final String text;

  const _Paragraph({required this.text});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Text(
        text,
        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
          height: 1.5,
          fontSize: 16,
          color: AppColors.darkBrownText.withValues(alpha: 0.9),
        ),
      ),
    );
  }
}

class _ImageBlock extends StatelessWidget {
  final String? url;
  final String? caption;

  const _ImageBlock({this.url, this.caption});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(12),
            child: Image.network(
              url ?? '',
              fit: BoxFit.cover,
              errorBuilder: (_, __, ___) => Container(
                height: 150,
                color: Colors.grey[200],
                child: const Icon(Icons.broken_image, color: Colors.grey),
              ),
            ),
          ),
          if (caption != null && caption!.isNotEmpty)
            Padding(
              padding: const EdgeInsets.only(top: 8, left: 4),
              child: Text(
                caption!,
                style: Theme.of(
                  context,
                ).textTheme.bodySmall?.copyWith(fontStyle: FontStyle.italic),
              ),
            ),
        ],
      ),
    );
  }
}

class _BulletedList extends StatelessWidget {
  final List<ContentBlock> items;

  const _BulletedList({required this.items});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10.0),
      child: Column(
        children: items
            .map(
              (item) => Padding(
                padding: const EdgeInsets.symmetric(vertical: 4.0),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Padding(
                      padding: EdgeInsets.only(top: 2, right: 8),
                      child: Icon(
                        Icons.fiber_manual_record,
                        size: 8,
                        color: AppColors.primaryGreen,
                      ),
                    ),
                    Expanded(
                      child: Text(
                        item.text ?? '',
                        style: const TextStyle(fontSize: 16, height: 1.4),
                      ),
                    ),
                  ],
                ),
              ),
            )
            .toList(),
      ),
    );
  }
}
