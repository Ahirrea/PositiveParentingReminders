# A-10 Account-Schritt zu lokalem Profil umbauen

[← Anforderungen](./README.md) · [Prozess](../PROZESS.md)
· Status siehe [Übersicht](./README.md#übersicht)

**User Story:** Als Elternteil möchte ich beim Einstieg nur meinen Vornamen
(optional den meines Kindes) angeben, um persönlich angesprochen zu werden —
ohne ein Konto anlegen zu müssen, das es laut Produktversprechen gar nicht gibt.

**Verfeinert am:** 2026-07-31
**Bedient PRD:** Nicht-Ziel „kein Backend, keine Cloud, kein Nutzerkonto";
„4 Zielgruppe — persönlicher, warmer Einstieg"
**Eingeschränkt durch:** [ADR-002](../entscheidungen/ADR-002-account-schritt-ohne-backend.md)
(akzeptiert, Option B — dort ist der Lösungsraum A–D dokumentiert)

## Andockpunkte im Code

- `onboarding/AccountCreationActivity.kt` + `activity_account_creation.xml` —
  der umzubauende Schritt: E-Mail-Feld mit Validierung, „Create account"- und
  „Sign in with Google"-Knopf, beide nur mit Platzhalter-Toasts.
- `drawable/ic_google_logo.xml` — nur von diesem Screen benutzt, entfällt.
- `OnboardingStep3Activity` — startet den Schritt per Intent.
- [A-1](./A-1-eintrag-schreiben-und-speichern.md), Entscheidung 4: das
  SharedPreferences-Flag `onboarding_complete` setzt der Abschluss-Knopf dieses
  Screens — A-10 übernimmt diesen Punkt.
- **Fehlt:** jegliche Persistenz für Profildaten; es gibt noch kein
  `app/src/test/`.

## Spannung zu Nicht-Zielen — und Auflösung

Der Screen selbst *war* die Spannung: ein Account-Versprechen in einer App,
deren stärkstes Nicht-Ziel „kein Backend, keine Cloud, kein Nutzerkonto" ist.
ADR-002 löst sie auf (Option B): lokales Profil statt Konto, nichts verlässt
das Gerät. Der neue Screen behauptet nirgends mehr ein Konto — auch nicht in
Texten („gespeichert nur auf diesem Gerät" statt „encrypted account").

## Entscheidungen (mit Begründung)

1. **Lokales Profil statt Schritt entfernen** — von der Ideengeberin am
   2026-07-31 in ADR-002 entschieden (Option B statt A): der warme Einstieg
   („Hallo, Katharina") bleibt erhalten, ohne ein Konto zu versprechen.
2. **Klasse wird umbenannt** (`AccountCreationActivity` →
   `ProfileSetupActivity`, Layout `activity_profile_setup.xml`): „Account"
   darf laut ADR-002 nirgends mehr behauptet werden — auch nicht im Code.
   Verworfen: Namen behalten (billiger, aber der Widerspruch bliebe im Code
   stehen).
3. **Vorname ist Pflicht, Kindname optional** — ADR-002 formuliert „fragt nur
   einen Vornamen (und optional den des Kindes)". Der Speichern-Knopf bleibt
   deaktiviert, solange der Vorname leer ist (gleiches Muster wie vorher bei
   der E-Mail). Verworfen: Überspringen-Link — kann nachgerüstet werden, wenn
   sich die Pflicht als Reibung erweist.
4. **SharedPreferences, nicht Room** — zwei Namen und ein Flag sind
   Schlüssel-Wert-Daten; die Room-Datenbank (ADR-004) ist für Journal-Einträge
   und existiert noch nicht. A-1 erwartet das Flag ohnehin in
   SharedPreferences. Verworfen: auf Room warten (unnötige Kopplung an A-1).
5. **Speichern setzt `onboarding_complete`** — Profil speichern *ist* der
   Onboarding-Abschluss (A-1, Entscheidung 4). Die Weiche im Launcher, die das
   Flag liest, kommt mit A-1.
6. **Texte auf Deutsch** — Konvention aus `CLAUDE.md`: neue nutzersichtbare
   Strings sind deutsch. Die Schritte 1–3 des Onboardings bleiben vorerst
   englisch (eigene Aufgabe, wenn gewünscht).

## Umfang / Nicht-Umfang

- **Rein:** Umbau des Screens (zwei Namensfelder, Speichern-Knopf,
  Datenschutz-Hinweis), lokale Persistenz (`LocalProfile`,
  `LocalProfileStore`), Onboarding-Flag, Entfernen von Google-Knopf, Logo und
  E-Mail-Validierung, erste JVM-Test-Suite (`app/src/test/`), Doku-Pflege.
- **Raus (bewusst):** Verwendung der Anrede („Hallo, Katharina") in Editor
  oder Rückblick (kommt mit den jeweiligen Screens), Profil später ändern
  (A-6 Einstellungen), Weiterleitung nach dem Speichern (A-1 baut die
  Launcher-Weiche und das Ziel), Übersetzung der übrigen Onboarding-Schritte.

## Spezifikation

### UX-Ablauf & Zustände

`ProfileSetupActivity`, weiterhin letzter Schritt des Onboardings:

1. Titel/Untertitel erklären, wofür der Vorname ist und dass er das Gerät
   nicht verlässt.
2. Feld „Dein Vorname" (Pflicht, Autofill `personGivenName`), Feld „Vorname
   deines Kindes (optional)" (kein Autofill).
3. „Profil speichern" ist deaktiviert, solange der Vorname leer/nur
   Whitespace ist. Beim Tippen: Profil speichern, Flag setzen, Bestätigung
   („Hallo, %s! Dein Profil ist gespeichert.").
4. Erneutes Öffnen zeigt das gespeicherte Profil vor (Tippfehler
   korrigierbar); erneutes Speichern überschreibt.

### Datenmodell / Persistenz

Neues Paket `com.positiveparenting.profile`:

- `LocalProfile` — reines Kotlin (JVM-testbar): `parentName: String`,
  `childName: String?`; Eingabe-Normalisierung (trimmen, innere
  Whitespace-Läufe zusammenfassen), leerer Kindname → `null`.
- `LocalProfileStore` — SharedPreferences (`local_profile`): `parent_name`,
  `child_name`, `onboarding_complete`. `save()` setzt das Flag mit.

### Randfälle & Fehlerbehandlung

- Nur-Whitespace-Vorname → Knopf deaktiviert; `fromInput` liefert zusätzlich
  `null` (doppelter Boden).
- Rotation: Instance State der `EditText`s gewinnt gegen das Vorbefüllen
  (Vorbefüllung nur bei `savedInstanceState == null`).
- SharedPreferences-`apply()` ist asynchron, aber prozess-sicher — kein
  eigener Fehlerpfad nötig.

### Barrierefreiheit

Hints über `TextInputLayout` (werden vorgelesen), Autofill-Hint für den
eigenen Vornamen, Touch-Ziele unverändert ≥ 48 dp, alle Texte aus
`strings.xml`.

### Testplan

- **JVM (`app/src/test/`, legt die Suite an):** `LocalProfileTest` —
  Normalisierung, Pflicht-Vorname, optionaler Kindname,
  Knopf-Regel (`isValidParentName`).
- Manuell: Onboarding durchlaufen → Namen speichern → Toast mit Anrede; App
  neu öffnen → Felder vorbefüllt.

### Doku-/Backlog-Auswirkungen

`CLAUDE.md` (Code-Map, Sprach- und Test-Hinweise), `docs/ONBOARDING.md`
(Flow-Diagramm, Dateibaum), `docs/BACKLOG.md` („Test-Suite anlegen":
JVM-Hälfte erledigt), A-1 (Verweise auf den alten Klassennamen).

## Definition of Done

- Der Screen fragt nur noch Vornamen (Kind optional) und speichert lokal;
  Google-Knopf, `ic_google_logo` und E-Mail-Validierung sind restlos entfernt.
- Nirgends — UI-Text, Klassenname, Ressource — wird mehr ein Konto behauptet.
- `onboarding_complete` wird beim Speichern gesetzt (Übergabepunkt an A-1).
- Neue Texte deutsch, in `strings.xml`; keine neuen Abhängigkeiten.
- `ProfileSetupActivity` im Manifest registriert (`exported="false"`).
- JVM-Tests grün; Doku und Statusübersicht gepflegt.

## Umsetzungsschritte

1. `LocalProfile` + `LocalProfileStore` unter `com.positiveparenting.profile`.
2. Activity/Layout umbenennen und umbauen; Strings ersetzen (deutsch);
   `ic_google_logo` löschen; Manifest und `OnboardingStep3Activity` anpassen.
3. `LocalProfileTest` als erste JVM-Test-Suite.
4. Doku nachziehen (CLAUDE.md, ONBOARDING.md, BACKLOG, A-1, Statuszeile).
