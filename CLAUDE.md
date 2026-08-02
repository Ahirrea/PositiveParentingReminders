# CLAUDE.md

Operational notes for working in this repo. For a broader human-oriented tour, see `docs/ONBOARDING.md`.

## What this is
Native **Android** app (Kotlin) — a daily micro-journaling / reflection app for parents.
Product spec: `docs/PRD.md`.

There is also a **`web/` directory**: a Next.js 14 project (`positive-parenting-web`)
that re-implements the same onboarding flow in React/TypeScript/Tailwind. It shares
no code with `app/` — per **ADR-003 (accepted)** it is a **design prototype only**,
not maintained alongside the app; see `web/README.md` and
`docs/entscheidungen/ADR-003-zwei-plattformen.md`. Android is the product.

## Requirements engineering (`docs/`)

The `docs/` layout is identical across all four projects — Maschinell,
PositiveParentingReminders, lieferkarte-karlsruhe, BauWatch-KA. It separates by rate
of change. **These documents are written in German**, unlike the code and this file.

| Layer | Path | Rate of change |
|---|---|---|
| Why & what | `docs/PRD.md` | rarely |
| How it was decided | `docs/entscheidungen/` | **append-only** |
| What's next | `docs/anforderungen/` | fluid |
| Technical tasks | `docs/BACKLOG.md` | fluid |

- **Requirements go through `docs/PROZESS.md`, not straight into code.** A
  non-trivial idea — even phrased as a question ("could we do X?") — gets refined
  first: survey the code, name the tensions with the non-goals in `docs/PRD.md`,
  lay out options with a recommendation, let the product owner decide the open
  questions, then **its own file** `docs/anforderungen/A-<n>-<short-title>.md` plus
  a row in `docs/anforderungen/README.md` (status lives **only** there).
  **Implementation only on explicit green light.** The `/anforderung` slash command
  runs the process.
- Small fixes and technical chores go straight into `docs/BACKLOG.md`; the dividing
  line is in the process file ("Anforderung oder Aufgabe? Der Test").
- Architectural decisions become an ADR in `docs/entscheidungen/`. That folder is
  **append-only** — an ADR is never rewritten; if a decision is reversed, write a
  new ADR and set the old one's `Status:` to `ersetzt durch ADR-<n>`.
- **Raw ideas need no file**, just a row in the overview table with status `💡 Idee`.
- ADR-002 and ADR-003 were **decided on 2026-07-31**: the account step becomes a
  **local profile** (no Google login, no backend — implemented as A-10, done),
  and Android is the product while `web/` is a design prototype.

> The previous product spec, `Product Requirements Document (PRD).txt`, described a
> different product than the one in this repo — cross-platform framework, iOS +
> Android, Firebase backend, LLM integration, E2E encryption, plus DAU/MAU and NPS
> targets. It is preserved verbatim at
> `docs/archiv/PRD-urspruenglich-2026-07.txt`; `docs/PRD.md` (v0.2) replaces it and
> describes what is actually being built.

## Stack
- Kotlin `2.2.0`, Gradle (Kotlin DSL) via wrapper `8.14.3`, Android Gradle Plugin `8.12.0`.
- `minSdk 33`, `compileSdk`/`targetSdk 36`, JVM target `11`.
- UI is **classic Android Views**: XML layouts + `ConstraintLayout` + `AppCompatActivity` + `findViewById`
  (ADR-001). Compose and View Binding are **off** (removed 2026-07-31 after being enabled-but-unused).
  There is **no AI code and no AI dependency** — Gemini was removed until A-8 is refined.
- Persistence is **Room** (ADR-004, added with A-1) via the KSP plugin. The schema history
  is checked in under `app/schemas/` — schema changes go through **migrations**, never
  `fallbackToDestructiveMigration`. The database is excluded from Android backup
  (`backup_rules.xml` / `data_extraction_rules.xml`): no entry ever leaves the device.
  Do **not** add `room-ktx` — its APIs (suspend DAOs) live in `room-runtime` since Room 2.7.
- App id / namespace: `com.positiveparenting`.

## Build / lint / test
Use the wrapper, never a global `gradle`. Requires JDK 17+ and the Android SDK (see the SessionStart hook).

