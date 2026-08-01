package com.positiveparenting.journal

import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class EntryDateFormatterTest {

    private val berlin = ZoneId.of("Europe/Berlin")

    @Test
    fun `formats a summer evening in German with weekday, date and time`() {
        // 2026-08-01T17:32:00Z = 19:32 in Berlin (CEST, UTC+2), a Saturday.
        val epochMillis = 1_785_605_520_000L

        assertEquals(
            "Samstag, 1. August 2026 · 19:32",
            EntryDateFormatter.format(epochMillis, berlin),
        )
    }

    @Test
    fun `the injected zone decides the calendar day, not UTC`() {
        // 2026-01-01T23:30:00Z is already Jan 2nd in Berlin (CET, UTC+1).
        val epochMillis = 1_767_310_200_000L

        assertEquals(
            "Freitag, 2. Januar 2026 · 00:30",
            EntryDateFormatter.format(epochMillis, berlin),
        )
        assertEquals(
            "Donnerstag, 1. Januar 2026 · 23:30",
            EntryDateFormatter.format(epochMillis, ZoneId.of("UTC")),
        )
    }
}
