import 'dart:convert';
import '../models/section.dart';
import 'api_client.dart';

class MapService {
  final ApiClient _apiClient;

  MapService(this._apiClient);

  Future<List<Section>> getMapForUser() async {
    final response = await _apiClient.get('/api/learning/map');

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => Section.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load map data: ${response.statusCode}');
    }
  }
}
