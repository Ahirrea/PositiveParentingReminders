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
| AI (planned) | Google Gemini via `com.google.ai.client.generativeai` |
| App id / namespace | `com.positiveparenting` |

> **Heads-up on "enabled but unused" features.** `build.gradle.kts` turns on Jetpack
> Compose (`buildFeatures.compose = true`) and View Binding (`viewBinding = true`), and the
> Gemini `generativeai` dependency is declared. As of today the screens are all
> **classic XML + `findViewById`** and **no Gemini calls exist yet**. Don't assume Compose
> or AI is wired in just because the toggles are on — check the actual screen first.

---

## Repository layout

```
app/
  build.gradle.kts                         # module build config, dependencies
  src/main/
    AndroidManifest.xml                    # only the onboarding flow is registered today
    java/com/positiveparenting/
      onboarding/                          # the only fully wired flow
        OnboardingActivity.kt              # LAUNCHER entry point
        OnboardingStep2Activity.kt
        OnboardingStep3Activity.kt
        AccountCreationActivity.kt
      journal/                             # stubs — setContentView is commented out
        JournalOverviewActivity.kt
        JournalEditorActivity.kt
      insights/InsightsActivity.kt         # has a layout, not yet in the manifest
      settings/SettingsActivity.kt         # stub
    res/
      layout/                              # one activity_*.xml per screen
      values/                              # strings.xml, colors.xml, themes.xml
      raw/                                 # Lottie animation files
      drawable/, mipmap-*/                 # icons & vectors
gradle/libs.versions.toml                  # version catalog — add/bump deps HERE
web/                                       # Next.js re-implementation of onboarding (see ADR-003)
docs/                                      # PRD, requirements, decisions, backlog (German)
```

Code is organized **by feature** (`onboarding`, `journal`, `insights`, `settings`), each
its own package under `com.positiveparenting`.

---

## How the app flows today

The only navigable path wired into `AndroidManifest.xml`:

```
OnboardingActivity (LAUNCHER)  →  OnboardingStep2Activity  →  OnboardingStep3Activity  →  AccountCreationActivity
      "Let's go"                       "Next"                     "Understood"
```

Each activity is a thin `AppCompatActivity`: it calls `setContentView(R.layout.…)` and wires
one button's `setOnClickListener` to `startActivity(Intent(...))` for the next screen.

`journal/`, `insights/`, and `settings/` exist as classes but are **not** registered in the
manifest and several have their `setContentView` commented out — they are placeholders for
the journaling and insights features described in the PRD. Expect to flesh these out.

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

> **Note:** There are currently **no meaningful tests** — only the default JUnit/Espresso
> dependencies. If you add logic worth testing, put unit tests under `app/src/test/` and
> instrumented tests under `app/src/androidTest/` (create those dirs; they don't exist yet).

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

## Secrets & the Gemini integration (when you get there)

`build.gradle.kts` applies the **secrets-gradle-plugin**. API keys (e.g. a Gemini key) belong
in `local.properties` (git-ignored) rather than in source or the manifest, and are surfaced
via `BuildConfig` (`buildConfig = true` is enabled). Do **not** commit keys.

The Gemini LLM was originally planned for sentiment/thematic analysis and generating
insights — **none of that is implemented, and it is deliberately deferred**: `PRD.md` makes
"no AI until the journal core works" a non-goal, because insights over an empty journal say
nothing. The declared-but-unused `generativeai` dependency is on the
[backlog](./BACKLOG.md) to be removed until [A-8](./anforderungen/README.md#übersicht) is
refined.

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
