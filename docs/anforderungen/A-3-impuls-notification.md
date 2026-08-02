# A-3 Ein Impuls pro Tag als Notification

[← Anforderungen](./README.md) · [Prozess](../PROZESS.md)
· Status siehe [Übersicht](./README.md#übersicht)

**User Story:** Als berufstätiges Elternteil möchte ich abends **einen** Impuls
als Notification erhalten, um ans Schreiben erinnert zu werden, ohne selbst
daran denken zu müssen — daran hängt die ganze Gewohnheit.

**Verfeinert am:** 2026-08-02
**Bedient PRD:** „5 Kernschleife" Schritt 1 („Abends kommt **ein** Impuls") ·
„3 Ziele — ein geführter Impuls pro Tag" · User Story „einen Impuls pro Tag
erhalten"
**Eingeschränkt durch:** [ADR-001](../entscheidungen/ADR-001-native-android-klassische-views.md)
(klassische Views) ·
[ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md) (Room, nur
für die Skip-Regel gelesen)

> **Zum grünen Licht:** Die Umsetzung wurde von der Ideengeberin direkt
> beauftragt („Setze A-3 um", 2026-08-02); Verfeinerung und Umsetzung sind in
> einem Zug erfolgt. Die Weichen unten sind darum mit konservativen Defaults
> gestellt und hier begründet — jede davon ist über A-6 (Einstellungen)
> revidierbar, keine bindet das Projekt langfristig.

## Andockpunkte im Code

- `journal/PromptProvider.kt` — **wiederverwendbar**: liefert den Impuls des
  Tages (`epochDay mod size` über `daily_prompts`). Die Notification zeigt
  exakt denselben Impuls, den der Editor beim Öffnen anzeigen wird.
- `journal/JournalEditorActivity.kt` — das Tap-Ziel der Notification; außerdem
  der erste Ort, den jede Nutzerin nach dem Onboarding sicher erreicht →
  dort werden Alarm-Planung und die einmalige Berechtigungsfrage verankert.
- `profile/LocalProfileStore.kt` — `isOnboardingComplete`: vor Abschluss des
  Onboardings darf nichts erinnern (die Notification würde am Onboarding
  vorbei in den Editor führen).
- `data/JournalEntryDao.kt` — bekommt eine Zählabfrage „Einträge seit
  Zeitpunkt" für die Skip-Regel („heute schon geschrieben → keine
  Erinnerung"). Kein Schema-Eingriff, DB bleibt Version 1.
- `res/drawable/ic_heart.xml` — vorhandenes weißes Vektor-Herz, taugt als
  monochromes Small Icon.
- **Fehlt komplett:** jeglicher Notification-Code (Channel, Receiver,
  Scheduling, Berechtigung) — das Paket `reminder/` entsteht neu.

## Spannung zu Nicht-Zielen — und Auflösung

- **„Kein Backend, keine Cloud":** „Notification" heißt hier ausschließlich
  **lokale** Notification via `AlarmManager` + `NotificationManager`. Kein
  FCM, kein Push-Dienst, keine neue Netzwerk-Berechtigung. Kein Konflikt —
  aber wichtig, es explizit zu benennen, weil „Notification" oft reflexhaft
  Push-Infrastruktur bedeutet.
- **„Keine KI":** der Impuls kommt unverändert aus der festen lokalen Liste
  (`PromptProvider`). Kein Konflikt.
- **PRD-Risiko „Journaling wird als Pflicht empfunden":** eine Erinnerung ist
  genau die Stelle, an der eine App zum Nörgler wird. Auflösung durch drei
  bewusste Begrenzungen: (1) **höchstens eine** Notification pro Tag, kein
  Nachfassen; (2) **Skip-Regel** — wer heute schon geschrieben hat, wird nicht
  erinnert; (3) normale Priorität (`IMPORTANCE_DEFAULT`), kein Vollbild, kein
  aufdringlicher Ton. Und eine Ablehnung der Berechtigung wird respektiert —
  gefragt wird genau einmal.
- **Datenschutz („kein Eintrag verlässt das Gerät"):** die Notification
  enthält nur den generischen Tagesimpuls, nie Eintragsinhalte oder Namen —
  sie ist auf dem Sperrbildschirm sichtbar.

## Entscheidungen (mit Begründung)

Alle am 2026-08-02 im Zuge des direkten Umsetzungsauftrags getroffen:

1. **Mechanik: `AlarmManager.setInexactRepeating` (RTC_WAKEUP, täglich).**
   Eine sanfte Abenderinnerung braucht keine Minutengenauigkeit; inexakt heißt
   keine `SCHEDULE_EXACT_ALARM`/`USE_EXACT_ALARM`-Berechtigung (Play-seitig
   restriktiert) und batteriefreundliches Batching durch das System.
   Verworfen: **WorkManager** (neue Abhängigkeit, periodische Arbeit driftet
   über Tage, für einen simplen Tages-Ping überdimensioniert), **exakte
   Alarme** (Berechtigungsaufwand ohne Produktnutzen).
2. **Feste Uhrzeit 20:00 lokal.** Das PRD sagt „abends"; 20:00 ist ein
   plausibler Default nach dem Zubettbringen. Die Konfigurierbarkeit ist
   ausdrücklich **A-6** (steht schon so in der Übersicht); die Konstante lebt
   an einer Stelle (`ReminderScheduler.REMINDER_TIME`). Verworfen: Uhrzeit
   jetzt schon abfragbar machen (zieht ein Stück A-6 vor und verlängert das
   Onboarding).
3. **Skip-Regel: heute schon ein Eintrag → keine Notification.** Die
   Erinnerung dient der Gewohnheit, nicht der Vollständigkeit; wer schon
   geschrieben hat, braucht keinen Ping (PRD-Risiko „Pflicht"). Kostet nur
   eine Zählabfrage. Verworfen: immer erinnern (nervt genau die, die die App
   richtig benutzen).
4. **Berechtigung `POST_NOTIFICATIONS` einmalig beim ersten Editor-Start
   erfragen.** `minSdk 33` ⇒ die Berechtigung ist immer eine
   Runtime-Abfrage. Der Editor nach abgeschlossenem Onboarding ist der erste
   Moment, in dem der Nutzen („dein täglicher Impuls") erlebbar ist; im
   Onboarding selbst wäre es ein weiterer Schritt vor dem ersten Erlebnis.
   Eine Ablehnung wird gespeichert und **nicht** wiederholt erfragt; die App
   funktioniert ohne Notification vollständig, wieder aktivieren kann man sie
   über die System-Einstellungen (komfortabel dann via A-6). Verworfen:
   eigener Onboarding-Schritt (verlängert den Einstieg), hartnäckiges
   Nachfragen (widerspricht „ruhige Oberfläche").
5. **Reboot und Zeitänderungen re-planen.** Alarme überleben keinen Neustart,
   und ein RTC-Alarm ist ein UTC-Zeitpunkt — nach Zeitzonen-/Uhrwechsel läge
   „20:00" sonst daneben. Ein Manifest-Receiver auf `BOOT_COMPLETED`,
   `TIME_SET`, `TIMEZONE_CHANGED` plant neu (nur bei abgeschlossenem
   Onboarding). Verworfen: ignorieren (die Erinnerung wäre nach jedem Neustart
   still weg — genau der schleichende Tod der Gewohnheit).
6. **Tap öffnet direkt den Editor.** Kernschleife Schritt 1 → 2 ohne Umweg;
   die Notification räumt sich weg (`autoCancel`). Verworfen: Launcher öffnen
   (ein Hop mehr, null Nutzen).

## Umfang / Nicht-Umfang

- **Rein:** neues Paket `reminder/` (reine Zeitberechnung, Scheduler,
  Notification-Receiver, Reschedule-Receiver), Notification-Channel,
  einmalige Berechtigungsabfrage im Editor, Skip-Regel inkl. DAO-Zählabfrage,
  Manifest-Einträge (Berechtigungen + Receiver), Strings, JVM-Test für die
  Zeitberechnung, DAO-Test für die Zählabfrage.
- **Raus (bewusst):** konfigurierbare Uhrzeit und Ein-/Aus-Schalter (A-6),
  persönliche Anrede mit Namen in der Notification (bewusst generisch —
  Sperrbildschirm), Nachfass-/Wiederholungslogik, exakte Alarme,
  Streak-/Gamification-Mechanik jeder Art.

## Spezifikation

### UX-Ablauf & Zustände

1. **Erster Editor-Start nach Onboarding:** System-Dialog „Benachrichtigungen
   erlauben?". Erlaubt → fertig. Abgelehnt → nie wieder gefragt, App läuft
   normal weiter.
2. **Täglich gegen 20:00** (inexakt, System darf batchen): Notification
   „Dein Impuls für heute" mit dem Tagesimpuls als Text (BigText für lange
   Impulse), Herz-Icon, Channel „Täglicher Impuls".
   - **Außer:** heute existiert schon ein Eintrag → nichts passiert.
   - **Außer:** Berechtigung fehlt/Channel deaktiviert → nichts passiert
     (kein Fehler).
3. **Tap:** öffnet `JournalEditorActivity` (zeigt denselben Impuls),
   Notification verschwindet. Es gibt nur eine Notification-ID — ein
   ungelesener Vortages-Impuls wird ersetzt, nie gestapelt.

### Interaktion mit Bestehendem

- `JournalEditorActivity.onCreate`: plant den Alarm **idempotent** bei jedem
  Start (gleiche `PendingIntent`-Identität ersetzt den bestehenden) und stellt
  einmalig die Berechtigungsfrage. Kein neuer Screen, kein Manifest-Eintrag
  für Activities.
- `PromptProvider` wird vom Receiver mit dem `epochDay` des Feuertags
  aufgerufen — Notification und Editor zeigen zwangsläufig denselben Impuls
  (gleiche Quelle, gleiche Formel).
- Beide Receiver prüfen `LocalProfileStore.isOnboardingComplete` — vor
  Abschluss des Onboardings wird weder geplant noch gezeigt.

### Datenmodell / Persistenz

Kein Schema-Eingriff, `journal.db` bleibt Version 1. Neu: nicht-suspendierende
DAO-Zählabfrage `countSinceBlocking(epochMillis)` (der Receiver hat keinen
Coroutine-Scope; er ruft sie via `goAsync()` auf einem Arbeits-Thread auf).
Zusätzlich merkt sich `reminder_prefs` (SharedPreferences), ob die
Berechtigungsfrage schon gestellt wurde.

### Externe Abhängigkeiten & Fallback

**Keine neue Abhängigkeit.** `AlarmManager`, `NotificationManager` und
`NotificationCompat` (androidx.core, vorhanden) genügen. Kein Netzwerk.

### Randfälle & Fehlerbehandlung

- **Berechtigung abgelehnt:** Alarm bleibt geplant, der Receiver bricht still
  ab — erlaubt die Nutzerin die Benachrichtigungen später in den
  System-Einstellungen, funktioniert die Erinnerung ohne weiteres Zutun.
- **Gerät um 20:00 aus:** `RTC_WAKEUP`-Repeating holt einen verpassten Termin
  nicht nach; die nächste Erinnerung kommt am Folgetag — bewusst, kein
  Nachfassen.
- **Neustart / Zeitumstellung / Zeitzonenwechsel:** Reschedule-Receiver plant
  auf das nächste lokale 20:00 neu.
- **App um 20:00 offen:** die Notification erscheint trotzdem — harmlos, und
  wer gerade schreibt, hat danach die Skip-Regel auf seiner Seite.
- **Impuls um Mitternacht:** Receiver berechnet den Impuls zum Feuerzeitpunkt;
  bleibt die Notification bis nach Mitternacht liegen, zeigt der Editor den
  neuen Tagesimpuls — akzeptiert, der Editor ist die Quelle der Wahrheit.
- **Doze/Standby:** inexakte Alarme dürfen verschoben werden; für eine
  Abenderinnerung ist eine Verschiebung um Minuten belanglos.

### Barrierefreiheit

Notification mit Standard-System-Darstellung (Screenreader-tauglich per se),
Titel + Impuls als Text, keine Information nur über das Icon. Alle Texte aus
`strings.xml` (Deutsch, wie alle neuen Strings).

### Testplan

- **JVM (`app/src/test/`):** `ReminderTimeCalculatorTest` — vor 20:00 → heute
  20:00; nach 20:00 → morgen; exakt 20:00 → morgen; Monats-/Jahreswechsel.
- **Instrumented (`app/src/androidTest/`):** `JournalEntryDaoTest` um
  `countSinceBlocking` erweitert (zählt ab Schwelle, ignoriert Ältere, 0 bei
  leerer DB).
- **Manuell (Gerät):** Onboarding abschließen → Berechtigungsdialog erscheint
  genau einmal; Uhr kurz vor 20:00 stellen → Notification kommt, Tap landet im
  Editor; Eintrag speichern, Alarm erneut feuern lassen → keine Notification;
  Neustart → Erinnerung kommt weiterhin.

### Doku-/Backlog-Auswirkungen

- `CLAUDE.md` + `docs/ONBOARDING.md`: Code-Map um `reminder/` ergänzen,
  Testlisten erweitern.
- `docs/anforderungen/README.md`: „Ausgangslage" („kein Notification-Code")
  aktualisieren, Statuszeile pflegen.
- `docs/BACKLOG.md`: nicht betroffen.

## Definition of Done

- Nach abgeschlossenem Onboarding ist der tägliche Alarm geplant; gegen 20:00
  erscheint höchstens eine Notification mit dem Tagesimpuls des Feuertags.
- Existiert am Feuertag bereits ein Eintrag, erscheint keine Notification.
- Tap auf die Notification öffnet den Journal-Editor mit demselben Impuls.
- Erinnerung überlebt Neustart und Zeitzonenwechsel (Receiver plant neu).
- Berechtigung wird genau einmal erfragt; eine Ablehnung lässt die App voll
  funktionsfähig und wird nicht erneut erfragt.
- Vor Abschluss des Onboardings erscheint nie eine Notification.
- Keine neue Abhängigkeit, keine Netzwerk-Berechtigung, kein Eintragsinhalt
  in der Notification; Strings in `strings.xml`.
- Receiver im Manifest (`exported="false"`); `./gradlew test` grün inkl.
  `ReminderTimeCalculatorTest`; DAO-Erweiterung unter `connectedAndroidTest`
  abgedeckt.

## Umsetzungsschritte

1. `reminder/ReminderTimeCalculator.kt` (rein: nächster Termin aus Jetzt +
   Uhrzeit) + JVM-Test.
2. `reminder/ReminderScheduler.kt` (idempotentes `setInexactRepeating`,
   Konstante 20:00).
3. DAO-Zählabfrage `countSinceBlocking` + Erweiterung `JournalEntryDaoTest`.
4. `reminder/DailyReminderReceiver.kt` (Berechtigungs-Check, Skip-Regel via
   `goAsync`, Channel, Notification mit Tagesimpuls, Tap → Editor).
5. `reminder/ReminderRescheduleReceiver.kt` (`BOOT_COMPLETED`, `TIME_SET`,
   `TIMEZONE_CHANGED` → neu planen, nur nach Onboarding).
6. Manifest: `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`, beide Receiver
   `exported="false"`.
7. `JournalEditorActivity`: Alarm planen + einmalige Berechtigungsfrage
   (`reminder_prefs`).
8. Strings (Channel-Name/-Beschreibung, Notification-Titel) auf Deutsch.
9. Doku nachziehen (CLAUDE.md, ONBOARDING.md, Übersicht/Ausgangslage,
   Statuszeile).
