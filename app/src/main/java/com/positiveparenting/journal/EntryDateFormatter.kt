package com.positiveparenting.journal

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Formats an entry's stored UTC moment for the overview list (A-2), e.g.
 * "Donnerstag, 1. August 2026 · 19:32". The time is included because several
 * entries per day are allowed. A fixed German pattern instead of
 * `ofLocalizedDateTime`: the journal is German by design and the fixed
 * pattern keeps the output deterministic for the JVM test. Pure — the zone
 * is injected so tests don't depend on the device default.
 */
object EntryDateFormatter {

    private val formatter =
        DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy · HH:mm", Locale.GERMAN)

    fun format(epochMillis: Long, zone: ZoneId): String =
        Instant.ofEpochMilli(epochMillis).atZone(zone).format(formatter)
}
