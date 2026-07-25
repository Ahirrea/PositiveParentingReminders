# PRD – Positive Parenting Reminders

**Eine tägliche Mikro-Journaling-App für Eltern: kurz aufschreiben, wie der Tag
mit dem Kind war, und darin Muster erkennen.**

| | |
|---|---|
| Status | Entwurf v0.2 (2026-07-25) |
| Plattform | Native Android (Kotlin), klassische Views |
| Zielgruppe (initial) | Persönliches Projekt (Single User) |

Verfeinerte Anforderungen: [`anforderungen/README.md`](./anforderungen/README.md) ·
Entscheidungen: [`entscheidungen/README.md`](./entscheidungen/README.md) ·
Technische Aufgaben: [`BACKLOG.md`](./BACKLOG.md) ·
Entwickler-Einstieg: [`ONBOARDING.md`](./ONBOARDING.md)

> **Warum v0.2 ein Neuschrieb ist.** Das ursprüngliche PRD
> ([Archiv](./archiv/PRD-urspruenglich-2026-07.txt)) beschrieb ein anderes Produkt
> als das, was im Repo steht: es forderte ein Cross-Platform-Framework
> (React Native/Flutter), iOS **und** Android, ein Firebase-Backend, eine
> Datenbank, LLM-Integration und Ende-zu-Ende-Verschlüsselung. Tatsächlich
> existiert eine **native Kotlin-Android-App mit klassischen Views, ohne Backend,
> ohne Datenbank und ohne eine Zeile KI-Code**. Dazu kamen Geschäfts-KPIs
> (DAU/MAU-Ziel 40 %, Retention D1/D7/D30, NPS, App-Store-Bewertungen) und ein
> „For the Business"-Abschnitt, die für ein Soloprojekt ohne Launch-Absicht keine
> Steuerungswirkung haben.
>
> Ein PRD, dem man nicht glauben kann, ist schlimmer als keins. v0.2 beschreibt
> deshalb das Produkt, das gebaut wird, und benennt offen, was noch nicht
> entschieden ist. Das Original bleibt unverändert im Archiv — die User Stories
> und die Problemanalyse darin sind weiter gültig und sind hier eingeflossen.

---

## 1. Problem

Viele Eltern kennen die Prinzipien positiver Erziehung, scheitern aber daran, sie
im Alltag anzuwenden. Zwischen Arbeit, Haushalt und Müdigkeit fehlt der Moment,
in dem man die eigene Reaktion noch einmal ansieht: *Warum habe ich vorhin
geschrien? Was war der Auslöser?*

Was fehlt, ist kein weiterer Ratgeber — Information gibt es reichlich —, sondern
ein einfaches, **privates** Werkzeug, in dem man die eigenen Reaktionen in zwei
Minuten festhält und über Wochen ein Muster erkennt. Ohne dieses Innehalten
bleiben Schuldgefühle, Reaktivität in schwierigen Momenten und verpasste
Gelegenheiten für Nähe.

Das Kernproblem ist nicht Wissensmangel, sondern die Übersetzung von Wissen in
beständige, achtsame Praxis.

## 2. Zielnutzer:in

Initial: die Entwicklerin selbst — ein Elternteil mit wenig Zeit und dem Wunsch,
die eigenen Reaktionsmuster besser zu verstehen. Sie schreibt abends auf dem
Handy, hat zwei bis drei Minuten und will dabei nicht das Gefühl haben, eine
Aufgabe abzuarbeiten.

Die App wird so gebaut, dass eine spätere Öffnung für andere architektonisch
möglich bleibt, aber nichts darauf optimiert wird.

## 3. Ziele

- **Ein Eintrag dauert unter drei Minuten.** Alles andere ist der Feind des
  Produkts: wird es zur Pflicht, hört man auf.
