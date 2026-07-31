# ADR-003: Zwei parallele Implementierungen (Android + Next.js)

**Status:** akzeptiert — Option A
**Datum:** 2026-07-25, entschieden 2026-07-31

## Kontext

Im Repo liegen **zwei** Implementierungen desselben Onboarding-Flows:

- `app/` — die native Android-App (Kotlin, klassische Views). Onboarding
  vollständig verdrahtet, Journal/Insights/Settings sind Stubs.
- `web/` — ein Next.js-14-Projekt (React, TypeScript, Tailwind, eigenes
  `package.json` als `positive-parenting-web`) mit demselben Onboarding als
  Seiten: `onboarding`, `value`, `privacy`, `account`, dazu eigene
  UI-Bausteine (`Button`, `Screen`, `TextField`) und eine Lottie-Stage.

Der Web-Teil kam über den Commit „Add Next.js web app scaffold with ported
onboarding flow" hinzu. In `CLAUDE.md` ist er **nicht erwähnt** — wer die
Anleitung liest, erfährt nichts davon.

Zwei Implementierungen des gleichen Flows ohne gemeinsame Quelle bedeuten: jede
Änderung am Onboarding ist zweimal zu machen, und sie driften auseinander, sobald
das einmal vergessen wird. Kein Code ist teilbar — Kotlin/XML und React/TSX haben
nichts gemeinsam. Nur `web/lib/strings.ts` und `res/values/strings.xml` bilden
denselben Inhalt zweimal ab.

## Zur Entscheidung stehende Optionen

**A — Android ist das Produkt, `web/` ist ein Wegwerf-Prototyp.** Als solcher
kennzeichnen (eigene `web/README.md`: „Design-Prototyp, nicht ausgeliefert") und
nicht mitpflegen. Alle Anforderungen zielen auf Android.

**B — `web/` ist das Produkt, Android wird eingestellt.** Web ist schneller zu
bauen und überall erreichbar. Kostet den bereits verdrahteten Android-Flow und
verlangt für „täglicher Impuls" eine andere Lösung als Android-Notifications.

**C — Beide gleichrangig pflegen.** Doppelte Arbeit auf Dauer, ohne
Code-Teilung — bei einer Entwicklerin schwer zu halten.

**D — `web/` entfernen.** Am saubersten für den Fokus, verliert aber die
Design-Arbeit, die dort steckt (die Web-Version ist gestalterisch weiter).

## Empfehlung

**A.** Die native App ist erheblich weiter (Manifest, acht Activities,
Gradle-Setup), und die Zielnutzerin ist auf Android. `web/` als Design-Prototyp zu
kennzeichnen behält seinen Wert — man kann Layouts dort schnell ausprobieren —
ohne die Illusion, es gäbe zwei Produkte.

Was in jedem Fall passieren muss: **`web/` gehört in `CLAUDE.md` erwähnt.** Ein
ganzes Next.js-Projekt, das die Anleitung nicht kennt, ist eine Falle für die
nächste Sitzung.

## Entscheidung

Die Ideengeberin hat am 2026-07-31 **Option A** gewählt: **Android ist das
Produkt, `web/` ist ein Design-Prototyp** und wird nicht mitgepflegt.

## Konsequenzen

- `web/` ist per `web/README.md` als „Design-Prototyp, nicht ausgeliefert"
  gekennzeichnet und wird bei Änderungen am Onboarding **nicht** nachgezogen.
- Alle Anforderungen in der [Anforderungsliste](../anforderungen/README.md)
  zielen auf die Android-App.
- Soll `web/` je zum Produkt werden, kehrt das diese Entscheidung um und braucht
  einen neuen ADR.
