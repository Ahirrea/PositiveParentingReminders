package com.positiveparenting.journal

import java.time.LocalDate

/**
 * Picks the daily prompt from a fixed list, rotating by date:
 * `index = epochDay mod size`. Pure and deterministic so it is JVM-testable.
 */
object PromptProvider {

    fun promptIndex(epochDay: Long, promptCount: Int): Int {
        require(promptCount > 0) { "promptCount must be positive" }
        return ((epochDay % promptCount + promptCount) % promptCount).toInt()
    }

    fun promptForDate(date: LocalDate, prompts: List<String>): String =
        prompts[promptIndex(date.toEpochDay(), prompts.size)]
}
