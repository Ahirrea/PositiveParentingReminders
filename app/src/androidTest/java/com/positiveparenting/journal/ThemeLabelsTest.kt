package com.positiveparenting.journal

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.positiveparenting.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Keys live in Kotlin, labels in resources, and they are paired by index —
 * the one fragile spot of the theme catalog (A-5). This test is the guard
 * rail: add a label without a key (or the other way round) and it fails.
 */
@RunWith(AndroidJUnit4::class)
class ThemeLabelsTest {

    private val labels: Array<String>
        get() = ApplicationProvider.getApplicationContext<android.content.Context>()
            .resources
            .getStringArray(R.array.theme_labels)

    @Test
    fun everyKeyHasALabel() {
        assertEquals(ThemeCatalog.KEYS.size, labels.size)
    }

    @Test
    fun noLabelIsBlank() {
        assertTrue(labels.none { it.isBlank() })
    }
}