- **Ein geführter Impuls pro Tag**, damit man nicht vor einem leeren Feld sitzt.
- **Stimmung und Thema festhalten**, damit aus Einzeleinträgen ein Muster
  ablesbar wird („Streit ums Zubettgehen häuft sich montags").
- **Rückblick über Wochen und Monate** — der eigentliche Nutzen entsteht erst im
  Zeitverlauf, nicht im einzelnen Eintrag.
- **Ruhige, reibungslose Oberfläche.** Das Schreiben soll sich wie ein Geschenk
  an sich selbst anfühlen, nicht wie ein Formular.

### User Stories (aus dem ursprünglichen PRD, weiterhin gültig)

- Als neues Elternteil möchte ich einen einfachen Einstieg und einen klaren
  Startpunkt, damit ich anfangen kann, ohne mich überfordert zu fühlen.
- Als berufstätiges Elternteil möchte ich **einen** Impuls pro Tag erhalten,
  damit ich in zwei bis drei Minuten unterwegs reflektieren kann.
- Als Elternteil in einer schwierigen Phase (z. B. Trotzanfälle) möchte ich
  Einträge mit Themen versehen, um meine Reaktionen auf wiederkehrende
  Situationen zu verfolgen.
- Als reflektierendes Elternteil möchte ich einen Wochen- oder Monatsrückblick
  meiner Stimmungsverläufe sehen, um meine Auslöser zu verstehen.
- Als datenschutzbewusste Nutzerin möchte ich sicher sein, dass meine Einträge
  privat bleiben, damit ich vollkommen ehrlich schreiben kann.

## 4. Nicht-Ziele

Dieser Abschnitt fehlte im ursprünglichen PRD vollständig. Er ist der wichtigste
Teil des Dokuments, weil er festlegt, was **nicht** gebaut wird:

- **Kein Backend, keine Cloud-Synchronisation, kein Nutzerkonto.** Die Einträge
  sind das Intimste, was diese App anfasst. Sie bleiben auf dem Gerät. Damit
  entfallen Firebase, Serverkosten, DSGVO-Auftragsverarbeitung und die gesamte
  Klasse von Datenleck-Risiken. Der Onboarding-Schritt „Account erstellen"
  existiert im Code und steht im Widerspruch dazu — siehe
  [offene Entscheidung](./entscheidungen/ADR-002-account-schritt-ohne-backend.md).
- **Keine KI, solange der Journal-Kern nicht steht.** Die Gemini-Abhängigkeit ist
  deklariert, aber ungenutzt. KI-Insights sind wertlos, solange es keine Einträge
  gibt, über die sie etwas sagen könnten.
- **Kein iOS in dieser Codebasis.** Es ist eine native Android-App
  ([ADR-001](./entscheidungen/ADR-001-native-android-klassische-views.md)).
- **Keine Monetarisierung, keine Premium-Funktionen, kein Marketing.** Privates
  Projekt. Der „For the Business"-Teil des ursprünglichen PRD ist gestrichen.
- **Kein Teilen von Einträgen**, in keiner Form.

## 5. Kernschleife

1. Abends kommt **ein** Impuls („Wann warst du heute stolz auf dich?").
2. Sie tippt zwei bis drei Sätze, wählt eine Stimmung und optional ein Thema.
3. Nach einigen Wochen öffnet sie den Rückblick und sieht ein Muster — welche
   Situationen sich häufen und wie sie darauf reagiert hat.

Schritt 1 und 2 müssen zusammen unter drei Minuten bleiben. Schritt 3 ist der
Grund, warum man Schritt 1 und 2 überhaupt durchhält.

## 6. Erfolgskriterien

Ehrliche Kriterien für ein Projekt ohne Launch-Absicht — die Kennzahlen aus dem
ursprünglichen PRD (DAU/MAU 40 %, Retention, NPS, Store-Bewertungen) sind
gestrichen, weil es keine Nutzerbasis gibt, an der man sie messen könnte:

- Die Entwicklerin benutzt die App **selbst über vier Wochen hinweg** an den
  meisten Tagen, ohne sich dazu zwingen zu müssen.
- Ein Eintrag ist in unter drei Minuten fertig — nachgemessen, nicht geschätzt.
- Nach vier Wochen lässt sich im Rückblick **mindestens ein Muster** benennen,
  das ohne die App nicht aufgefallen wäre.
- Kein Eintrag verlässt jemals das Gerät.

## 7. Rahmenbedingungen

- **Plattform:** Native Android, Kotlin `2.2.0`, `minSdk 33`, `compileSdk`/
  `targetSdk 36`, JVM-Ziel 11. UI in klassischen Android-Views (XML-Layouts,
  `ConstraintLayout`, `AppCompatActivity`, `findViewById`) — siehe
  [ADR-001](./entscheidungen/ADR-001-native-android-klassische-views.md).
- **App-Id / Namespace:** `com.positiveparenting`.
- **Abhängigkeiten** laufen über den Version-Katalog
  `gradle/libs.versions.toml` (Lottie ist heute die einzige Ausnahme).
- **Datenhaltung:** ausschließlich lokal auf dem Gerät. Welche Technik (Room,
  DataStore, SQLite) ist noch nicht entschieden und gehört zur ersten
  Journal-Anforderung.
- **Secrets:** API-Keys gehören in `local.properties` (git-ignoriert) und werden
  über `BuildConfig` per secrets-gradle-plugin durchgereicht. Niemals Keys oder
  eine `local.properties` committen.
- **Sprache:** Der Code und die bisherigen Dokumente sind auf Englisch,
  die UI-Strings liegen in `res/values/strings.xml`. Neue Dokumentation in diesem
  Ordner ist auf Deutsch.
- **Es gibt noch keine Tests** — weder `app/src/test/` noch `app/src/androidTest/`.
  Nur die Standard-JUnit-/Espresso-Abhängigkeiten sind deklariert.

### Risiken & Gegenmaßnahmen

| Risiko | Auswirkung | Gegenmaßnahme |
|---|---|---|
| Journaling wird als Pflicht empfunden | Man hört nach zwei Wochen auf | Radikal kurz halten (unter drei Minuten), ruhige Oberfläche, ein Impuls statt einer leeren Seite |
| Insights bleiben generisch oder unpassend | Vertrauen ist weg, und zwar dauerhaft | Erst regelbasierte Rückblicke über die eigenen Daten, KI später und nur mit Feedback-Schleife |
| Sensible Daten gehen verloren oder nach außen | Katastrophal — es sind Aussagen über eigene Kinder | Kein Backend, keine Cloud, keine Analytics. Das ist als Nicht-Ziel festgeschrieben, nicht als Absicht |
| Zwei parallele Implementierungen (Android + Next.js) driften auseinander | Doppelte Arbeit, widersprüchliche Zustände | Offene Entscheidung, siehe [ADR-003](./entscheidungen/ADR-003-zwei-plattformen.md) |
