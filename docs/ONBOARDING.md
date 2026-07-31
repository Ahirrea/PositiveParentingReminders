# Onboarding — Positive Parenting Reminders

Welcome! This is the native **Android** app for *Positive Parenting Reminders* — a daily
micro-journaling and reflection app for parents. Parents get short guided prompts, log
their emotional responses, and (eventually) receive AI-generated insights on fostering
empathy and resilience. See [`PRD.md`](./PRD.md) for the full product vision — note that it
is written in German, as is the rest of `docs/`.

This guide gets a new engineer productive in the codebase quickly. It describes what
exists **today** and flags where the code is still scaffolding versus the PRD's end goal.
What to build next lives in [`anforderungen/README.md`](./anforderungen/README.md); the
process for turning an idea into a buildable requirement is [`PROZESS.md`](./PROZESS.md).

---

## Tech stack at a glance

| Area | Choice |
|------|--------|
| Language | Kotlin `2.2.0` |
| Build | Gradle (Kotlin DSL) `8.14.3`, Android Gradle Plugin `8.12.0` |
| UI | Android Views — XML layouts + `ConstraintLayout`, `AppCompatActivity`, `findViewById` |
| SDK | `minSdk 33`, `compileSdk`/`targetSdk 36`, JVM target `11` |
| Animations | [Lottie](https://airbnb.io/lottie/) `6.6.7` (`res/raw/*.json`, `*.lottie`) |
| Persistence | [Room](https://developer.android.com/training/data-storage/room) — local only, excluded from Android backup (ADR-004) |
| AI | none — deliberately deferred until the journal core works (see A-8) |
| App id / namespace | `com.positiveparenting` |

> **No Compose, no View Binding, no AI dependency.** Both toggles and the Gemini
> `generativeai` dependency were enabled-but-unused for a while and have been removed
> (2026-07-31). All screens are **classic XML + `findViewById`** — that is the deliberate
> choice, see ADR-001.

---

## Repository layout

```
app/
  build.gradle.kts                         # module build config, dependencies
  src/main/
    AndroidManifest.xml                    # onboarding flow + journal editor are registered
    java/com/positiveparenting/
      onboarding/                          # runs once, then forwards to the editor
        OnboardingActivity.kt              # LAUNCHER entry point
        OnboardingStep2Activity.kt
        OnboardingStep3Activity.kt
        AccountCreationActivity.kt         # completing it sets the onboarding_complete flag
        OnboardingPrefs.kt                 # SharedPreferences flag
      journal/
        JournalEditorActivity.kt           # A-1: prompt, text, mood, save to Room
        PromptProvider.kt                  # daily prompt rotation (epochDay mod size)
        JournalOverviewActivity.kt         # stub — setContentView is commented out
      data/                                # Room persistence (ADR-004)
        JournalEntry.kt                    # entity: text, mood (1–5, nullable), prompt
        JournalEntryDao.kt                 # insert only — reads come with A-2
        AppDatabase.kt                     # journal.db, excluded from Android backup
      insights/InsightsActivity.kt         # has a layout, not yet in the manifest
      settings/SettingsActivity.kt         # stub
    res/
      layout/                              # one activity_*.xml per screen
      values/                              # strings.xml, colors.xml, themes.xml
      raw/                                 # Lottie animation files
      drawable/, mipmap-*/                 # icons & vectors
gradle/libs.versions.toml                  # version catalog — add/bump deps HERE
web/                                       # design prototype only, not maintained (ADR-003)
docs/                                      # PRD, requirements, decisions, backlog (German)
```

Code is organized **by feature** (`onboarding`, `journal`, `insights`, `settings`), each
its own package under `com.positiveparenting`.

---

## How the app flows today

First start (onboarding not yet completed):

```
OnboardingActivity (LAUNCHER)  →  OnboardingStep2Activity  →  OnboardingStep3Activity  →  AccountCreationActivity  →  JournalEditorActivity
      "Let's go"                       "Next"                     "Understood"                "Create account"
```

Completing the last onboarding step sets the `onboarding_complete` SharedPreferences flag
(`OnboardingPrefs`). On every later start, `OnboardingActivity` forwards straight to
`JournalEditorActivity` — the core loop (A-1): see today's prompt, write a few sentences,
optionally tap a mood, save to the local Room database.

Each activity is a thin `AppCompatActivity`: it calls `setContentView(R.layout.…)` and wires
its views with `findViewById`.

`JournalOverviewActivity`, `insights/`, and `settings/` exist as classes but are **not**
registered in the manifest — they are placeholders for the features described in the PRD
(A-2, A-7, A-6). Expect to flesh these out.

---

## Build & run

A JDK 17+ and the Android SDK are required (Android Studio bundles both). Use the Gradle
wrapper — never a globally installed `gradle`.

```bash
# Build the debug APK
./gradlew assembleDebug

# Install & run on a connected device / emulator (API 33+)
./gradlew installDebug

# Static analysis
./gradlew lint

# Unit tests (JVM) and instrumented tests (device/emulator)
./gradlew test
./gradlew connectedAndroidTest
```

The easiest path is to open the project root in **Android Studio**, let it sync Gradle, then
Run the `app` configuration on an emulator (API 33+).

> **Note:** JVM unit tests live under `app/src/test/` (e.g. `PromptProviderTest`),
> instrumented tests under `app/src/androidTest/` (e.g. `JournalEntryDaoTest` against an
> in-memory Room database — needs a device/emulator).

---

## Conventions

- **Dependencies live in the version catalog.** Add or bump versions in
  `gradle/libs.versions.toml` and reference them as `libs.…` in `build.gradle.kts`. (Lottie
  is currently the one hardcoded exception.)
- **Kotlin style:** official (`kotlin.code.style=official` in `gradle.properties`).
- **Resources over hardcoded strings:** user-facing text goes in `res/values/strings.xml`;
  colors in `colors.xml`. Follow the existing `snake_case` id naming for views
  (`lets_go_button`, `title_textview`).
- **One activity ↔ one `activity_*.xml` layout**, wired with `findViewById`.
- **New screens must be registered** in `AndroidManifest.xml` (`exported="false"` unless it's
  a launcher/deep-link target).

---

## Secrets & future AI integration (when you get there)

`build.gradle.kts` applies the **secrets-gradle-plugin**. API keys belong in
`local.properties` (git-ignored) rather than in source or the manifest, and are surfaced
via `BuildConfig` (`buildConfig = true` is enabled). Do **not** commit keys.

An LLM (originally Gemini) was planned for sentiment/thematic analysis and generating
insights — **none of that is implemented, and it is deliberately deferred**: `PRD.md` makes
"no AI until the journal core works" a non-goal, because insights over an empty journal say
nothing. The once declared-but-unused `generativeai` dependency has been removed; it comes
back only with a refined [A-8](./anforderungen/README.md#übersicht).

---

## Git workflow

- Default branch: `main`. History shows work merged via PRs from short-lived
  `feature/*` branches (e.g. `feature/onboarding-screen`, `feat/onboarding-step3`).
- Branch off `main`, keep changes focused, open a PR.
- `local.properties`, `/build`, `.gradle`, and most of `.idea/` are git-ignored — don't
  commit build output or machine-local config.

---

## Good first steps

1. Open in Android Studio, sync, and run the onboarding flow on an emulator to see the
   Lottie-animated screens.
2. Read `OnboardingActivity` → `OnboardingStep2Activity` → `OnboardingStep3Activity` to see
   the navigation pattern — it's the template every screen follows.
3. Skim [`PRD.md`](./PRD.md) — especially the **non-goals**, they are the part that
   constrains what you build.
4. Read [`anforderungen/README.md`](./anforderungen/README.md) to see what is queued.
   A-1 (write and store an entry) is the foundation everything else needs.
5. Pick up a stub (`JournalOverviewActivity` / `SettingsActivity`): give it a layout,
   register it in the manifest, and wire it into the flow. Non-trivial work goes through
   [`PROZESS.md`](./PROZESS.md) first.
