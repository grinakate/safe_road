import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/models/leaderboard_entry.dart';
import 'package:safe_road/providers/leaderboard_provider.dart';

class LeaderboardScreen extends StatefulWidget {
  const LeaderboardScreen({super.key});

  @override
  State<LeaderboardScreen> createState() => _LeaderboardScreenState();
}

class _LeaderboardScreenState extends State<LeaderboardScreen> with TickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    // Fetch initial data
    final provider = context.read<LeaderboardProvider>();
    provider.fetchWeek();
    provider.fetchAll();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Рейтинг'),
        bottom: TabBar(
          controller: _tabController,
          tabs: const [Tab(text: 'Неделя'), Tab(text: 'Все время')],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildTabContent(context, isWeek: true),
          _buildTabContent(context, isWeek: false),
        ],
      ),
    );
  }

  Widget _buildTabContent(BuildContext context, {required bool isWeek}) {
    return Consumer<LeaderboardProvider>(builder: (context, prov, _) {
      final loading = isWeek ? prov.loadingWeek : prov.loadingAll;
      final List<LeaderboardEntry> items = isWeek ? prov.week : prov.all;
      if (loading) return const Center(child: CircularProgressIndicator());
      if (items.isEmpty) return const Center(child: Text('Нет данных'));

      return ListView.separated(
        padding: const EdgeInsets.all(12),
        itemCount: items.length,
        separatorBuilder: (_, __) => const SizedBox(height: 8),
        itemBuilder: (context, idx) {
          final e = items[idx];
          return _buildRow(e, idx + 1);
        },
      );
    });
  }

  Widget _buildRow(LeaderboardEntry entry, int visibleIndex) {
    final isCurrent = entry.isCurrentUser;
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 12),
      decoration: BoxDecoration(
        color: isCurrent ? Colors.green[100] : Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 4)],
      ),
      child: Row(
        children: [
          SizedBox(
            width: 36,
            child: Text(entry.rank > 0 ? entry.rank.toString() : visibleIndex.toString(),
                style: const TextStyle(fontWeight: FontWeight.bold)),
          ),
          const SizedBox(width: 12),
          CircleAvatar(
            backgroundImage: NetworkImage(entry.avatarUrl),
            child: null,
          ),
          const SizedBox(width: 12),
          Expanded(child: Text(entry.nickname)),
          const SizedBox(width: 12),
          Text('${entry.xp}'),
        ],
      ),
    );
  }
}


