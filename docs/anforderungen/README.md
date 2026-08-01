# Anforderungen

Verfeinerte Ideen — Ergebnis des [Refinement-Prozesses](../PROZESS.md). Rein
technische Aufgaben und kleine Fixes laufen über [`BACKLOG.md`](../BACKLOG.md);
die Trennlinie steht im
[Prozess](../PROZESS.md#anforderung-oder-aufgabe-der-test).

Diese Übersicht ist der Einstieg — und die **einzige Quelle für den Status**: die
Anforderungsdateien selbst führen keinen Status, damit nichts auseinanderlaufen
kann. Eine erledigte Anforderung **bleibt liegen, wo sie ist**; sie ist ab dann
das Protokoll, *warum* es so gelöst wurde.

**Zeile oder Datei?** Eine rohe Idee bleibt eine Zeile in der Tabelle. Erst bei
der Verfeinerung entsteht `A-<Nr>-<kurz-titel>.md` und die Zeile wird verlinkt.

**Statuslegende:** `💡 Idee` · `✅ bereit` · `🚧 in Umsetzung` · `🏁 erledigt`
· `🧊 zurückgestellt` · `🗑 verworfen`

## Ausgangslage

Fertig sind das **Onboarding** (`OnboardingActivity` → `OnboardingStep2Activity` →
`OnboardingStep3Activity` → `ProfileSetupActivity`, vor A-10
`AccountCreationActivity`) und seit A-1 der **Journal-Editor**
(`JournalEditorActivity` + Room-Datenschicht in `data/`) — nach abgeschlossenem
Onboarding startet die App direkt im Editor. Der Rest ist Gerüst:

- `journal/JournalOverviewActivity` — Stub, das `setContentView` ist
  auskommentiert, nicht im Manifest.
- `insights/InsightsActivity` — hat ein Layout, ist nicht im Manifest.
- `settings/SettingsActivity` — Stub.
- Kein Notification-Code, kein KI-Code.

Mit A-1 liegt das Fundament: Einträge werden lokal in Room gespeichert
([ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md)) — darauf
bauen A-2 (lesen), A-7 (Rückblick) und A-9 (Export) auf.

## Übersicht

| Nr. | Anforderung | Status | Worum es geht |
|---|---|---|---|
| A-1 | [Eintrag schreiben und speichern](./A-1-eintrag-schreiben-und-speichern.md) | 🏁 erledigt | Der Kern des Produkts: Tagesimpuls, Textfeld, Stimmung, Speichern — lokal in Room ([ADR-004](../entscheidungen/ADR-004-room-als-lokale-persistenz.md)). Bedient PRD „Kernschleife" Schritte 1–2. Umgesetzt 2026-08-01. |
| A-2 | [Journal-Übersicht](./A-2-journal-uebersicht.md) | ✅ bereit | Liste der eigenen Einträge, neueste zuerst, zum Wiederlesen — nur lesen, bewusst ohne Auswertung (die bleibt A-7). Setzt A-1 voraus. |
| A-3 | Ein Impuls pro Tag als Notification | 💡 Idee | „Einen einzigen Impuls pro Tag" ist die User Story, an der die ganze Gewohnheit hängt. Es existiert noch kein Notification-Code, und die Uhrzeit gehört konfigurierbar (→ A-6). |
| A-4 | Stimmung erfassen | 🗑 verworfen | Aufgegangen in [A-1](./A-1-eintrag-schreiben-und-speichern.md) (Entscheidung vom 2026-07-31): die Stimmungsauswahl gehört von Anfang an in den Editor, sonst fehlen dem Rückblick später die Daten. |
| A-5 | Einträge mit Themen versehen | 💡 Idee | „Streit ums Zubettgehen", „Geschwisterstreit" — die Voraussetzung dafür, wiederkehrende Situationen überhaupt zu erkennen. Offen: feste Themenliste oder freie Schlagworte. |
| A-6 | Einstellungen | 💡 Idee | Erinnerungszeit, Name, Datenexport/-löschung. `SettingsActivity` ist ein Stub. |
| A-7 | Rückblick über Wochen und Monate | 💡 Idee | Der eigentliche Nutzen des Produkts (PRD „Kernschleife" Schritt 3). `InsightsActivity` hat ein Layout, aber keine Registrierung und keine Daten. Zunächst **regelbasiert** über die eigenen Einträge — bewusst ohne KI. |
| A-8 | KI-gestützte Insights | 💡 Idee | Die Gemini-Abhängigkeit ist deklariert, aber ungenutzt. Laut PRD-Nicht-Ziel **erst nach** einem funktionierenden Journal-Kern; braucht außerdem eine Feedback-Schleife („war das hilfreich?"), sonst erodiert Vertrauen. |
| A-9 | Datenexport und vollständiges Löschen | 💡 Idee | Ohne Cloud ist ein Gerätewechsel sonst Datenverlust — dasselbe Argument wie bei den anderen lokalen Projekten. Gehört zum Datenschutz-Versprechen dazu, nicht als Extra. |
| A-10 | [Account-Schritt zu lokalem Profil umbauen](./A-10-lokales-profil.md) | 🏁 erledigt | Umsetzung von [ADR-002](../entscheidungen/ADR-002-account-schritt-ohne-backend.md) (akzeptiert, Option B): der Schritt (jetzt `ProfileSetupActivity`) fragt nur noch einen Vornamen (optional den des Kindes), speichert lokal; Google-Login-Knopf und `GoogleLogo`-Icon sind entfallen. |

## Neue Anforderung aufnehmen

Schritt 7 des [Refinement-Prozesses](../PROZESS.md):

1. Für eine rohe Idee genügt eine neue Zeile mit Status `💡 Idee`.
2. Zur Verfeinerung [`_vorlage.md`](./_vorlage.md) nach
   `A-<nächste Nr>-<kurz-titel>.md` kopieren und ausfüllen (Nummern werden nicht
   wiederverwendet, auch nicht bei `🗑 verworfen`), dann die Zeile verlinken und
   auf `✅ bereit` setzen.
3. Status ausschließlich hier pflegen — auch später bei `🚧` und `🏁`.
