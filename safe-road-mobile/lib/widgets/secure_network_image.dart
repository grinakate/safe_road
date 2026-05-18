import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';

import '../core/constants.dart';
import '../services/api_client.dart';

class SecureNetworkImage extends StatelessWidget {
  final String imageUrl;
  final double? width;
  final double? height;
  final BoxFit fit;

  const SecureNetworkImage({
    super.key,
    required this.imageUrl,
    this.width,
    this.height,
    this.fit = BoxFit.contain,
  });

  @override
  Widget build(BuildContext context) {
    final apiClient = GetIt.I<ApiV1Client>();

    return FutureBuilder<Map<String, String>>(
      future: apiClient.getAuthHeaders(),
      builder: (context, snapshot) {
        // Пока ждем заголовки, показываем индикатор загрузки
        if (snapshot.connectionState == ConnectionState.waiting) {
          return SizedBox(
            width: width,
            height: height,
            child: const Center(
              child: CircularProgressIndicator(strokeWidth: 2),
            ),
          );
        }
        if (snapshot.hasError || !snapshot.hasData) {
          // Если возникла ошибка при получении заголовков
          return SizedBox(
            width: width,
            height: height,
            child: const Icon(
              Icons.error_outline,
              size: 40,
            ), // Ошибка получения заголовков
          );
        }

        // Если заголовки успешно получены, используем CachedNetworkImage
        return CachedNetworkImage(
          imageUrl: '${AppConstants.baseUrl}$imageUrl',
          httpHeaders: snapshot.data,
          // Передаем заголовки
          width: width,
          height: height,
          fit: fit,
          // Placeholder, пока изображение загружается по сети
          placeholder: (context, url) => SizedBox(
            width: width,
            height: height,
            child: const Center(
              child: CircularProgressIndicator(strokeWidth: 2),
            ),
          ),
          errorWidget: (context, url, error) => SizedBox(
            width: width,
            height: height,
            child: const Icon(Icons.emoji_events, size: 40),
          ),
        );
      },
    );
  }
}
