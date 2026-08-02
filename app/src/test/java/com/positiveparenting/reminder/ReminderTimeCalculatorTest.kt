package com.positiveparenting.reminder

import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderTimeCalculatorTest {

    private val reminderTime = LocalTime.of(20, 0)

    @Test
    fun `before the reminder time the trigger is today`() {
        val now = LocalDateTime.of(2026, 8, 2, 9, 30)

        assertEquals(
            LocalDateTime.of(2026, 8, 2, 20, 0),
            ReminderTimeCalculator.nextTrigger(now, reminderTime),
        )
    }

    @Test
    fun `after the reminder time the trigger is tomorrow`() {
        val now = LocalDateTime.of(2026, 8, 2, 21, 15)

        assertEquals(
            LocalDateTime.of(2026, 8, 3, 20, 0),
            ReminderTimeCalculator.nextTrigger(now, reminderTime),
        )
    }

    @Test
    fun `exactly at the reminder time the trigger is tomorrow`() {
        // Firing "now" would be a surprise ping the moment the clock hits
        // 20:00:00.000 — the boundary belongs to the next day.
        val now = LocalDateTime.of(2026, 8, 2, 20, 0)

        assertEquals(
            LocalDateTime.of(2026, 8, 3, 20, 0),
            ReminderTimeCalculator.nextTrigger(now, reminderTime),
        )
    }

    @Test
    fun `one second before the reminder time still triggers today`() {
        val now = LocalDateTime.of(2026, 8, 2, 19, 59, 59)

        assertEquals(
            LocalDateTime.of(2026, 8, 2, 20, 0),
            ReminderTimeCalculator.nextTrigger(now, reminderTime),
        )
    }

    @Test
    fun `rollover crosses month and year boundaries`() {
        val newYearsEve = LocalDateTime.of(2026, 12, 31, 22, 0)

        assertEquals(
            LocalDateTime.of(2027, 1, 1, 20, 0),
            ReminderTimeCalculator.nextTrigger(newYearsEve, reminderTime),
        )
    }
}
