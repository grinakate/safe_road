import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/data/models/gamification/leaderboard_entry.dart';
import 'package:safe_road/ui/theme/app_colors.dart';
import 'package:safe_road/ui/theme/app_theme.dart';

import '../../../logic/providers/leaderboard_provider.dart';
import '../../../logic/providers/leaderboard_state.dart';
import '../../widgets/common/secure_network_image.dart';

class LeaderboardScreen extends StatefulWidget {
  const LeaderboardScreen({super.key});

  @override
  State<LeaderboardScreen> createState() => _LeaderboardScreenState();
}

class _LeaderboardScreenState extends State<LeaderboardScreen>
    with TickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(
      length: LeaderboardPeriod.values.length,
      vsync: this,
    );

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final provider = context.read<LeaderboardProvider>();

      for (var period in LeaderboardPeriod.values) {
        final state = provider.getState(period);
        if (state.entries.isEmpty) {
          provider.fetch(period);
        }
      }
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Рейтинг'),
        backgroundColor: AppColors.white,
        bottom: TabBar(
          controller: _tabController,
          labelStyle: AppTextStyles.headlineLarge.copyWith(fontSize: 14),
          tabs: LeaderboardPeriod.values
              .map((period) => Tab(text: period.label))
              .toList(),
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: LeaderboardPeriod.values
            .map((period) => _buildTabContent(context, period: period))
            .toList(),
      ),
    );
  }

  Widget _buildTabContent(
    BuildContext context, {
    required LeaderboardPeriod period,
  }) {
    return Consumer<LeaderboardProvider>(
      builder: (context, prov, _) {
        final state = prov.getState(period);

        // 1. Первичная загрузка (когда вообще ничего нет)
        if (state.status == LoadingStatus.loading) {
          return const Center(child: CircularProgressIndicator());
        }

        // 2. Ошибка
        if (state.status == LoadingStatus.error && state.entries.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.error_outline, size: 48, color: Colors.red),
                const SizedBox(height: 12),
                Text(
                  'Не удалось загрузить данные',
                  style: TextStyle(color: Colors.grey[700]),
                ),
                TextButton(
                  onPressed: () => prov.fetch(period),
                  child: const Text('Повторить попытку'),
                ),
              ],
            ),
          );
        }

        // 3. Пустой список
        if (state.entries.isEmpty) {
          return RefreshIndicator(
            onRefresh: () => prov.fetch(period),
            child: const SingleChildScrollView(
              physics: AlwaysScrollableScrollPhysics(),
              child: SizedBox(
                height: 300, // Высота, чтобы жест работал на пустом экране
                child: Center(
                  child: Text(
                    'Рейтинг пока пуст. Потяните вниз для обновления.',
                  ),
                ),
              ),
            ),
          );
        }

        // 4. Отображение списка + Pull-to-Refresh
        return RefreshIndicator(
          onRefresh: () => prov.fetch(period), // Запрос свежих данных по жесту
          child: ListView.separated(
            physics: const AlwaysScrollableScrollPhysics(),
            padding: const EdgeInsets.all(12),
            itemCount: state.entries.length,
            separatorBuilder: (_, __) => const SizedBox(height: 8),
            itemBuilder: (context, idx) {
              final leader = state.entries[idx];
              return _buildRow(leader, idx + 1);
            },
          ),
        );
      },
    );
  }

  Widget _buildRow(LeaderboardEntry leader, int visibleIndex) {
    final isCurrent = leader.isCurrentUser;
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 20),
      decoration: BoxDecoration(
        color: isCurrent ? AppColors.lightGreenBackground : AppColors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [AppTheme.cardShadow],
      ),
      child: Row(
        children: [
          SizedBox(
            width: 36,
            child: Text(
              leader.rank.toString(),
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
          const SizedBox(width: 12),
          CircleAvatar(
            radius: 30,
            backgroundColor: Colors.transparent,
            child: SecureNetworkImage(
              imageUrl: leader.avatarUrl,
              fit: BoxFit.fitHeight,
              width: 60,
              height: 60,
            ),
          ),
          const SizedBox(width: 18),
          Expanded(
            child: Text(
              leader.nickname,
              style: TextStyle(
                fontWeight: isCurrent ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          ),
          const SizedBox(width: 12),
          Text(
            '${leader.xp} XP',
            style: const TextStyle(fontWeight: FontWeight.w600),
          ),
        ],
      ),
    );
  }
}
