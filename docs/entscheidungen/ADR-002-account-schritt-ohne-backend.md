# ADR-002: „Account erstellen" im Onboarding — ohne Backend

**Status:** vorgeschlagen — **Entscheidung offen**
**Datum:** 2026-07-25

## Kontext

Der Onboarding-Flow endet in einer `AccountCreationActivity`. Gleichzeitig legt
das PRD v0.2 als Nicht-Ziel fest: **kein Backend, keine Cloud-Synchronisation,
kein Nutzerkonto** — die Einträge bleiben auf dem Gerät.

Ein Account-Schritt ohne Backend kann nur eins von zwei Dingen sein: eine
Attrappe, die nichts tut, oder das Versprechen eines Backends, das es nicht gibt
und laut Nicht-Ziel nicht geben soll. Beides ist ein Widerspruch, der auffällt,
sobald man den Screen benutzt.

Verstärkend: das ursprüngliche PRD forderte Firebase, verschlüsselte Datenbank
und Konten. Der Screen stammt aus dieser Welt.

## Zur Entscheidung stehende Optionen

**A — Schritt entfernen.** Onboarding endet nach der Werteseite, danach direkt
ins Journal. Ehrlichste Variante, passt zum Nicht-Ziel, entfernt einen
Reibungspunkt vor dem ersten Eintrag.

**B — Zu „lokales Profil" umbauen.** Der Screen fragt nur einen Vornamen (und
optional den des Kindes) und speichert lokal. Behält den persönlichen Einstieg,
ohne ein Konto zu behaupten. Der „Mit Google anmelden"-Knopf und das
`GoogleLogo`-Icon müssten weg.

**C — Als Attrappe stehen lassen.** Billigste Variante heute, teuerste später:
der erste Nutzer, der auf ein neues Gerät wechselt, erwartet, dass seine Daten
mitkommen.

**D — Backend doch bauen.** Kehrt das Nicht-Ziel um und bringt Serverkosten,
DSGVO-Auftragsverarbeitung und Datenleck-Risiko für die intimsten Daten der App
zurück.

## Empfehlung

**B**, mit A als naher Alternative. Ein lokales Profil erhält das Warme am
Einstieg („Hallo, Katharina"), ohne ein Konto zu versprechen. D widerspricht dem
stärksten Nicht-Ziel des Produkts und sollte nur nach ausdrücklicher Umkehrung
dieses Nicht-Ziels gewählt werden — dann als eigener ADR.

## Offen

Diese Entscheidung trifft die Ideengeberin. Bis dahin bleibt der Screen, wie er
ist; der Widerspruch ist im PRD unter „Nicht-Ziele" benannt.
