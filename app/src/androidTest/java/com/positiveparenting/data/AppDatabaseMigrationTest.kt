package com.positiveparenting.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The first real schema migration of the project (A-5, ADR-004: migrations
 * instead of data loss). What is being proven here is not that the column
 * appears — it is that an existing journal survives the update untouched.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    @Test
    fun migrate1To2KeepsEntriesAndLeavesThemeNull() {
        helper.createDatabase(TEST_DB, 1).use { db ->
            db.execSQL(
                """
                INSERT INTO journal_entries (createdAtEpochMillis, text, mood, prompt)
                VALUES (1722470400000, 'Heute Abend ruhig geblieben.', 4, 'Wann warst du heute stolz auf dich?')
                """.trimIndent()
            )
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            AppDatabase.MIGRATION_1_2,
        )

        migrated.query(
            "SELECT createdAtEpochMillis, text, mood, prompt, theme FROM journal_entries"
        ).use { cursor ->
            assertTrue("Der Eintrag muss die Migration überleben", cursor.moveToFirst())
            assertEquals(1, cursor.count)
            assertEquals(1_722_470_400_000L, cursor.getLong(0))
            assertEquals("Heute Abend ruhig geblieben.", cursor.getString(1))
            assertEquals(4, cursor.getInt(2))
            assertEquals("Wann warst du heute stolz auf dich?", cursor.getString(3))
            assertTrue("Bestandseinträge haben noch kein Thema", cursor.isNull(4))
        }
        migrated.close()
    }

    @Test
    fun migratedDatabaseAcceptsAThemeAfterwards() {
        helper.createDatabase(TEST_DB, 1).use { db ->
            db.execSQL(
                "INSERT INTO journal_entries (createdAtEpochMillis, text) VALUES (1, 'ohne Thema')"
            )
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            AppDatabase.MIGRATION_1_2,
        )
        migrated.execSQL("UPDATE journal_entries SET theme = 'bedtime' WHERE createdAtEpochMillis = 1")

        migrated.query("SELECT theme FROM journal_entries").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("bedtime", cursor.getString(0))
        }
        migrated.close()
    }

    private companion object {
        const val TEST_DB = "migration-test.db"
    }
}
