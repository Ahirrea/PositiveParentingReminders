package com.positiveparenting.journal

/**
 * Picks the daily prompt from the fixed local list (`daily_prompts` in
 * strings.xml): `index = epochDay mod size`. Pure and deterministic so the
 * rotation is JVM-testable; the prompt is fixed when the editor opens and
 * does not change across midnight within an open session.
 */
object PromptProvider {

    fun promptForDay(epochDay: Long, prompts: List<String>): String {
        require(prompts.isNotEmpty()) { "prompts must not be empty" }
        return prompts[Math.floorMod(epochDay, prompts.size.toLong()).toInt()]
    }
}
