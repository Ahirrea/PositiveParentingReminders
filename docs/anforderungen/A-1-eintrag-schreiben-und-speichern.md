# A-1 Eintrag schreiben und speichern

[← Anforderungen](./README.md) · [Prozess](../PROZESS.md)
· Status siehe [Übersicht](./README.md#übersicht)

**User Story:** Als Elternteil mit wenig Zeit möchte ich abends in unter drei
Minuten zu einem Tagesimpuls ein paar Sätze und meine Stimmung festhalten, um
über Wochen meine Reaktionsmuster zu erkennen.

**Verfeinert am:** 2026-07-31
**Bedient PRD:** „5 Kernschleife" Schritte 1–2 · „3 Ziele — Eintrag unter drei
Minuten, ein geführter Impuls, Stimmung festhalten"
**Eingeschränkt durch:** [ADR-001](../entscheidungen/ADR-001-native-android-klassische-views.md)
(klassische Views) ·
[ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md) (Room)

## Andockpunkte im Code

- `journal/JournalEditorActivity.kt` — **Stub**: `onCreate` ohne
  `setContentView` (auskommentiert), nicht im Manifest. Wird der Kern dieser
  Anforderung.
- `res/layout/activity_journal_editor.xml` — Platzhalter (ein zentrierter
  `TextView` mit hartkodiertem Text). Wird ersetzt.
- Der Activity-Bauplan aus dem Onboarding ist wiederverwendbar: dünne
  `AppCompatActivity`, `findViewById`, Material-Komponenten
  (`MaterialButton`, `TextInputLayout` wie in `AccountCreationActivity`).
- `onboarding/OnboardingActivity.kt` — Launcher; bekommt die Weiche
  „Onboarding erledigt → direkt in den Editor".
- **Fehlt komplett:** Persistenz (kein Datenmodell, keine Datenbank, kein
  Repository), Stimmungs-UI, Impuls-Logik, Tests (weder `app/src/test/` noch
  `app/src/androidTest/` existieren).

## Spannung zu Nicht-Zielen — und Auflösung

- **„Kein Backend, keine Cloud, kein Nutzerkonto":** kein Konflikt — alles
  bleibt lokal (Room auf dem Gerät, [ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md)).
  Kein Eintrag verlässt das Gerät.
- **„Keine KI, solange der Journal-Kern nicht steht":** der Tagesimpuls kommt
  aus einer **festen lokalen Liste** (`strings.xml`-Array), nicht aus einem
  Modell. Kein Konflikt.
- **„Ein Eintrag dauert unter drei Minuten":** die Maßlatte für jede
  UI-Entscheidung hier — ein Screen, keine Pflichtfelder außer dem Text,
  Stimmung ist ein Tipp, Speichern ein zweiter.
- **Android-Backup:** `AndroidManifest.xml` hat `allowBackup="true"` mit den
  Default-Regeln — die Datenbank würde in Googles Cloud-Backup landen. Das
  kollidiert mit „kein Eintrag verlässt jemals das Gerät" (PRD
  Erfolgskriterien). **Auflösung:** die Datenbank wird in
  `backup_rules.xml`/`data_extraction_rules.xml` vom Backup ausgeschlossen;
  Gerätewechsel löst später A-9 (Export) sauber.

## Entscheidungen (mit Begründung)

Alle vier am 2026-07-31 von der Ideengeberin entschieden:

1. **Persistenz: Room** → eigener
   [ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md).
   Verworfen: DataStore (Aggregation für A-7 unpraktikabel), rohes SQLite
   (mehr Code ohne Vorteil).
2. **Stimmung gehört in A-1** (5-stufige Auswahl im Editor). Ohne Stimmung ab
   dem ersten Eintrag fehlen A-7 später die Daten — nachträglich lässt sich
   Stimmung nicht erheben. **A-4 geht damit in A-1 auf** und wird in der
   Übersicht verworfen. Verworfen: eigenständiges A-4 später.
3. **Tagesimpuls schon jetzt, lokal:** feste Liste in `strings.xml`, Auswahl
   rotierend nach Datum. „Nicht vor einem leeren Feld sitzen" gehört zum
   Schreiben selbst; die Notification bleibt A-3. Verworfen: leeres Textfeld
   bis A-3.
