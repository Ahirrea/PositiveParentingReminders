# A-2 Journal-Übersicht

[← Anforderungen](./README.md) · [Prozess](../PROZESS.md)
· Status siehe [Übersicht](./README.md#übersicht)

**User Story:** Als reflektierendes Elternteil möchte ich meine bisherigen
Einträge chronologisch wiederlesen können, um zu sehen, was ich festgehalten
habe — die Vorstufe zum Muster-Erkennen im Rückblick (A-7).

**Verfeinert am:** 2026-08-01
**Bedient PRD:** „5 Kernschleife" Schritt 3 (Vorstufe: Wiederlesen vor
Muster-Erkennen) · „3 Ziele — Rückblick über Wochen und Monate"
**Eingeschränkt durch:** [ADR-001](../entscheidungen/ADR-001-native-android-klassische-views.md)
(klassische Views) ·
[ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md) (Room)

> **Hinweis zum Entscheidungsweg:** Die Umsetzung wurde von der Ideengeberin
> ausdrücklich beauftragt („A-2 umsetzen"), ohne dass die Weichen vorab einzeln
> abgestimmt wurden. Die Entscheidungen unten sind deshalb die begründeten
> Empfehlungen der Umsetzung; jede ist klein genug, um bei Nichtgefallen ohne
> Datenmodell-Folgen revidiert zu werden (reine UI-/Query-Entscheidungen).

## Andockpunkte im Code

- `journal/JournalOverviewActivity.kt` — **Stub**: `setContentView`
  auskommentiert, nicht im Manifest. Wird der Kern dieser Anforderung.
- `res/layout/activity_journal_overview.xml` — Platzhalter (zentrierter
  `TextView` mit hartkodiertem Text). Wird ersetzt.
- `data/JournalEntry` — trägt bereits alles, was die Liste zeigt:
  `createdAtEpochMillis`, `text`, `mood` (1–5, nullable), `prompt` (nullable).
  **Kein Schemawechsel nötig, keine Migration.**
- `data/JournalEntryDao` — hat nur `insert` und `findById`; der Kommentar dort
  kündigt die Listen-Query für A-2 an. **Fehlt:** eine sortierte Abfrage.
- `journal/JournalEditorActivity` — der Startbildschirm nach dem Onboarding;
  bekommt den Einstiegspunkt in die Übersicht. Der Activity-Bauplan
  (dünne `AppCompatActivity`, `findViewById`, dunkles Layout auf
  `animation_background`, weiße Typo) ist wiederverwendbar.
- `journal/PromptProvider` + `PromptProviderTest` — Vorbild für „kleine pure
  Funktion, JVM-getestet"; die Datumsformatierung der Liste folgt dem Muster.
- **Fehlt komplett:** RecyclerView-Abhängigkeit im Katalog (Material zieht sie
  nur transitiv), Item-Layout, Adapter, Leerzustand.

## Spannung zu Nicht-Zielen — und Auflösung

- **„Kein Backend, keine Cloud":** kein Konflikt — die Liste liest dieselbe
  lokale Room-DB, nichts verlässt das Gerät. Keine neuen Berechtigungen.
- **„Ein Eintrag dauert unter drei Minuten":** die Übersicht darf den
  Schreibfluss nicht verstellen. Deshalb bleibt der Editor der Startbildschirm
  (Entscheidung aus A-1) und die Übersicht ist ein bewusster Abzweig — ein
  dezenter Text-Knopf unterhalb des Speicherns, kein zweiter Pflicht-Screen.
- **„Keine KI":** kein Berührungspunkt — reine Anzeige vorhandener Daten.
- **Rückblick (A-7) nicht vorwegnehmen:** die Übersicht ist bewusst *dumm* —
  chronologische Liste, keine Aggregation, keine Wochen-/Monatssichten, keine
  Statistik. Alles Auswertende bleibt bei A-7.

## Entscheidungen (mit Begründung)

1. **Neueste zuerst.** Beim Wiederlesen interessiert zuerst das Gestern, nicht
   der erste Eintrag von vor Wochen. Sortierung `createdAtEpochMillis DESC`,
   Gleichstand über `id DESC` (mehrere Einträge pro Tag sind erlaubt).
   Verworfen: älteste zuerst (liest sich wie ein Archiv, nicht wie ein
   Tagebuch, das man abends aufschlägt).
2. **Die Liste zeigt den vollen Eintragstext — kein Detail-Screen.** Einträge
   sind per Produktdefinition zwei bis drei Sätze; Abschneiden + Detailansicht
   wäre ein zweiter Screen ohne Mehrwert. Verworfen: Vorschau mit
   „Mehr"-Navigation (mehr Code, mehr Tipps, kein Nutzen bei Kurztexten).
3. **Nur lesen.** Bearbeiten und Löschen sind **raus** — Bearbeiten wirft die
   Frage nach Verlaufs-Ehrlichkeit auf (ein Reflexionstagebuch nachträglich
   zu glätten untergräbt den Zweck) und Löschen gehört zum
   Datenhoheits-Paket A-9/A-6. Wenn gewünscht, als eigene Weiche nachziehen.
4. **Einstieg: Text-Knopf „Meine Einträge" im Editor**, unterhalb des
   Speichern-Knopfs. Verworfen: Übersicht als Startbildschirm mit Weiter-zum-
   Editor (verstellt die Kernschleife und widerspricht der A-1-Entscheidung
   „App-Start → Editor"); App-Bar-Menü (der Editor hat bewusst keine App-Bar).
5. **Laden per einmaliger `suspend`-Query in `onResume`, kein `Flow`.** Die
   Liste ist nur sichtbar, wenn die Activity vorne ist; kommt man vom Editor
   zurück, lädt `onResume` frisch. Ein reaktiver `Flow` brächte hier keine
   sichtbare Verbesserung, aber mehr Maschinerie. Verworfen: `Flow` +
   `repeatOnLifecycle`.
6. **RecyclerView (`androidx.recyclerview`) explizit im Version-Katalog.**
   Material zieht sie ohnehin transitiv; die explizite Deklaration folgt der
   Konvention „Abhängigkeiten über den Katalog" und macht die Nutzung sichtbar.
   Verworfen: `ListView` (veraltetes API ohne DiffUtil), Wiederverwendung nur
   transitiv (unsichtbare Abhängigkeit).
7. **Zeitstempel im Listeneintrag mit festem deutschen Muster**
   („Donnerstag, 1. August 2024 · 02:00") über eine kleine pure Funktion
   (`EntryDateFormatter`, JVM-getestet, Zeitzone injizierbar). Mit Uhrzeit,
   weil mehrere Einträge pro Tag erlaubt sind. Festes Muster statt
   `ofLocalizedDateTime`, weil die Journal-Texte ohnehin Deutsch sind und das
   Muster deterministisch testbar ist (JDK-CLDR-Varianz umgangen).

## Umfang / Nicht-Umfang

- **Rein:** sortierte DAO-Query, `JournalOverviewActivity` verdrahten
  (RecyclerView, Adapter mit DiffUtil, Leerzustand), Item-Layout (Datum+Zeit,
  Stimmungs-Emoji, Impuls, Text), Einstiegs-Knopf im Editor,
  Manifest-Registrierung, deutsche Strings, Tests (DAO-Sortierung
  instrumentiert, Datumsformat JVM).
- **Raus (bewusst):** Bearbeiten/Löschen von Einträgen (→ Weiche s. o., Löschen
  → A-9/A-6), Suchen/Filtern, Gruppierung nach Woche/Monat und jede Auswertung
  (→ A-7), Paginierung (bei einem Eintrag/Tag sind selbst Jahre nur Hunderte
  Zeilen — RecyclerView recycelt; wird es je spürbar, ist `Paging` eine
  Backlog-Aufgabe), Export (→ A-9).

## Spezifikation

### UX-Ablauf & Zustände

Ein Screen, `JournalOverviewActivity`, im Stil des Editors (dunkler Grund,
weiße Typo):

1. **Kopf:** Titel „Meine Einträge".
2. **Liste:** ein Card-Eintrag pro Journal-Eintrag, neueste zuerst. Je Karte:
   - Kopfzeile: Zeitstempel (links, Caption) · Stimmungs-Emoji (rechts, nur
     wenn erfasst, mit `contentDescription` der Stufe).
   - Impuls des Tages (kursiv, Caption) — nur wenn gespeichert.
   - Eintragstext, voll ausgeschrieben.
3. **Leerzustand:** statt der Liste ein zentrierter Hinweis („Noch keine
   Einträge …"), wenn die DB leer ist.
4. **Zurück:** System-Back führt in den Editor zurück (normale Back-Stack-
   Semantik, kein eigener Up-Knopf nötig).

Einstieg: im Editor unterhalb des Speichern-Knopfs ein rahmenloser
Text-Knopf „Meine Einträge" → `startActivity(JournalOverviewActivity)`.
Der Editor bleibt dabei offen (kein `finish()`), Back kehrt zum angefangenen
Text zurück.

Zustände: *lädt* (kurzlebig, keine eigene UI — lokale Query) → *Liste* oder
*leer*. Fehlerpfad s. u.

### Interaktion mit Bestehendem

- `JournalEntryDao`: neue Query, **kein** Schemawechsel (Version bleibt 1):
  `SELECT * FROM journal_entries ORDER BY createdAtEpochMillis DESC, id DESC`.
- Editor-Layout: ein zusätzlicher `MaterialButton` (Textstil) unter
  `save_entry_button`; Editor-Activity registriert nur den Click-Listener.
- Neue Activity **im Manifest registrieren** (`exported="false"`).

### Datenmodell / Persistenz

Unverändert (`JournalEntry`, DB-Version 1). Nur eine neue lesende DAO-Methode
`entriesNewestFirst(): List<JournalEntry>` (suspend).

### Externe Abhängigkeiten & Fallback

`androidx.recyclerview:recyclerview` über `gradle/libs.versions.toml`
(bislang nur transitiv über Material vorhanden). Kein Netzwerk, kein Fallback.

### Randfälle & Fehlerbehandlung

- **Keine Einträge:** Leerzustand statt leerer Fläche.
- **Query schlägt fehl** (defekte DB o. Ä.): Toast mit Fehlermeldung, der
  Leerzustand bleibt sichtbar; die App stürzt nicht ab.
- **Rotation/Prozess-Tod:** kein zu rettender Zustand — `onResume` lädt neu;
  die Scroll-Position hält der `RecyclerView`-Instance-State.
- **Sehr langer Eintragstext:** die Karte wächst mit (`wrap_content` in einer
  scrollenden Liste), nichts wird abgeschnitten.
- **Eintrag ohne Stimmung/Impuls:** die betreffende Zeile entfällt (View
  `GONE`), kein leerer Platzhalter.
- **Zeitzonenwechsel/Reisen:** angezeigt wird die Gerätezeitzone zum
  Anzeigezeitpunkt — bewusst simpel; gespeichert ist der UTC-Moment.

### Barrierefreiheit

Stimmungs-Emoji mit `contentDescription` (bestehende
`mood_*_description`-Strings), Karten sind reine Anzeige (keine zu kleinen
Touch-Ziele), Einstiegs-Knopf ≥ 48 dp, alle Texte aus `strings.xml`,
Kontrast Weiß auf `animation_background` wie im Editor.

### Testplan

- **JVM (`app/src/test/`):** `EntryDateFormatter` — festes Muster,
  deterministisch bei injizierter Zeitzone; Mitternachts-/Zeitzonenfall.
- **Instrumented (`app/src/androidTest/`):** `JournalEntryDao.entriesNewestFirst`
  — sortiert absteigend nach Zeitstempel, Gleichstand nach `id` absteigend,
  leere DB liefert leere Liste.
- Manuell: Editor → „Meine Einträge" → Liste zeigt gespeicherte Einträge
  (neueste oben, Emoji/Impuls korrekt) → Back → Editor-Text unversehrt;
  Leerzustand bei frischer Installation.

### Doku-/Backlog-Auswirkungen

- `CLAUDE.md` + `docs/ONBOARDING.md`: Code-Map — `JournalOverviewActivity`
  ist kein Stub mehr.
- `docs/BACKLOG.md`: „Stub-Activities registrieren" — Übersicht erledigt,
  Insights/Settings bleiben offen.
- `docs/anforderungen/README.md`: Ausgangslage + Statuszeile.

## Definition of Done

- Vom Editor aus erreichbar: „Meine Einträge" öffnet die Liste; gespeicherte
  Einträge erscheinen neueste zuerst mit Zeitstempel, Text, ggf. Stimmung und
  Impuls; Back kehrt in den Editor zurück.
- Leerzustand bei leerer DB; kein Absturz bei Query-Fehler.
- `JournalOverviewActivity` im Manifest (`exported="false"`); keine
  hartkodierten UI-Strings (der Platzhalter-Text des alten Layouts ist weg);
  RecyclerView über den Version-Katalog; DB-Schema unverändert (Version 1,
  keine Migration).
- `./gradlew test` grün inkl. `EntryDateFormatterTest`; DAO-Sortier-Test
  kompiliert unter `assembleDebugAndroidTest` (Gerät für
  `connectedAndroidTest` vorausgesetzt).
- Kein Eintrag verlässt das Gerät (keine neuen Berechtigungen, keine
  Analytics).

## Umsetzungsschritte

1. Katalog + `app/build.gradle.kts`: `androidx.recyclerview` ergänzen.
2. DAO: `entriesNewestFirst()` + Instrumented-Test für die Sortierung.
3. `EntryDateFormatter` (pure, Zeitzone injizierbar) + JVM-Test.
4. Item-Layout `item_journal_entry.xml` + Adapter (`ListAdapter`/DiffUtil).
5. `activity_journal_overview.xml` neu (Titel, RecyclerView, Leerzustand)
   + Strings/Farben in Ressourcen.
6. `JournalOverviewActivity` verdrahten (Laden in `onResume`, Leerzustand,
   Fehlerpfad) + Manifest-Eintrag.
7. Einstiegs-Knopf im Editor (Layout + Click-Listener).
8. Doku nachziehen (CLAUDE.md, ONBOARDING.md, BACKLOG, Statuszeile).
