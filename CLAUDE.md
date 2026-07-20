# CLAUDE.md

Operational notes for working in this repo. For a broader human-oriented tour, see `ONBOARDING.md`.

## What this is
Native **Android** app (Kotlin) — a daily micro-journaling / reflection app for parents.
Product spec: `Product Requirements Document (PRD).txt`.

## Stack
- Kotlin `2.2.0`, Gradle (Kotlin DSL) via wrapper `8.14.3`, Android Gradle Plugin `8.12.0`.
- `minSdk 33`, `compileSdk`/`targetSdk 36`, JVM target `11`.
- UI is **classic Android Views**: XML layouts + `ConstraintLayout` + `AppCompatActivity` + `findViewById`.
  Compose and View Binding are toggled on in `app/build.gradle.kts` but **no screen uses them yet** — don't
  assume they're wired in. The Gemini `generativeai` dependency is declared but **no AI code exists yet**.
- App id / namespace: `com.positiveparenting`.

## Build / lint / test
Use the wrapper, never a global `gradle`. Requires JDK 17+ and the Android SDK (see the SessionStart hook).
```bash
./gradlew assembleDebug          # build debug APK
./gradlew lint                   # Android lint
./gradlew test                   # JVM unit tests
./gradlew connectedAndroidTest   # instrumented tests (needs device/emulator)
```
There are **no real tests yet** — only the default JUnit/Espresso deps, and no `app/src/test/` or
`app/src/androidTest/` dirs. Create them when adding tests.

## Conventions
- **Dependencies go through the version catalog** `gradle/libs.versions.toml`, referenced as `libs.…` in
  `app/build.gradle.kts`. (Lottie is the one hardcoded exception today.)
- User-facing text → `app/src/main/res/values/strings.xml`; colors → `colors.xml`. No hardcoded UI strings.
- View ids use `snake_case` (`lets_go_button`, `title_textview`).
- One activity ↔ one `res/layout/activity_*.xml`, wired with `findViewById`.
- **Every new screen must be registered** in `app/src/main/AndroidManifest.xml` (`exported="false"` unless it's
  a launcher/deep-link target). Only the onboarding flow is registered today.
- Kotlin official code style (`kotlin.code.style=official`).

## Code map (feature packages under `app/src/main/java/com/positiveparenting/`)
- `onboarding/` — the only fully wired flow. Launcher is `OnboardingActivity` →
  `OnboardingStep2Activity` → `OnboardingStep3Activity` → `AccountCreationActivity`.
- `journal/` — `JournalOverviewActivity`, `JournalEditorActivity`: **stubs** (`setContentView` commented out),
  not in the manifest.
- `insights/InsightsActivity` — has a layout, not yet in the manifest.
- `settings/SettingsActivity` — stub.

## Secrets
API keys (e.g. Gemini) belong in `local.properties` (git-ignored), surfaced via `BuildConfig` through the
secrets-gradle-plugin. **Never commit keys** or a `local.properties`.

## Git
Solo project; work lands on `main`. `local.properties`, `/build`, `.gradle`, and most of `.idea/` are
git-ignored — don't commit build output or machine-local config.
