# Backlog

Technische Aufgaben und Fixes. **Ausgearbeitete Anforderungen** stehen in
[`anforderungen/README.md`](./anforderungen/README.md) und entstehen über den
festen [Refinement-Prozess](./PROZESS.md) — die Trennlinie steht dort unter
„Anforderung oder Aufgabe? Der Test".

Stand: 2026-07-31. Reihenfolge = Priorität.

## Hoch — irreführender Projektzustand

Diese drei Punkte kosten nichts außer Aufräumen, aber jeder von ihnen lässt das
Projekt nach einer Entscheidung aussehen, die nie getroffen wurde:

- [x] **Compose und View Binding sind aktiviert, aber ungenutzt.** *Erledigt
  2026-07-31:* abgeschaltet — Compose-Plugin, Compose-Abhängigkeiten und beide
  `buildFeatures`-Flags entfernt, passend zu
  [ADR-001](./entscheidungen/ADR-001-native-android-klassische-views.md).
- [x] **Gemini-Abhängigkeit (`generativeai`) ist deklariert, aber es gibt keinen
  KI-Code.** *Erledigt 2026-07-31:* entfernt (Dependency und Katalog-Eintrag).
  Kommt erst mit einer verfeinerten A-8 zurück. Das secrets-gradle-plugin bleibt
  für künftige Keys bestehen.
- [x] **`web/` fehlt in `CLAUDE.md`.** *Erledigt:* `CLAUDE.md` beschreibt `web/`
  inzwischen; seit [ADR-003](./entscheidungen/ADR-003-zwei-plattformen.md)
  (akzeptiert, Option A) ist es zudem per `web/README.md` als Design-Prototyp
  gekennzeichnet.

## Mittel — Grundlagen

- [x] **Test-Suite anlegen.** *Erledigt 2026-08-01:* `app/src/test/` existiert
  seit [A-10](./anforderungen/A-10-lokales-profil.md) (`LocalProfileTest`),
  seit A-1 dazu `PromptProviderTest`; `app/src/androidTest/` existiert seit
  [A-1](./anforderungen/A-1-eintrag-schreiben-und-speichern.md)
  (`JournalEntryDaoTest` gegen In-Memory-Room).
- [ ] **Lottie über den Version-Katalog ziehen.** Heute die einzige hartkodierte
  Abhängigkeit in `app/build.gradle.kts`; alle anderen laufen über
  `gradle/libs.versions.toml`.
- [ ] **Stub-Activities registrieren, sobald sie Inhalt haben.**
  `InsightsActivity` und `SettingsActivity` fehlen im `AndroidManifest.xml`
  (`exported="false"`). Ein unregistrierter Screen ist nicht erreichbar — das
  gehört in die Definition of Done jeder Anforderung, die einen Screen baut.
  (`JournalEditorActivity` ist seit A-1 registriert, `JournalOverviewActivity`
  seit A-2.)

- [x] **Edge-to-Edge und Disabled-Kontrast auf den Onboarding-Screens prüfen.**
  *Erledigt 2026-08-01:* alle Onboarding-Layouts und der Editor haben
  `fitsSystemWindows` (seit `targetSdk 35+` zeichnet Android erzwungen randlos);
  Speichern-Knöpfe (Editor + Profil) nutzen explizite Farb-Zustände
  (`save_button_*.xml`), Hints sind Weiß statt Theme-Lila. Außerdem: Mandala
  loopt wieder, Herz (Step 2) pulsiert per `ObjectAnimator`, und „Profil
  speichern" leitet direkt in den Editor weiter statt einen Neustart zu
  verlangen.

## Niedrig

- [ ] **`.idea/` prüfen.** Teilweise eingecheckt; nur die geteilten Teile gehören
  in die Versionskontrolle, maschinenlokale nicht.