4. **Einstieg: App-Start → Editor.** Onboarding setzt beim Abschluss ein
   lokales Flag (SharedPreferences); der Launcher (`OnboardingActivity`)
   leitet danach bei jedem Start direkt in den Editor. Verworfen: Onboarding
   bei jedem Start erneut durchlaufen.

## Umfang / Nicht-Umfang

- **Rein:** Editor-Screen (Impuls, Textfeld, Stimmungsauswahl, Speichern),
  Room-Setup (Entity, DAO, Datenbank), Impuls-Rotation nach Datum,
  Onboarding-Flag + Weiterleitung, Manifest-Registrierung, Backup-Ausschluss
  der DB, erste Test-Suite.
- **Raus (bewusst):** Einträge lesen/bearbeiten/löschen (A-2), Themen (A-5),
  Notification (A-3), Rückblick (A-7), Export (A-9), persönliche Anrede mit
  Namen (A-10), Entwurfs-Autosave über App-Neustarts hinweg.

## Spezifikation

### UX-Ablauf & Zustände

Ein Screen, `JournalEditorActivity`:

1. **Kopf:** heutiges Datum (lokal formatiert) und der Tagesimpuls, z. B.
   „Wann warst du heute stolz auf dich?".
2. **Textfeld:** mehrzeilig, Fokus beim Öffnen, Hint aus `strings.xml`.
3. **Stimmungsreihe:** fünf Symbole (sehr schwer … sehr gut) als
   Einfachauswahl, **optional** — ein Eintrag ohne Stimmung ist speicherbar,
   die Auswahl ist ein einziger Tipp und darf die Drei-Minuten-Schleife nicht
   verlängern.
