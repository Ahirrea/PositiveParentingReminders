package com.positiveparenting.reminder

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Computes the next daily-reminder trigger: today at [reminderTime] if that
 * is still ahead, otherwise tomorrow. Pure and deterministic so the boundary
 * cases (just before, exactly at, just after, day rollovers) are JVM-testable.
 */
object ReminderTimeCalculator {

    fun nextTrigger(now: LocalDateTime, reminderTime: LocalTime): LocalDateTime {
        val todayTrigger = now.toLocalDate().atTime(reminderTime)
        return if (now.isBefore(todayTrigger)) todayTrigger else todayTrigger.plusDays(1)
    }
}
