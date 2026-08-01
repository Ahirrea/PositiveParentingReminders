package com.positiveparenting.journal

import org.junit.Assert.assertEquals
import org.junit.Test

class PromptProviderTest {

    private val prompts = listOf("A", "B", "C")

    @Test
    fun `same day always yields the same prompt`() {
        val first = PromptProvider.promptForDay(20_000L, prompts)
        val second = PromptProvider.promptForDay(20_000L, prompts)

        assertEquals(first, second)
    }

    @Test
    fun `consecutive days rotate through the list`() {
        assertEquals("A", PromptProvider.promptForDay(0L, prompts))
        assertEquals("B", PromptProvider.promptForDay(1L, prompts))
        assertEquals("C", PromptProvider.promptForDay(2L, prompts))
    }

    @Test
    fun `rotation wraps around after the last prompt`() {
        assertEquals("A", PromptProvider.promptForDay(3L, prompts))
        assertEquals("B", PromptProvider.promptForDay(prompts.size * 100L + 1, prompts))
    }

    @Test
    fun `negative epoch days stay in bounds`() {
        // floorMod, not %: days before 1970 must still map into the list.
        assertEquals("C", PromptProvider.promptForDay(-1L, prompts))
        assertEquals("A", PromptProvider.promptForDay(-3L, prompts))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty prompt list is rejected`() {
        PromptProvider.promptForDay(0L, emptyList())
    }
}
