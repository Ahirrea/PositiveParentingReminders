# A-5 Einträge mit Themen versehen

[← Anforderungen](./README.md) · [Prozess](../PROZESS.md)
· Status siehe [Übersicht](./README.md#übersicht)

**User Story:** Als Elternteil in einer schwierigen Phase möchte ich meine
Einträge mit einem Thema versehen, um meine Reaktionen auf wiederkehrende
Situationen zu verfolgen.

**Verfeinert am:** 2026-08-07
**Bedient PRD:** „3 Ziele — Stimmung und Thema festhalten, damit aus
Einzeleinträgen ein Muster ablesbar wird" · „5 Kernschleife" Schritt 2 (und
Voraussetzung für Schritt 3) · User Story „Einträge mit Themen versehen"
**Eingeschränkt durch:**
[ADR-001](../entscheidungen/ADR-001-native-android-klassische-views.md)
(klassische Views) ·
[ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md)
(Room, Migrationen statt Datenverlust)

## Warum jetzt, vor A-7

Themen lassen sich **nicht nachträglich erzeugen**. Jeder Abend, der ohne
Themenfeld geschrieben wird, bleibt dauerhaft ohne Thema — und genau davon lebt
der Rückblick: das PRD-Beispiel „Streit ums Zubettgehen häuft sich montags"
(`../PRD.md`, Abschnitt 3) ist eine Aussage über Themenhäufigkeit. Baut man A-7
vor A-5, kann der Rückblick über die dann vorhandenen Wochen nur
Stimmungsverläufe zeigen. Es ist dasselbe Argument, mit dem die Stimmung
seinerzeit aus A-4 in A-1 vorgezogen wurde.

Das Erfolgskriterium „nach vier Wochen mindestens ein Muster benennen"
(`../PRD.md`, Abschnitt 6) läuft mit, seit A-1 steht — deshalb ist die
Reihenfolge A-5 → A-7 keine Geschmacksfrage.

## Andockpunkte im Code

- `data/JournalEntry.kt` — Entity mit `id`, `createdAtEpochMillis`, `text`,
  `mood`, `prompt`. **Es fehlt das Themenfeld**; die Ergänzung ist die erste
  echte Schema-Änderung des Projekts (Version 1 → 2).
- `data/AppDatabase.kt` — `@Database(version = 1)`, `Room.databaseBuilder`
  **ohne** `addMigrations`. Bekommt Version 2 und die erste `Migration`.
  `app/schemas/com.positiveparenting.data.AppDatabase/1.json` ist eingecheckt,
  `2.json` kommt dazu.
- `data/JournalEntryDao.kt` — `insert`, `findById`, `entriesNewestFirst`,
  `countSinceBlocking`. Braucht ein gezieltes `updateTheme` für das Nachtragen;
  die bestehenden Abfragen bleiben unverändert.
- `journal/JournalEditorActivity.kt` — die Stimmungsauswahl ist der fertige
  Bauplan: `moodButtonIds`, `selectedMood()`, `STATE_MOOD` im
  `onSaveInstanceState`, Übergabe im `JournalEntry(...)`-Konstruktor. Die
  Themenauswahl wird exakt danach modelliert.
- `res/layout/activity_journal_editor.xml` — `mood_toggle_group` sitzt zwischen
  Textfeld und Speichern-Knopf; die Themenzeile kommt darunter. Der ganze Screen
  ist bereits eine `ScrollView` mit `fillViewport`.
- `journal/PromptProvider.kt` + `strings.xml`-Array `daily_prompts` — das
  Vorbild für „feste lokale Liste in Ressourcen, reine Kotlin-Logik daneben,
  JVM-getestet". Die Themenliste wird genauso gebaut.
- `journal/JournalEntryAdapter.kt` + `res/layout/item_journal_entry.xml` — die
  Karte zeigt Zeitstempel, Stimmungs-Emoji, Impuls und Text, jeweils mit
  `View.GONE` für „nicht vorhanden". Die Themenzeile fügt sich in dieses Muster.
- `journal/JournalOverviewActivity.kt` — lädt in `onResume`, kennt Leerzustand
  und Fehlerpfad. Bekommt den Klickpfad zum Nachtragen.
- **Keine neue UI-Abhängigkeit nötig:** `com.google.android.material` (`1.13.0-rc01`)
  ist eingebunden, `ChipGroup`/`Chip` und `MaterialAlertDialogBuilder` kommen
  daraus. Neu im Version-Katalog ist allein `androidx.room:room-testing` für den
  Migrationstest — reine Testabhängigkeit.
- **Fehlt komplett:** Themenfeld, Themenkatalog, Migration, Auswahl-UI im Editor,
  Anzeige und Nachtrag-Pfad in der Übersicht.

## Spannung zu Nicht-Zielen — und Auflösung

- **„Ein Eintrag dauert unter drei Minuten"** (PRD Ziele). Jedes zusätzliche
  Feld arbeitet gegen dieses Ziel. **Auflösung:** das Thema ist **optional** und
  kostet **einen Tipp** — kein Tippen, kein Dialog, kein Pflichtfeld. Acht Chips
  passen in zwei Zeilen und sind in einer Sekunde überblickbar. Wer nichts
  auswählt, speichert wie bisher.
- **„Keine KI, solange der Journal-Kern nicht steht"** (PRD Nicht-Ziele). Kein
  Konflikt, aber die naheliegende Versuchung — Thema aus dem Text erraten —
  wird hier ausdrücklich ausgeschlossen: die Liste ist statisch in
  `strings.xml`, es gibt keine Vorschläge, keine Klassifikation, kein Modell.
  Automatische Themenerkennung wäre frühestens A-8.
- **„Kein Backend, keine Cloud, kein Nutzerkonto"** (PRD Nicht-Ziele). Kein
  Konflikt: eine Spalte mehr in der lokalen Room-Datenbank, keine neue
  Berechtigung, kein Netzwerk. Der Backup-Ausschluss aus A-1
  (`backup_rules.xml`/`data_extraction_rules.xml`) gilt für die ganze Datei und
  damit automatisch für das neue Feld.
- **A-2 hat die Übersicht bewusst als „nur lesen" festgelegt.** Das Nachtragen
  von Themen weicht diese Entscheidung auf — die Ideengeberin hat das am
  2026-08-07 bewusst so entschieden (siehe Entscheidung 3), weil die vor A-5
  geschriebenen Einträge sonst dauerhaft ohne Thema blieben. **Auflösung, eng
  gefasst:** nachtragbar ist **ausschließlich das Thema**. Text, Stimmung,
  Impuls und Zeitstempel bleiben unveränderlich — ein Tagebuch, das man
  umschreiben kann, ist keins mehr. Ein allgemeiner Bearbeiten-Modus bleibt
  damit weiterhin ungebaut und wäre eine eigene Anforderung.
  [A-2](./A-2-journal-uebersicht.md) selbst wird **nicht umgeschrieben** — die
  Datei ist das Protokoll ihres Standes; diese Einschränkung steht hier.
- **ADR-004: „Migrationen, nie `fallbackToDestructiveMigration`."** Kein
  Konflikt — im Gegenteil, ADR-004 hat den Fall wörtlich vorweggenommen („Das
  Datenmodell wird mit A-5 (Themen) sicher wachsen"). Deshalb ist hier **kein
  neuer ADR nötig**: die Technikwahl ist getroffen, A-5 löst nur ein, was dort
  schon als Konsequenz steht. Praktisch heißt das: `2.json` wird eingecheckt,
  die Migration bekommt einen eigenen Instrumented-Test, und die bestehende
  Datenbank auf dem Gerät der Ideengeberin überlebt das Update mit allen
  Einträgen.
- **ADR-001: klassische Views.** `ChipGroup` und `MaterialAlertDialogBuilder`
  sind gewöhnliche Android-Views im XML- und `findViewById`-Stil. Kein Compose.
- **`CLAUDE.md`-Konventionen.** Keine hartkodierten Strings (Labels und
  Dialogtexte nach `strings.xml`), Ids in `snake_case`, die neue
  Testabhängigkeit über `gradle/libs.versions.toml`. Erwartbar ist die dort
  dokumentierte Macke: der **erste** Build nach einer neuen Schema-Version kann
  in `kspDebugKotlin` mit `JsonDecodingException … 'EOF'` scheitern, obwohl
  `2.json` korrekt ist — einfach erneut bauen, nicht die (gültige) JSON
  debuggen.

## Entscheidungen (mit Begründung)

Alle vier am 2026-08-07 von der Ideengeberin entschieden.

1. **Feste, kuratierte Themenliste** statt freier Schlagworte.
   *Begründung:* auswählen statt tippen hält den Eintrag unter drei Minuten, und
   nur stabile Schreibweisen sind zählbar. Freie Schlagworte hätten „Zubettgehen",
   „Bett" und „bett" nebeneinander erzeugt und damit genau das Muster zerlegt,
   das A-7 finden soll — bei einer einzigen, abends müden Nutzerin ist das kein
   theoretisches Risiko.
   *Verworfen:* **freie Schlagworte** (Zerfaserung, Tippaufwand, eigene Tabelle
   nötig) und **Hybrid „Liste + eigenes Thema"** (deckt beides ab, verdoppelt
   aber den Umfang um Verwaltung selbst angelegter Themen; kann später
   nachgezogen werden, wenn sich die Liste im Alltag als zu eng erweist).

2. **Genau ein Thema pro Eintrag, optional.**
   *Begründung:* dieselbe Interaktion wie bei der Stimmung — ein Tipp oder gar
   keiner. Das Datenmodell bleibt eine nullable Spalte, die Migration ein
   `ALTER TABLE`. Ein Abend hat meist eine dominante Situation; wenn sich das im
   Gebrauch als zu eng erweist, ist der Umstieg auf mehrere Themen eine
   mechanische Migration (Verknüpfungstabelle anlegen, `INSERT … SELECT`, Spalte
   fallen lassen) und kein Umbau.
   *Verworfen:* **Mehrfachauswahl von Anfang an** — ehrlicher gegenüber
   chaotischen Abenden, aber sie erzwingt sofort eine Verknüpfungstabelle und
   zwingt A-7, Mehrfachzählung zu erklären.

3. **Themen sind in der Übersicht nachtragbar** — abweichend von der
   Empfehlung der Umsetzung, die den Nur-Lese-Charakter aus A-2 unangetastet
   lassen wollte.
   *Begründung der Ideengeberin:* die seit A-1 geschriebenen Einträge sollen
   nicht als dauerhafte Datenlücke im ersten Rückblick stehen.
   *Auflösung der Spannung:* streng auf das Thema begrenzt (siehe oben);
   Text und Stimmung bleiben unveränderlich, es entsteht **kein** allgemeiner
   Bearbeiten-Modus.

4. **Acht Themen als Startliste**, kompakt.
   *Begründung:* die Liste muss in einer Sekunde überblickbar sein, und die
   Zahlen müssen dicht genug werden, um überhaupt ein Muster zu zeigen. Bei
   grob 30 Einträgen in vier Wochen bleiben bei acht Themen im Schnitt ~4 pro
   Thema; bei zwölf wären es ~2,5 — zu dünn für eine Aussage. Das letzte Thema
   ist bewusst positiv besetzt, sonst passen Themen nur zu Konflikt-Einträgen,
   während die Hälfte der Tagesimpulse nach gelungenen Momenten fragt.
   *Verworfen:* **fünf Themen** (dichtere Zahlen, aber zu viele Abende ohne
   passendes Thema) und **zwölf Themen** (bessere Trefferquote, aber längeres
   Scannen pro Eintrag und zu dünne Zahlen im Rückblick).

Aus 1 und 2 folgt eine technische Festlegung, die kein eigener ADR ist, aber
langfristig bindet und deshalb hier begründet steht: **gespeichert wird ein
stabiler englischer Schlüssel, nicht das deutsche Label.** Labels dürfen sich
jederzeit ändern (`„Wut & Trotz"` → `„Trotz"`), ohne die Historie zu entwerten;
in der Datenbank steht weiterhin `anger`. Das passt zur Konvention „Code
englisch, UI-Strings deutsch" und macht den Katalog rein und JVM-testbar.

## Umfang / Nicht-Umfang

- **Rein:** Themenfeld in `JournalEntry`; Schema-Version 2 mit echter Migration
  und eingecheckter `2.json`; Themenkatalog als reine Kotlin-Logik plus
  Label-Array in `strings.xml`; einzeilige Chip-Auswahl im Editor; Anzeige des
  Themas auf der Karte der Übersicht; Nachtragen/Ändern/Entfernen des Themas
  über die Übersicht; Tests (JVM, DAO, Migration).
- **Raus:**
  - **Auswertung nach Thema** — Häufigkeiten, Filter, „montags häuft sich" ist
    A-7. A-5 erzeugt die Daten, deutet sie nicht.
  - **Eigene Themen anlegen** (Hybrid verworfen, siehe Entscheidung 1).
  - **Mehrere Themen pro Eintrag** (Entscheidung 2).
  - **Bearbeiten von Text, Stimmung, Impuls oder Zeitstempel** (Entscheidung 3).
  - **Löschen von Einträgen** — gehört zu A-9.
  - **Themen in der Notification** — die Notification bleibt bewusst generisch
    (A-3: nichts Inhaltliches auf den Sperrbildschirm).
  - **Automatische Themenerkennung** aus dem Text (PRD-Nicht-Ziel „keine KI").
  - **Themenliste in den Einstellungen pflegen** — falls je gewünscht, A-6.

## Spezifikation

### Themenkatalog

`journal/ThemeCatalog.kt`, reines Kotlin ohne Android-Bezug, nach dem Vorbild
von `PromptProvider`:

- `val KEYS: List<String>` — die stabilen Schlüssel in fester Reihenfolge:
  `bedtime`, `morning`, `meals`, `siblings`, `anger`, `screentime`, `chores`,
  `closeness`.
- `fun isKnown(key: String?): Boolean` — für Werte aus der Datenbank.
- `fun indexOf(key: String?): Int?` — Position für die parallele Label-Liste.

Die Labels liegen als **index-paralleles** `string-array` `theme_labels` in
`strings.xml`, genau wie `daily_prompts` zum `PromptProvider`:

| # | Schlüssel | Label |
|---|---|---|
| 0 | `bedtime` | Zubettgehen |
| 1 | `morning` | Morgens & Loskommen |
| 2 | `meals` | Essen |
| 3 | `siblings` | Geschwister |
| 4 | `anger` | Wut & Trotz |
| 5 | `screentime` | Medienzeit |
| 6 | `chores` | Aufräumen & Pflichten |
| 7 | `closeness` | Nähe & Verbundenheit |

Die Parallelität von Schlüsseln und Labels ist die einzige Fragilität dieses
Aufbaus; sie wird durch einen Instrumented-Test abgesichert, der beide Längen
vergleicht und leere Labels ausschließt.

### Datenmodell / Persistenz

- `JournalEntry` bekommt `val theme: String? = null` — `null` heißt „kein Thema
  angegeben", genau wie bei `mood`.
- `AppDatabase`: `version = 2`, `.addMigrations(MIGRATION_1_2)`.
  `MIGRATION_1_2` führt aus:
  `ALTER TABLE journal_entries ADD COLUMN theme TEXT` — bestehende Zeilen
  bekommen `NULL`. **Kein** `fallbackToDestructiveMigration` (ADR-004).
- `app/schemas/com.positiveparenting.data.AppDatabase/2.json` wird eingecheckt.
- Kein Index: bei einigen hundert Einträgen ist ein Full Scan ohnehin schneller
  als ein Indexzugriff; A-7 entscheidet das neu, wenn es Aggregate braucht.
- DAO-Ergänzung, gezielt statt eines allgemeinen `@Update`, damit kein anderes
  Feld überschrieben werden kann:
  `@Query("UPDATE journal_entries SET theme = :theme WHERE id = :id")`
  `suspend fun updateTheme(id: Long, theme: String?)`.

### UX — Editor (`JournalEditorActivity`)

Zwischen Stimmungsauswahl und Speichern-Knopf, im selben Rhythmus wie die
Stimmung:

- Label `Worum ging es heute? (optional)`.
- `ChipGroup` (`app:singleSelection="true"`, `app:selectionRequired="false"`),
  Chips im Filter-Stil, zur Laufzeit aus `ThemeCatalog.KEYS` + `theme_labels`
  erzeugt — das Array bleibt die einzige Quelle, kein doppeltes Pflegen im
  Layout.
- Zweiter Tipp auf denselben Chip hebt die Auswahl wieder auf („doch kein
  Thema"), garantiert durch `selectionRequired="false"`.
- Kontrast: heller Text und heller Rand auf dem dunklen `animation_background`,
  wie bei den Stimmungsknöpfen (`@color/mood_button_background`, weiße
  Schrift/Kontur) — nicht das Theme-Lila, das im Editor schon einmal
  nachgebessert werden musste.
- Zustandserhalt: der gewählte Schlüssel wandert als `STATE_THEME` in
  `onSaveInstanceState`, analog zu `STATE_MOOD`, und übersteht Drehung und
  Prozesstod.
- Speichern übergibt `theme = selectedThemeKey()` an `JournalEntry`. Schlägt das
  Speichern fehl, bleibt die Auswahl stehen — wie Text und Stimmung heute schon.

### UX — Übersicht (`JournalOverviewActivity`, `JournalEntryAdapter`)

- **Anzeige:** auf der Karte erscheint das Thema als eigene Zeile über dem
  Impuls; ohne Thema ist die Zeile `View.GONE` — dasselbe Muster wie Stimmung
  und Impuls heute.
- **Nachtragen:** Tipp auf die Karte öffnet einen
  `MaterialAlertDialogBuilder`-Dialog mit Einfachauswahl über die acht Labels
  plus einem Eintrag `Kein Thema`. Das aktuelle Thema ist vorausgewählt.
  „Speichern" ruft `updateTheme` und lädt die Liste neu; „Abbrechen" ändert
  nichts. Ein Dialog auf einmal — ein zweiter Tipp während des Speicherns wird
  ignoriert.
- Die Karte bekommt `clickable`/`focusable` und einen sprechenden
  Dialog-Titel (`Thema wählen`), damit der Pfad ohne Erklärung auffindbar ist.
- Alles andere auf der Karte bleibt unveränderlich.

### Randfälle & Fehlerbehandlung

| Fall | Verhalten |
|---|---|
| Bestehende Datenbank (Version 1) | Migration läuft, alle Einträge bleiben erhalten, `theme` ist `NULL`; sie lassen sich über die Übersicht nachtragen. |
| Unbekannter Schlüssel in der Datenbank (Thema später aus der Liste entfernt) | Die Karte zeigt keine Themenzeile; der Wert bleibt **unangetastet** in der Datenbank — es wird nichts stillschweigend gelöscht. Der Dialog startet in diesem Fall ohne Vorauswahl. |
| `updateTheme` schlägt fehl | Toast mit Fehlermeldung, Liste bleibt unverändert; kein Absturz, kein halber Zustand. |
| Doppeltipp auf Speichern im Editor | Unverändert durch A-1 abgedeckt (Knopf wird beim ersten Tipp deaktiviert). |
| Drehung/Prozesstod im Editor mit gewähltem Thema | Auswahl bleibt erhalten (`STATE_THEME`). |
| Sehr großer System-Schriftgrad | Die `ChipGroup` bricht in weitere Zeilen um; der Editor ist eine `ScrollView`, es geht nichts verloren. |
| Eintrag ohne Thema | Vollkommen normal — kein Hinweis, kein Nachfassen, keine Pflicht. |

### Barrierefreiheit

- Chips tragen echten Text, keine Emoji — anders als die Stimmung brauchen sie
  keine eigene `contentDescription`; TalkBack liest Label und Auswahlzustand.
- Mindest-Touchfläche 48 dp (`app:chipMinHeight`), da Material-Chips von Haus
  aus nur 32 dp hoch sind.
- Kontrast von Chip-Text und -Rand gegen den dunklen Hintergrund wird geprüft,
  im aktiven wie im inaktiven Zustand.
- Die anklickbare Karte in der Übersicht ist fokussierbar; der Auswahldialog ist
  eine Standard-Material-Komponente und damit tastatur- und TalkBack-tauglich.

### Testplan

- **JVM (`app/src/test/`), neu `ThemeCatalogTest`:** Schlüssel sind eindeutig
  und nicht leer; die Liste entspricht exakt einer im Test hartkodierten
  Erwartung — benennt jemand einen Schlüssel um, bricht der Test, denn genau
  dieser Wert steht in gespeicherten Einträgen; `isKnown` liefert `false` für
  Unbekanntes und `null`; `indexOf` passt zu `KEYS`.
- **Instrumented, neu `ThemeLabelsTest`:** `theme_labels` hat dieselbe Länge wie
  `ThemeCatalog.KEYS`, kein Label ist leer — sichert die parallele Reihenfolge.
- **Instrumented, `JournalEntryDaoTest` erweitert:** Einfügen mit Thema liest
  denselben Schlüssel zurück; `updateTheme` setzt ein Thema, ändert es und
  entfernt es wieder (`null`), ohne Text, Stimmung, Impuls oder Zeitstempel
  anzufassen.
- **Instrumented, neu `AppDatabaseMigrationTest`:** mit `MigrationTestHelper`
  eine Version-1-Datenbank anlegen, einen Eintrag schreiben, auf Version 2
  migrieren — der Eintrag ist unverändert vorhanden und `theme` ist `NULL`.
  Dafür kommt `androidx.room:room-testing` als `androidTestImplementation` in
  den Version-Katalog, und `app/build.gradle.kts` nimmt das Schema-Verzeichnis
  in die `androidTest`-Assets auf.
- **In Web-Sitzungen** gibt es kein Gerät: dort deckt `./gradlew test` den
  JVM-Teil ab, und `assembleDebugAndroidTest` weist immerhin nach, dass die
  Instrumented-Tests übersetzen.

### Doku- / Backlog-Auswirkungen

- `CLAUDE.md`: Code-Map um `ThemeCatalog` und die erweiterten Editor-/
  Übersichts-Beschreibungen ergänzen; im Persistenz-Absatz die Schema-Version
  auf 2 ziehen und die erste Migration erwähnen; Testliste erweitern.
- `docs/ONBOARDING.md`: dieselben Stellen nachziehen.
- `docs/anforderungen/README.md`: „Ausgangslage" um die Themen ergänzen (bei der
  Umsetzung), Statuszeile pflegen.
- `docs/PRD.md`: nicht betroffen — „Stimmung und Thema festhalten" steht dort
  bereits als Ziel.
- `docs/BACKLOG.md`: nicht betroffen; Lottie und die Registrierung der
  Stub-Screens bleiben offen.
- `docs/entscheidungen/`: **kein neuer ADR** — ADR-004 deckt Room, Migrationen
  und ausdrücklich den Fall A-5 ab.

## Definition of Done

- Der Editor zeigt unter der Stimmung acht Themen-Chips; die Auswahl ist
  optional, kostet einen Tipp und lässt sich durch erneutes Antippen aufheben.
- Ein gespeicherter Eintrag trägt den gewählten **Schlüssel** (nicht das Label)
  in der Spalte `theme`; ohne Auswahl steht dort `NULL`.
- Die Übersicht zeigt das Thema auf der Karte und blendet die Zeile ohne Thema
  aus.
- Ein Tipp auf eine Karte erlaubt, das Thema zu setzen, zu ändern und zu
  entfernen; Text, Stimmung, Impuls und Zeitstempel bleiben unveränderlich.
- Eine bestehende Datenbank aus Version 1 wird migriert, **ohne einen Eintrag zu
  verlieren**; `2.json` ist eingecheckt; `fallbackToDestructiveMigration` kommt
  nirgends vor.
- Keine hartkodierten UI-Strings (Labels, Dialogtexte, Fehlermeldung in
  `strings.xml`), Ids in `snake_case`, `room-testing` über
  `gradle/libs.versions.toml`.
- Keine neue Berechtigung, kein Netzwerkzugriff, keine KI-Abhängigkeit; die
  Notification bleibt unverändert generisch.
- `./gradlew test` grün inklusive `ThemeCatalogTest`;
  `assembleDebugAndroidTest` übersetzt die erweiterten und neuen
  Instrumented-Tests (Migrationstest inbegriffen).
- Kein neuer Screen — am `AndroidManifest.xml` ändert sich nichts.

## Umsetzungsschritte

1. `journal/ThemeCatalog.kt` (rein: Schlüssel, `isKnown`, `indexOf`) +
   `ThemeCatalogTest` als JVM-Test.
2. `strings.xml`: `string-array` `theme_labels` (acht Labels, Reihenfolge wie
   `KEYS`), Label über der Chip-Reihe, Dialogtitel, „Kein Thema", Fehlermeldung;
   Chip-Farbzustände in `colors.xml`, falls die vorhandenen nicht reichen.
3. `JournalEntry` um `theme` erweitern; `AppDatabase` auf Version 2 mit
   `MIGRATION_1_2` (`ALTER TABLE … ADD COLUMN theme TEXT`) und `addMigrations`;
   `2.json` einchecken. *(Beim ersten Build den bekannten KSP-`EOF`-Fehler
   erwarten und einfach erneut bauen.)*
4. Version-Katalog + `app/build.gradle.kts`: `androidx-room-testing` als
   `androidTestImplementation`, Schema-Verzeichnis in die `androidTest`-Assets;
   `AppDatabaseMigrationTest` schreiben.
5. DAO: `updateTheme` ergänzen, `JournalEntryDaoTest` um Thema und Update
   erweitern.
6. Editor: `ChipGroup` ins Layout, Chips zur Laufzeit erzeugen, `STATE_THEME`
   im Instanzzustand, Thema beim Speichern übergeben.
7. Übersicht: Themenzeile in `item_journal_entry.xml` und im Adapter; Karte
   klickbar machen, Auswahldialog, `updateTheme`, Neuladen, Fehlerpfad.
8. `ThemeLabelsTest` (Längen- und Leer-Prüfung) ergänzen.
9. Doku nachziehen (`CLAUDE.md`, `docs/ONBOARDING.md`, Ausgangslage und
   Statuszeile in der Übersicht).