4. **Speichern-Knopf:** deaktiviert, solange der Text leer/nur Whitespace ist.
   Beim Tippen auf Speichern: Eintrag einfügen (IO-Dispatcher via
   `lifecycleScope`), kurze Bestätigung („Gespeichert"), dann `finish()`.

Zustände: *leer* (Speichern aus) → *schreibend* (Speichern an) →
*gespeichert* (Bestätigung, Activity endet). Bei Speicherfehler: Fehlermeldung
(Toast/Snackbar), Text und Auswahl bleiben erhalten.

### Interaktion mit Bestehendem

- `OnboardingActivity.onCreate`: steht das SharedPreferences-Flag
  `onboarding_complete`, sofort `startActivity(JournalEditorActivity)` +
  `finish()`.
- Das Flag setzt heute der Abschluss-Knopf der `AccountCreationActivity`;
  A-10 (Umbau zum lokalen Profil) übernimmt diesen Punkt mit.
- Neue Activity **im Manifest registrieren** (`exported="false"`).

### Datenmodell / Persistenz (Room, ADR-004)

Neues Paket `com.positiveparenting.data`:

- **Entity `JournalEntry`:** `id: Long` (PK, autoGenerate) ·
  `createdAtEpochMillis: Long` · `text: String` · `mood: Int?` (1–5, null =
  keine Angabe) · `prompt: String?` (der angezeigte Impuls, damit der
  Rückblick später weiß, worauf geantwortet wurde).
- **DAO `JournalEntryDao`:** `insert(entry)`; lesende Abfragen kommen mit A-2.
- **`AppDatabase`:** `RoomDatabase`-Singleton, Version 1, **kein**
  `fallbackToDestructiveMigration`.
- Mehrere Einträge pro Tag sind erlaubt (kein künstlicher Unique-Constraint).

### Impuls-Rotation

`<string-array name="daily_prompts">` mit ~10 deutschen Impulsen (an den
PRD-Beispielen orientiert). Auswahl: `index = epochDay mod size` — rein,
deterministisch, ohne `Date.now()`-Streuung testbar. Kapselung in einer
kleinen Funktion (`PromptProvider`), damit sie JVM-testbar ist.

### Externe Abhängigkeiten & Fallback

Room (Runtime, KTX, Compiler) + KSP-Plugin über
`gradle/libs.versions.toml` (KSP-Version passend zu Kotlin `2.2.0` wählen).
Kein Netzwerk, kein Fallback nötig.

### Randfälle & Fehlerbehandlung

- Leerer/Whitespace-Text → Speichern deaktiviert, kein leerer Eintrag möglich.
- Rotation/Prozess-Tod im Schreiben: `EditText` mit `android:id` stellt den
  Text über den Instance State wieder her; die Stimmungsauswahl wird via
  `onSaveInstanceState` gesichert.
- Doppeltipp auf Speichern → Knopf beim ersten Tipp deaktivieren (kein
  Doppel-Insert).
- Insert schlägt fehl (voller Speicher o. Ä.) → Fehlermeldung, Eingaben
  bleiben erhalten, Activity bleibt offen.
- Datumsgrenze um Mitternacht: der Impuls wird beim Öffnen bestimmt und
  ändert sich während einer offenen Sitzung nicht.

### Barrierefreiheit

Stimmungs-Symbole mit `contentDescription` (je Stufe benannt), Touch-Ziele
≥ 48 dp, Auswahlzustand nicht nur über Farbe (Rahmen/Zustand), Texte aus
`strings.xml`.

### Testplan (legt die Test-Suite an)

- **JVM (`app/src/test/`):** `PromptProvider` — Rotation deterministisch,
  Wrap-around, stabiler Index pro Tag.
- **Instrumented (`app/src/androidTest/`):** `JournalEntryDao` gegen
  In-Memory-Room — Insert persistiert alle Felder, `mood`/`prompt` nullable.
- Manuell: kompletter Durchstich Onboarding → Flag → Editor → Speichern →
  App-Neustart landet direkt im Editor.

### Doku-/Backlog-Auswirkungen

- `CLAUDE.md`: Code-Map um `data/` ergänzen; „keine echten Tests"-Absätze
  aktualisieren. `docs/ONBOARDING.md` ebenso.
- `docs/BACKLOG.md`: Punkt „Test-Suite anlegen" wird hiermit erledigt;
  „Stub-Activities registrieren" für den Editor abgehakt.
- PRD „Rahmenbedingungen → Datenhaltung": auf ADR-004 verweisen (erledigt).

## Definition of Done

- Kernschleife funktioniert auf dem Gerät: App öffnen → Impuls sehen → Text +
  optional Stimmung → Speichern → Eintrag liegt in der Room-DB.
- Nach abgeschlossenem Onboarding startet die App direkt im Editor; das
  Onboarding erscheint kein zweites Mal.
- `JournalEditorActivity` im Manifest (`exported="false"`); keine
  hartkodierten UI-Strings (auch der Platzhalter-Text im alten Layout ist weg);
  neue Abhängigkeiten ausschließlich über den Version-Katalog.
- Datenbank vom Android-Backup ausgeschlossen.
- `./gradlew test` grün mit den neuen JVM-Tests; DAO-Test läuft unter
  `connectedAndroidTest`.
- Kein Eintrag verlässt das Gerät (keine neuen Netzwerk-Berechtigungen,
  keine Analytics).

## Umsetzungsschritte

1. Build: KSP-Plugin + Room in `gradle/libs.versions.toml` und
   `app/build.gradle.kts`.
2. Datenschicht: `JournalEntry`, `JournalEntryDao`, `AppDatabase` unter
   `com.positiveparenting.data` + DAO-Instrumented-Test.
3. `PromptProvider` + `daily_prompts`-Array + JVM-Test.
4. Layout `activity_journal_editor.xml` neu (Datum, Impuls, Textfeld,
   Stimmungsreihe, Speichern) + Strings/Farben in Ressourcen.
5. `JournalEditorActivity` verdrahten (Zustände, Speichern über
   `lifecycleScope`, Fehlerpfad, Instance State) + Manifest-Eintrag.
6. Onboarding-Flag setzen (Abschluss-Knopf) und Launcher-Weiche in
   `OnboardingActivity`.
7. Backup-Regeln: DB in `backup_rules.xml`/`data_extraction_rules.xml`
   ausschließen.
8. Doku nachziehen (CLAUDE.md, ONBOARDING.md, BACKLOG-Haken, Statuszeile).
