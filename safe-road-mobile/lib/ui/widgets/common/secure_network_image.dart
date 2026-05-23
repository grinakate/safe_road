import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';

import '../../../core/constants.dart';
import '../../../core/service_locator.dart';
import '../../../data/services/storage/token_storage_service.dart';

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

  Future<Map<String, String>> _getHeaders() async {
    final token = await getIt<TokenStorageService>().getToken();
    return token != null ? {'Authorization': 'Bearer $token'} : {};
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<Map<String, String>>(
      future: _getHeaders(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return _buildLoading();
        }

        return CachedNetworkImage(
          imageUrl: '${AppConstants.baseUrl}$imageUrl',
          httpHeaders: snapshot.data ?? {},
          width: width,
          height: height,
          fit: fit,
          placeholder: (context, url) => _buildLoading(),
          errorWidget: (context, url, error) => _buildError(),
        );
      },
    );
  }

  Widget _buildLoading() => SizedBox(
    width: width,
    height: height,
    child: const Center(child: CircularProgressIndicator(strokeWidth: 2)),
  );

  Widget _buildError() => SizedBox(
    width: width,
    height: height,
    child: const Icon(Icons.emoji_events, size: 40),
  );
}
