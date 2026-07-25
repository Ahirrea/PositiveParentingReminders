# Entscheidungen (ADRs)

Architektur- und Grundsatzentscheidungen, je Entscheidung eine Datei
(`ADR-<Nr>-<kurz-titel>.md`). Dieser Ordner ist **append-only**: ein ADR wird
nie umgeschrieben. Kehrt eine Entscheidung sich um, entsteht ein **neuer** ADR
und der alte bekommt `Status: ersetzt durch ADR-<Nr>`. So bleibt nachvollziehbar,
was wann warum galt.

Ein ADR entsteht, wenn eine Entscheidung die Architektur ändert oder das Projekt
langfristig bindet (Schritt 5 des [Prozesses](../PROZESS.md)). Weichen innerhalb
einer einzelnen Anforderung bleiben in der Anforderungsdatei.

## Übersicht

| Nr. | Entscheidung | Status | Kern |
|---|---|---|---|
| [ADR-001](./ADR-001-native-android-klassische-views.md) | Native Android mit klassischen Views | akzeptiert | Nachträglich dokumentiert: gebaut wurde nativ Kotlin, nicht Cross-Platform wie im alten PRD. Das PRD ist korrigiert worden, nicht der Code. |
| [ADR-002](./ADR-002-account-schritt-ohne-backend.md) | „Account erstellen" im Onboarding — ohne Backend | **offen** | Der Screen verspricht ein Konto, das es laut Nicht-Ziel nicht geben soll. Vier Optionen, Empfehlung: lokales Profil. |
| [ADR-003](./ADR-003-zwei-plattformen.md) | Zwei parallele Implementierungen (Android + Next.js) | **offen** | `web/` bildet denselben Onboarding-Flow ein zweites Mal ab und steht in keiner Anleitung. Vier Optionen, Empfehlung: Android ist das Produkt, `web/` ist Prototyp. |

**Zwei Entscheidungen sind offen** und warten auf die Ideengeberin. Bis dahin
beschreibt das PRD den Ist-Zustand und benennt den Widerspruch, statt ihn
stillschweigend aufzulösen.

## Neuen ADR anlegen

1. Nächste freie Nummer nehmen (dreistellig, `ADR-004`, …). Nummern werden nie
   wiederverwendet.
2. Datei nach dem Muster unten anlegen.
3. Zeile in der Tabelle oben ergänzen.
4. Kehrt der ADR eine frühere Entscheidung um: im alten ADR `Status:` auf
   `ersetzt durch ADR-<Nr>` setzen — das ist die **einzige** erlaubte Änderung an
   einem bestehenden ADR.

## Aufbau

```markdown
# ADR-<Nr>: <Titel>

**Status:** vorgeschlagen | akzeptiert | ersetzt durch ADR-<Nr>
**Datum:** <Datum>

## Kontext
<Welche Kräfte wirken? Was war die Ausgangslage?>

## Entscheidung
<Was wird getan — im Aktiv, ein Satz.>

## Begründung
<Warum diese und nicht die Alternativen.>

## Verworfene Alternativen
<je Alternative ein Satz, warum nicht.>

## Konsequenzen
<Was folgt daraus, auch das Unangenehme. Was darf jetzt nicht mehr passieren?>
```
