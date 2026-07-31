package com.positiveparenting.journal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PromptProviderTest {

    private val prompts = listOf("prompt-0", "prompt-1", "prompt-2")

    @Test
    fun `index is stable for the same day`() {
        assertEquals(
            PromptProvider.promptIndex(epochDay = 12345, promptCount = prompts.size),
            PromptProvider.promptIndex(epochDay = 12345, promptCount = prompts.size)
        )
    }

    @Test
    fun `rotation is deterministic - index equals epochDay mod size`() {
        assertEquals(0, PromptProvider.promptIndex(epochDay = 0, promptCount = 3))
        assertEquals(1, PromptProvider.promptIndex(epochDay = 1, promptCount = 3))
        assertEquals(2, PromptProvider.promptIndex(epochDay = 2, promptCount = 3))
    }

    @Test
    fun `rotation wraps around after the last prompt`() {
        assertEquals(0, PromptProvider.promptIndex(epochDay = 3, promptCount = 3))
        assertEquals(1, PromptProvider.promptIndex(epochDay = 4, promptCount = 3))
    }

    @Test
    fun `consecutive days advance to the next prompt`() {
        val today = LocalDate.of(2026, 7, 31)
        val indexToday = PromptProvider.promptIndex(today.toEpochDay(), prompts.size)
        val indexTomorrow = PromptProvider.promptIndex(today.plusDays(1).toEpochDay(), prompts.size)
        assertEquals((indexToday + 1) % prompts.size, indexTomorrow)
    }

    @Test
    fun `index stays in range even for pre-1970 dates`() {
        val index = PromptProvider.promptIndex(epochDay = -1, promptCount = prompts.size)
        assertTrue(index in prompts.indices)
    }

    @Test
    fun `promptForDate returns the rotated prompt`() {
        val date = LocalDate.ofEpochDay(4)
        assertEquals("prompt-1", PromptProvider.promptForDate(date, prompts))
    }
}
