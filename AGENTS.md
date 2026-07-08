# Repository Guidelines

## Project Structure & Module Organization
Carto is a single-module Android app in `app/` using Kotlin, Jetpack Compose, MVVM, Hilt, Room, Retrofit/OkHttp, Apollo GraphQL, Firebase, Mapbox, and Paymob. Main source lives under `app/src/main/java/com/shopify/carto/`, grouped by `core/`, `feature/`, `navigation/`, `ui/theme/`, and app setup. Resources are in `app/src/main/res/`; GraphQL operations and schemas are in `app/src/main/graphql/`; Room schemas are in `app/schemas/`. Unit tests belong in `app/src/test/`, instrumentation and Compose UI tests in `app/src/androidTest/`. The Supabase payment edge function is in `supabase/functions/create-payment/`.

## Build, Test, and Development Commands
Use JDK 17 and the checked-in Gradle wrapper.

- `./gradlew assembleDebug` or `.\gradlew.bat assembleDebug`: builds a debug APK.
- `./gradlew lintDebug`: runs Android lint; CI treats lint failures as blocking.
- `./gradlew testDebugUnitTest`: runs local JVM unit tests.
- `./gradlew connectedDebugAndroidTest`: runs device/emulator instrumentation tests.
- `./gradlew generateApolloSources`: regenerate Apollo models after GraphQL query or schema changes.
- `supabase functions serve create-payment`: run the Paymob proxy locally when working on the edge function.

## Coding Style & Naming Conventions
Use Kotlin idioms with 4-space indentation and Gradle Kotlin DSL. Keep feature code in `feature/<name>/{data,domain,presentation,di}` when possible. Name classes and composables in `UpperCamelCase`; functions, properties, and flows in `lowerCamelCase`. Follow existing suffixes such as `ViewModel`, `UiState`, `Event`, `Effect`, `RepositoryImpl`, `UseCase`, `Dao`, and `DIModule`. Keep Compose screens stateless where practical, with state owned by ViewModels and reusable UI split into `components/`.

## Testing Guidelines
Write focused JUnit4 tests for use cases, repositories, mappers, validation, Room DAOs, and coroutine/Flow behavior. Use `kotlinx-coroutines-test`, Room testing utilities, Hilt test support, and Compose UI tests where appropriate. Test files should mirror the source package and use names like `CheckoutViewModelTest` or `SearchProductMapperTest`. Run `lintDebug` and `testDebugUnitTest` before opening a PR; run `connectedDebugAndroidTest` for navigation, database, or UI behavior changes.

## Commit & Pull Request Guidelines
Recent history uses Conventional Commit-style subjects such as `feat: Add navigation for Order History and Order Details screens.` Prefer `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, or `chore:` with a short imperative summary. PRs should include a clear description, linked task or issue, testing performed, screenshots or screen recordings for UI changes, and notes about migrations, GraphQL schema updates, or configuration changes.

## Security & Configuration Tips
Do not commit secrets, tokens, or generated service credentials. Keep local values in `local.properties`, including Shopify, Mapbox, Paymob, and Supabase settings. CI creates `app/google-services.json` from the `GOOGLE_SERVICES_JSON` secret, so avoid adding that file to commits.
