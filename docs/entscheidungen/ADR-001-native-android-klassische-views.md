# ADR-001: Native Android mit klassischen Views

**Status:** akzeptiert (nachträglich dokumentiert am 2026-07-25)
**Datum:** 2026-07-25

## Kontext

Das ursprüngliche PRD forderte „ein Cross-Platform-Framework (z. B. React Native,
Flutter)" und „iOS und Android". Gebaut wurde etwas anderes: eine **native
Kotlin-App für Android** mit klassischen Views (XML-Layouts,
`ConstraintLayout`, `AppCompatActivity`, `findViewById`).

Diese Entscheidung wurde nie aufgeschrieben — sie ist durch den Code entstanden.
Dieser ADR holt das nach, damit die Abweichung vom PRD dokumentiert ist statt
stillschweigend zu bestehen.

## Entscheidung

Die App bleibt **native Android in Kotlin mit klassischen Views**. Das PRD ist
darauf korrigiert worden (v0.2), nicht der Code auf das PRD.

## Begründung

- Für ein Soloprojekt mit einer Zielnutzerin auf einem Android-Gerät bringt
  Cross-Platform keinen Nutzen, sondern eine zusätzliche Abstraktionsschicht.
- iOS ist ohne Apple-Gerät und ohne Developer-Programm ohnehin nicht baubar.
- Klassische Views sind das, was bereits funktioniert: der komplette
  Onboarding-Flow läuft damit. Ein Wechsel mitten im halbfertigen Zustand kostet
  Zeit und bringt beim aktuellen Funktionsumfang nichts.

## Verworfene Alternativen

- **React Native / Flutter** (wie im ursprünglichen PRD): löst ein Problem
  (zwei Plattformen), das dieses Projekt nicht hat.
- **Jetpack Compose:** ist in `app/build.gradle.kts` bereits eingeschaltet, wird
  aber von **keinem** Screen benutzt. Ein Umstieg wäre vertretbar, aber er ist
  eine eigene Entscheidung und kein Nebeneffekt — siehe Konsequenzen.

## Konsequenzen

- Jeder neue Screen ist eine `AppCompatActivity` mit einem
  `res/layout/activity_*.xml` und `findViewById`, und muss **im
  `AndroidManifest.xml` registriert** werden (`exported="false"`, außer bei
  Launcher-/Deep-Link-Zielen). Heute ist nur der Onboarding-Flow registriert.
- **Compose und View Binding sind in `app/build.gradle.kts` aktiviert, aber
  ungenutzt.** Das ist irreführend: es sieht nach einer getroffenen Entscheidung
  aus, wo keine ist. Entweder bewusst nutzen oder abschalten — steht als Aufgabe
  im [Backlog](../BACKLOG.md).
- Kein Code lässt sich mit dem Next.js-Prototyp unter `web/` teilen (siehe
  [ADR-003](./ADR-003-zwei-plattformen.md)).
