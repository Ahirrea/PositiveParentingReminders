package com.positiveparenting.profile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalProfileTest {

    @Test
    fun `fromInput trims and collapses inner whitespace`() {
        val profile = LocalProfile.fromInput("  Katharina ", " Emma \t Marie ")

        assertNotNull(profile)
        assertEquals("Katharina", profile?.parentName)
        assertEquals("Emma Marie", profile?.childName)
    }

    @Test
    fun `fromInput without parent name returns null`() {
        assertNull(LocalProfile.fromInput("", ""))
        assertNull(LocalProfile.fromInput("   ", "Emma"))
    }

    @Test
    fun `blank child name becomes null`() {
        assertNull(LocalProfile.fromInput("Katharina", "")?.childName)
        assertNull(LocalProfile.fromInput("Katharina", "   ")?.childName)
    }

    @Test
    fun `isValidParentName mirrors the fromInput requirement`() {
        assertTrue(LocalProfile.isValidParentName(" K "))
        assertFalse(LocalProfile.isValidParentName(""))
        assertFalse(LocalProfile.isValidParentName(" \t "))
    }

    @Test
    fun `normalizeName leaves clean input untouched`() {
        assertEquals("Katharina", LocalProfile.normalizeName("Katharina"))
    }
}
