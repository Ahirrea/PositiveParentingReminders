package com.positiveparenting.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JournalEntryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: JournalEntryDao

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.journalEntryDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    // Reading DAO queries arrive with A-2, so the assertions here go through a raw cursor.
    @Test
    fun insertPersistsAllFields() = runBlocking {
        val id = dao.insert(
            JournalEntry(
                createdAtEpochMillis = 1_722_400_000_000,
                text = "Heute ruhig geblieben beim Zubettgehen.",
                mood = 4,
                prompt = "Wann warst du heute stolz auf dich?"
            )
        )
        assertTrue(id > 0)

        database.openHelper.readableDatabase
            .query("SELECT id, createdAtEpochMillis, text, mood, prompt FROM journal_entries")
            .use { cursor ->
                assertEquals(1, cursor.count)
                cursor.moveToFirst()
                assertEquals(id, cursor.getLong(0))
                assertEquals(1_722_400_000_000, cursor.getLong(1))
                assertEquals("Heute ruhig geblieben beim Zubettgehen.", cursor.getString(2))
                assertEquals(4, cursor.getInt(3))
                assertEquals("Wann warst du heute stolz auf dich?", cursor.getString(4))
            }
    }

    @Test
    fun moodAndPromptAreNullable() = runBlocking {
        dao.insert(
            JournalEntry(
                createdAtEpochMillis = 1_722_400_000_000,
                text = "Eintrag ohne Stimmung und ohne Impuls."
            )
        )

        database.openHelper.readableDatabase
            .query("SELECT mood, prompt FROM journal_entries")
            .use { cursor ->
                assertEquals(1, cursor.count)
                cursor.moveToFirst()
                assertTrue(cursor.isNull(0))
                assertTrue(cursor.isNull(1))
            }
    }
}
