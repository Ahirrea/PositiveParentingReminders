package com.positiveparenting.journal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeCatalogTest {

    /**
     * These keys are written into saved entries. Renaming one silently
     * orphans every entry that carries it, so the list is pinned here on
     * purpose: this test is meant to fail loudly at exactly that moment.
     */
    @Test
    fun keysAreStable() {
        assertEquals(
            listOf(
                "bedtime",
                "morning",
                "meals",
                "siblings",
                "anger",
                "screentime",
                "chores",
                "closeness",
            ),
            ThemeCatalog.KEYS,
        )
    }

    @Test
    fun keysAreUniqueAndNotBlank() {
        assertEquals(ThemeCatalog.KEYS.size, ThemeCatalog.KEYS.toSet().size)
        assertTrue(ThemeCatalog.KEYS.none { it.isBlank() })
    }

    @Test
    fun indexOfMatchesPositionInKeys() {
        ThemeCatalog.KEYS.forEachIndexed { index, key ->
            assertEquals(index, ThemeCatalog.indexOf(key))
        }
    }

    @Test
    fun indexOfIsNullForNullAndUnknownKeys() {
        assertNull(ThemeCatalog.indexOf(null))
        assertNull(ThemeCatalog.indexOf(""))
        assertNull(ThemeCatalog.indexOf("zubettgehen"))
        assertNull(ThemeCatalog.indexOf("Bedtime"))
    }

    @Test
    fun isKnownAcceptsOnlyCatalogKeys() {
        assertTrue(ThemeCatalog.KEYS.all { ThemeCatalog.isKnown(it) })
        assertFalse(ThemeCatalog.isKnown(null))
        assertFalse(ThemeCatalog.isKnown("holidays"))
    }
}
