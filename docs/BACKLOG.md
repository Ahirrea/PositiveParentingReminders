# Backlog

Technische Aufgaben und Fixes. **Ausgearbeitete Anforderungen** stehen in
[`anforderungen/README.md`](./anforderungen/README.md) und entstehen über den
festen [Refinement-Prozess](./PROZESS.md) — die Trennlinie steht dort unter
„Anforderung oder Aufgabe? Der Test".

Stand: 2026-07-25. Reihenfolge = Priorität.

## Hoch — irreführender Projektzustand

Diese drei Punkte kosten nichts außer Aufräumen, aber jeder von ihnen lässt das
Projekt nach einer Entscheidung aussehen, die nie getroffen wurde:

- [ ] **Compose und View Binding sind aktiviert, aber ungenutzt.** In
  `app/build.gradle.kts` eingeschaltet, **kein** Screen benutzt sie. Entweder
  bewusst nutzen (dann als ADR) oder abschalten. Siehe
  [ADR-001](./entscheidungen/ADR-001-native-android-klassische-views.md).
- [ ] **Gemini-Abhängigkeit (`generativeai`) ist deklariert, aber es gibt keinen
  KI-Code.** Bis A-8 verfeinert ist, gehört sie entfernt — eine deklarierte
  Abhängigkeit ohne Nutzung sieht wie eine Integration aus.
- [ ] **`web/` fehlt in `CLAUDE.md`.** Ein vollständiges Next.js-Projekt, von dem
  die Anleitung nichts weiß, ist eine Falle für die nächste Sitzung. Hängt an
  [ADR-003](./entscheidungen/ADR-003-zwei-plattformen.md).

## Mittel — Grundlagen

- [ ] **Test-Suite anlegen.** Es gibt weder `app/src/test/` noch
  `app/src/androidTest/`, nur die Standard-JUnit-/Espresso-Abhängigkeiten. Der
  erste echte Test entsteht sinnvollerweise mit A-1 (Persistenz ist testbar, UI
  weniger).
- [ ] **Lottie über den Version-Katalog ziehen.** Heute die einzige hartkodierte
  Abhängigkeit in `app/build.gradle.kts`; alle anderen laufen über
  `gradle/libs.versions.toml`.
- [ ] **Stub-Activities registrieren, sobald sie Inhalt haben.**
  `JournalOverviewActivity`, `JournalEditorActivity`, `InsightsActivity`,
  `SettingsActivity` fehlen im `AndroidManifest.xml` (`exported="false"`). Ein
  unregistrierter Screen ist nicht erreichbar — das gehört in die Definition of
  Done jeder Anforderung, die einen Screen baut.

## Niedrig

- [ ] **`.idea/` prüfen.** Teilweise eingecheckt; nur die geteilten Teile gehören
  in die Versionskontrolle, maschinenlokale nicht.
