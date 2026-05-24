import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/ui/widgets/common/secure_network_image.dart';

import '../../data/models/learning/section.dart';
import '../../data/models/learning/topic.dart';
import '../../data/services/learning_service.dart';
import '../../logic/providers/topic_provider.dart';
import '../theme/app_theme.dart';

class TheoryTreeScreen extends StatefulWidget {
  const TheoryTreeScreen({super.key});

  @override
  State<TheoryTreeScreen> createState() => _TheoryTreeScreenState();
}

class _TheoryTreeScreenState extends State<TheoryTreeScreen> {
  bool _isLoading = false;
  List<Section> _sections = [];
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadSections();
  }

  Future<void> _loadSections() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });
    try {
      final service = GetIt.I<LearningService>();
      final sections = await service.getRoadMap();
      if (mounted) {
        setState(() {
          _sections = sections;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _errorMessage =
              'Не удалось загрузить карту обучения: ${e.toString()}';
        });
        _showSnackBar('Ошибка загрузки: ${e.toString()}');
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
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
    return Scaffold(
      appBar: AppBar(
        title: const Text('Теория', style: AppTheme.appBarTitle,),
        backgroundColor: AppColors.white,
        foregroundColor: AppColors.brownText,
        elevation: 0,
      ),
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: _loadSections,
          color: AppColors.primaryGreen,
          child: _isLoading
              ? const Center(
                  child: CircularProgressIndicator(
                    color: AppColors.primaryGreen,
                  ),
                )
              : _errorMessage != null
              ? Center(
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          _errorMessage!,
                          textAlign: TextAlign.center,
                          style: AppTextStyles.bodyMedium,
                        ),
                        const SizedBox(height: 16),
                        ElevatedButton(
                          onPressed: _loadSections,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: AppColors.primaryGreen,
                            foregroundColor: AppColors.white,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                          child: const Text('Повторить попытку'),
                        ),
                      ],
                    ),
                  ),
                )
              : _sections.isEmpty
              ? const Center(
                  child: Text(
                    'Разделы обучения пока не доступны.',
                    textAlign: TextAlign.center,
                    style: AppTextStyles.bodyMedium,
                  ),
                )
              : ListView.builder(
                  itemCount: _sections.length,
                  itemBuilder: (context, index) {
                    final section = _sections[index];
                    return _buildSectionTile(section);
                  },
                ),
        ),
      ),
    );
  }

  Widget _buildSectionTile(Section section) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ExpansionTile(
        collapsedBackgroundColor: AppColors.white,
        backgroundColor: AppColors.white,
        splashColor: AppColors.white,
        initiallyExpanded: false,
        collapsedShape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        tilePadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        collapsedIconColor: AppColors.brownText,
        title: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Padding(
                padding: const EdgeInsets.only(right: 14.0),
                child: SecureNetworkImage(
                  imageUrl: section.url,
                  width: 70, // Фиксированная ширина для картинки
                  fit: BoxFit.fitWidth,
                ),
              ),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      section.title,
                      style: AppTextStyles.headlineLarge.copyWith(fontSize: 18),
                    ),
                    const SizedBox(height: 4),
                    Text(section.description, style: AppTextStyles.bodyMedium.copyWith(fontSize: 12)),
                  ],
                ),
              ),
            ],
          ),
        ),
        children: section.topics
            .map((topic) => _buildTopicListTile(topic))
            .toList(),
      ),
    );
  }

  Widget _buildTopicListTile(Topic topic) {
    return ListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 32, vertical: 0),
      leading: const Icon(
        Icons.arrow_forward_rounded,
        color: AppColors.primaryGreen,
        size: 16,
      ),
      title: Text(topic.title, style: AppTextStyles.sectionHeaderText.copyWith(fontSize: 14)),
      onTap: () {
        // Переходим на экран с теорией по выбранному топику
        _openTheory(topic.id);
      },
    );
  }

  // --- ЛОГИКА ТЕОРИИ ---
  Future<void> _openTheory(int topicId) async {
    final topicProv = context.read<TopicProvider>();

    try {
      if (topicProv.getCached(topicId) == null) {
        _showLoadingDialog(); // Показываем лоадер, если темы нет в кэше
        await topicProv.getTopic(topicId);
        if (mounted) {
          Navigator.of(context).pop(); // Убираем лоадер
        }
      }
      if (mounted) {
        context.push('/topic/$topicId');
      }
    } catch (e) {
      if (mounted && Navigator.of(context).canPop()) {
        Navigator.of(context).pop();
      }
      _showSnackBar('Не удалось загрузить теорию');
    } finally {}
  }

  void _showLoadingDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const Center(child: CircularProgressIndicator()),
    );
  }
}
