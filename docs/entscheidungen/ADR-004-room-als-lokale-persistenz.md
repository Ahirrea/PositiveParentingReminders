# ADR-004: Room als lokale Persistenz

**Status:** akzeptiert
**Datum:** 2026-07-31

## Kontext

Die App hat bisher **keinerlei Persistenz** — kein Datenmodell, keine Datenbank,
keine gespeicherten Einträge. Mit
[A-1](../anforderungen/A-1-eintrag-schreiben-und-speichern.md) entsteht die
erste zu speichernde Struktur (Journal-Einträge). Das PRD legt fest:
Datenhaltung **ausschließlich lokal** auf dem Gerät (Nicht-Ziel „kein Backend,
keine Cloud"), und die Wahl der Technik gehört zur ersten Journal-Anforderung.

Absehbare Zugriffe über A-1 hinaus: chronologische Liste (A-2), Aggregation
über Wochen/Monate für den Rückblick (A-7), Filter nach Thema (A-5), Export und
vollständiges Löschen (A-9).

## Entscheidung

Journal-Daten werden mit **Room** (SQLite mit typsicherer DAO-Schicht)
gespeichert.

## Begründung

- A-2 und vor allem A-7 sind **Abfrage-Anforderungen** („alle Einträge eines
  Monats", „Stimmungsverlauf über Wochen"). Das ist die Kernkompetenz einer
  relationalen Datenbank; Room macht sie typsicher und testbar (In-Memory-DB
  für Instrumented-Tests).
- Room ist der Android-Standard mit eingebauten **Schema-Migrationen** — das
  Datenmodell wird mit A-5 (Themen) sicher wachsen.
- Die Kosten sind überschaubar: KSP-Plugin und drei Einträge im
  Version-Katalog.

## Verworfene Alternativen

- **DataStore (Proto/JSON):** gut für Kleinkram wie Einstellungen oder das
  lokale Profil (A-10) — dafür bleibt es auch die richtige Wahl —, aber Listen
  filtern und über Monate aggregieren hieße, alles in den Speicher zu laden und
  von Hand zu filtern.
- **SQLite direkt:** keine neue Abhängigkeit, aber handgeschriebenes SQL,
  Cursor-Handling und Migrationen ohne Netz — mehr Code und Fehlerfläche ohne
  Vorteil gegenüber Room.

## Konsequenzen

- `app/build.gradle.kts` bekommt das **KSP-Plugin** (Version passend zu Kotlin
  `2.2.0`) und Room-Runtime/KTX/Compiler — **über den Version-Katalog**, wie
  alle Abhängigkeiten.
- Es entsteht ein neues Paket für die Datenschicht
  (`com.positiveparenting.data`), das nicht an ein einzelnes Feature gebunden
  ist — der Code-Map-Abschnitt in `CLAUDE.md` ist nachzuziehen.
- Schema-Änderungen laufen ab der zweiten Version über **Migrationen**, nie
  über `fallbackToDestructiveMigration` — die Einträge sind das Produkt,
  Datenverlust ist inakzeptabel.
- Kleinstdaten ohne Abfragebedarf (Onboarding-Flag, lokales Profil aus
  ADR-002) gehören **nicht** in die Datenbank; dafür genügen SharedPreferences
  bzw. später DataStore.
