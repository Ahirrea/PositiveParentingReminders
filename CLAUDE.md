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
  **local profile** (no Google login, no backend — implementation tracked as A-10),
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
JVM unit tests live in `app/src/test/` (e.g. `PromptProviderTest`), instrumented tests in
`app/src/androidTest/` (e.g. `JournalEntryDaoTest` against in-memory Room).

## Conventions
- **Dependencies go through the version catalog** `gradle/libs.versions.toml`, referenced as `libs.…` in
  `app/build.gradle.kts`. (Lottie is the one hardcoded exception today.)
- User-facing text → `app/src/main/res/values/strings.xml`; colors → `colors.xml`. No hardcoded UI strings.
- UI-string language is mixed by design: the journal editor and `daily_prompts` are
  **German** (per the A-1 spec — the target user writes German); the onboarding flow
  is still English and gets reworked with A-10. New user-facing strings: German.
- View ids use `snake_case` (`lets_go_button`, `title_textview`).
- One activity ↔ one `res/layout/activity_*.xml`, wired with `findViewById`.
- **Every new screen must be registered** in `app/src/main/AndroidManifest.xml` (`exported="false"` unless it's
  a launcher/deep-link target). Registered today: the onboarding flow and `JournalEditorActivity`.
- Kotlin official code style (`kotlin.code.style=official`).

## Code map (feature packages under `app/src/main/java/com/positiveparenting/`)
- `onboarding/` — launcher flow: `OnboardingActivity` → `OnboardingStep2Activity` →
  `OnboardingStep3Activity` → `AccountCreationActivity`. Runs once: completing it sets the
  `onboarding_complete` SharedPreferences flag (`OnboardingPrefs`); afterwards the launcher
  forwards straight to the journal editor.
- `journal/` — `JournalEditorActivity` (A-1): daily prompt (`PromptProvider`, rotates over the
  `daily_prompts` string-array by epoch day), multiline text, optional 5-step mood, saves to Room.
  `JournalOverviewActivity` is still a **stub** (`setContentView` commented out), not in the manifest.
- `data/` — Room persistence (ADR-004): `JournalEntry` entity, `JournalEntryDao`, `AppDatabase`
  singleton (`journal.db`, version 1, schema snapshots in `app/schemas/`). The DB is excluded from
  Android backup/device transfer (`res/xml/backup_rules.xml`, `data_extraction_rules.xml`) — entries
  must never leave the device.
- `insights/InsightsActivity` — has a layout, not yet in the manifest.
- `settings/SettingsActivity` — stub.

## Secrets
API keys (e.g. Gemini) belong in `local.properties` (git-ignored), surfaced via `BuildConfig` through the
secrets-gradle-plugin. **Never commit keys** or a `local.properties`.

## Git
Solo project; work lands on `main`. `local.properties`, `/build`, `.gradle`, and most of `.idea/` are
git-ignored — don't commit build output or machine-local config.
