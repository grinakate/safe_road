# AGENTS.md

## Project snapshot
- Flutter app for the `safe_road` project (`lib/main.dart` is the entry point).
- App startup uses `get_it` (`lib/core/service_locator.dart`) and chooses the initial route from `AuthService.isAuthenticated()`.
- UI is split into `lib/screens/` for pages and `lib/widgets/` for reusable blocks like `RoadHeader`, `SectionHeader`, and dialogs.
- Network/API code lives in `lib/services/` and uses the shared `AppConstants.baseUrl` from `lib/core/constants.dart`.
- Assets are already organized under `assets/images/`, `assets/icons/`, and `assets/avatars/` and are registered in `pubspec.yaml`.

## Theme and UI conventions
- Use the centralized theme files under `lib/theme/` for colors, text styles, and `ThemeData`.
- Prefer `AppTheme.lightTheme` in `MaterialApp`, and use `AppColors` / `AppTextStyles` instead of raw `Colors.*` or ad-hoc `TextStyle` values.
- Keep screen-specific overrides minimal; only diverge when the UI truly needs a local exception.
- Reuse existing patterns: login/register forms, roadmap cards, bottom navigation, and achievement dialogs already show the intended design language.

## Architecture notes
- `MapScreen` loads profile + course map data in parallel and then builds the roadmap from `Section`/`Topic` models.
- `QuizScreen` submits answers through `QuizService` and navigates to `ResultScreen` with the returned score payload.
- `ProfileScreen` pulls user profile + avatars through `UserService` and opens `AvatarSelectionDialog` / `AchievementInfoDialog` for interactions.
- `SecureNetworkImage` is the standard way to load authenticated images; it prepends `AppConstants.baseUrl` and fetches auth headers via `ApiClient`.

## Developer workflow
- Fetch dependencies after edits that touch imports/assets/fonts:
  ```bash
  flutter pub get
  ```
- Validate code and formatting with:
  ```bash
  flutter analyze
  flutter test
  ```
- Run the app locally with:
  ```bash
  flutter run
  ```

## Working rules for agents
- Before changing UI, search for direct `Colors.*`, `TextStyle(...)`, and `styleFrom(...)` usages in the target area.
- Prefer changing shared theme primitives first, then update screens/widgets to consume them.
- Keep route names, service boundaries, and asset paths unchanged unless the task explicitly requires a migration.
- When adding new design tokens, place them in `lib/theme/app_colors.dart` or `lib/theme/app_text_styles.dart` rather than scattering constants across screens.