In Claude Code web sessions, `./gradlew` only works if the environment's network
policy allows `dl.google.com` (Android SDK **and** Google Maven: AGP, Room, androidx)
and `services.gradle.org` (+ its `github.com` redirect). Before building, check the
SessionStart hook actually finished (`local.properties` with `sdk.dir` exists — the
hook can die silently). If Google hosts are blocked, pure-JVM code can still be
verified manually: compile and run the `app/src/test/` tests with kotlin-compiler +
JUnit fetched from Maven Central (reachable by default).
```bash
./gradlew assembleDebug          # build debug APK
./gradlew lint                   # Android lint
./gradlew test                   # JVM unit tests
./gradlew connectedAndroidTest   # instrumented tests (needs device/emulator)
```
Known flake: the **first** build after a new Room schema version can fail in
`kspDebugKotlin` with `JsonDecodingException … 'EOF'` even though the schema JSON under
`app/schemas/` was written correctly — just rerun; don't debug the (valid) JSON.

JVM tests live in `app/src/test/` (`LocalProfileTest` since A-10, `PromptProviderTest`
since A-1, `EntryDateFormatterTest` since A-2, `ReminderTimeCalculatorTest` since A-3). Instrumented tests live in `app/src/androidTest/` (`JournalEntryDaoTest`
against in-memory Room, since A-1) — they need a device/emulator, which Claude Code web
sessions don't have; there, `assembleDebugAndroidTest` at least verifies they compile.

## Conventions
- **Dependencies go through the version catalog** `gradle/libs.versions.toml`, referenced as `libs.…` in
  **both** the root `build.gradle.kts` (plugin aliases) and `app/build.gradle.kts`. When removing a catalog
  entry, grep for its `libs.…` references first — a stale `kotlin.compose` alias in the root file once broke
  every Gradle invocation. (Lottie is the one hardcoded exception today.)
- User-facing text → `app/src/main/res/values/strings.xml`; colors → `colors.xml`. No hardcoded UI strings.
- UI-string language is mixed by design: the journal editor, `daily_prompts` and the
  profile step (A-10) are **German** (the target user writes German); onboarding
  steps 1–3 are still English. New user-facing strings: German.
- View ids use `snake_case` (`lets_go_button`, `title_textview`).
- One activity ↔ one `res/layout/activity_*.xml`, wired with `findViewById`.
- **Every new screen must be registered** in `app/src/main/AndroidManifest.xml` (`exported="false"` unless it's
  a launcher/deep-link target). Registered today: the onboarding flow, `JournalEditorActivity`,
  `JournalOverviewActivity`, and the two `reminder/` receivers (A-3).
- Kotlin official code style (`kotlin.code.style=official`).

## Code map (feature packages under `app/src/main/java/com/positiveparenting/`)
- `onboarding/` — launcher flow, runs once: `OnboardingActivity` →
  `OnboardingStep2Activity` → `OnboardingStep3Activity` → `ProfileSetupActivity`
  (local profile, ADR-002/A-10). Once `onboarding_complete` is set, the launcher
  redirects straight into `JournalEditorActivity` (A-1).
- `profile/` — `LocalProfile` (pure Kotlin, JVM-tested) + `LocalProfileStore`
  (SharedPreferences: names + `onboarding_complete` flag).
- `data/` — Room layer (ADR-004): `JournalEntry` entity, `JournalEntryDao`,
  `AppDatabase` singleton (`journal.db`, version 1).
- `journal/` — `JournalEditorActivity`: **fully wired** (A-1: date + daily prompt,
  multiline text, optional 5-step mood, save to Room) + `PromptProvider` (pure,
  JVM-tested date rotation over the `daily_prompts` array).
  `JournalOverviewActivity`: **fully wired** (A-2: all entries newest first as read-only
  cards, RecyclerView + `JournalEntryAdapter`, reached via the editor's "Meine Einträge"
  button) + `EntryDateFormatter` (pure, JVM-tested timestamp formatting).
- `reminder/` — daily reminder (A-3): purely **local** notification at 20:00
  (`AlarmManager.setInexactRepeating`, no exact-alarm permission, no push service, no
  WorkManager). `ReminderTimeCalculator` (pure, JVM-tested next-trigger logic),
  `ReminderScheduler` (idempotent arming; time constant becomes configurable with A-6),
  `DailyReminderReceiver` (shows the day's prompt via `PromptProvider`, tap opens the
  editor, **skips when today already has an entry**), `ReminderRescheduleReceiver`
  (re-arms after reboot/time change). Permission `POST_NOTIFICATIONS` is requested
  exactly once, on the first editor launch; a denial is respected.
- `insights/InsightsActivity` — has a layout, not yet in the manifest.
- `settings/SettingsActivity` — stub.

## Secrets
API keys (e.g. Gemini) belong in `local.properties` (git-ignored), surfaced via `BuildConfig` through the
secrets-gradle-plugin. **Never commit keys** or a `local.properties`.

## Git
Solo project; work lands on `main`. `local.properties`, `/build`, `.gradle`, and most of `.idea/` are
git-ignored — don't commit build output or machine-local config.
