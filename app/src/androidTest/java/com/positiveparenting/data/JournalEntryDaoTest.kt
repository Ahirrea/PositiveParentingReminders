package com.positiveparenting.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JournalEntryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: JournalEntryDao

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        dao = db.journalEntryDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertPersistsAllFields() = runBlocking {
        val id = dao.insert(
            JournalEntry(
                createdAtEpochMillis = 1_722_470_400_000L,
                text = "Heute Abend ruhig geblieben.",
                mood = 4,
                prompt = "Wann warst du heute stolz auf dich?",
            )
        )

        val stored = dao.findById(id)
        assertNotNull(stored)
        assertEquals(1_722_470_400_000L, stored?.createdAtEpochMillis)
        assertEquals("Heute Abend ruhig geblieben.", stored?.text)
        assertEquals(4, stored?.mood)
        assertEquals("Wann warst du heute stolz auf dich?", stored?.prompt)
    }

    @Test
    fun moodAndPromptAreNullable() = runBlocking {
        val id = dao.insert(
            JournalEntry(
                createdAtEpochMillis = 1_722_470_400_000L,
                text = "Nur Text, keine Stimmung.",
            )
        )

        val stored = dao.findById(id)
        assertNotNull(stored)
        assertNull(stored?.mood)
        assertNull(stored?.prompt)
    }

    @Test
    fun entriesNewestFirstSortsByTimestampThenInsertOrder() = runBlocking {
        val oldest = dao.insert(JournalEntry(createdAtEpochMillis = 1L, text = "älter"))
        val tieFirst = dao.insert(JournalEntry(createdAtEpochMillis = 5L, text = "gleicher Moment, zuerst"))
        val tieSecond = dao.insert(JournalEntry(createdAtEpochMillis = 5L, text = "gleicher Moment, danach"))
        val newest = dao.insert(JournalEntry(createdAtEpochMillis = 9L, text = "neuester"))

        val entries = dao.entriesNewestFirst()

        assertEquals(listOf(newest, tieSecond, tieFirst, oldest), entries.map { it.id })
    }

    @Test
    fun entriesNewestFirstIsEmptyOnFreshDatabase() = runBlocking {
        assertTrue(dao.entriesNewestFirst().isEmpty())
    }

    @Test
    fun countSinceCountsOnlyEntriesAtOrAfterThreshold() = runBlocking {
        dao.insert(JournalEntry(createdAtEpochMillis = 10L, text = "gestern"))
        dao.insert(JournalEntry(createdAtEpochMillis = 100L, text = "heute früh"))
        dao.insert(JournalEntry(createdAtEpochMillis = 200L, text = "heute Abend"))

        assertEquals(2, dao.countSinceBlocking(100L))
        assertEquals(0, dao.countSinceBlocking(201L))
    }

    @Test
    fun countSinceIsZeroOnFreshDatabase() = runBlocking {
        assertEquals(0, dao.countSinceBlocking(0L))
    }

    @Test
    fun themeIsStoredAndNullableLikeMood() = runBlocking {
        val withTheme = dao.insert(
            JournalEntry(
                createdAtEpochMillis = 1L,
                text = "Zubettgehen war schwierig.",
                theme = "bedtime",
            )
        )
        val withoutTheme = dao.insert(
            JournalEntry(createdAtEpochMillis = 2L, text = "Kein Thema angegeben."),
        )

        assertEquals("bedtime", dao.findById(withTheme)?.theme)
        assertNull(dao.findById(withoutTheme)?.theme)
    }

    @Test
    fun updateThemeSetsChangesAndClearsTheTheme() = runBlocking {
        val id = dao.insert(
            JournalEntry(createdAtEpochMillis = 1L, text = "Erst ohne Thema."),
        )

        dao.updateTheme(id, "bedtime")
        assertEquals("bedtime", dao.findById(id)?.theme)

        dao.updateTheme(id, "siblings")
        assertEquals("siblings", dao.findById(id)?.theme)

        dao.updateTheme(id, null)
        assertNull(dao.findById(id)?.theme)
    }

    @Test
    fun updateThemeLeavesEveryOtherFieldUntouched() = runBlocking {
        val id = dao.insert(
            JournalEntry(
                createdAtEpochMillis = 1_722_470_400_000L,
                text = "Heute Abend ruhig geblieben.",
                mood = 4,
                prompt = "Wann warst du heute stolz auf dich?",
            )
        )

        dao.updateTheme(id, "anger")

        val stored = dao.findById(id)
        assertEquals(1_722_470_400_000L, stored?.createdAtEpochMillis)
        assertEquals("Heute Abend ruhig geblieben.", stored?.text)
        assertEquals(4, stored?.mood)
        assertEquals("Wann warst du heute stolz auf dich?", stored?.prompt)
    }

    @Test
    fun updateThemeOnUnknownIdChangesNothing() = runBlocking {
        val id = dao.insert(JournalEntry(createdAtEpochMillis = 1L, text = "bleibt"))

        dao.updateTheme(id + 999, "bedtime")

        assertNull(dao.findById(id)?.theme)
    }

    @Test
    fun multipleEntriesPerDayAreAllowed() = runBlocking {
        val first = dao.insert(JournalEntry(createdAtEpochMillis = 1L, text = "morgens"))
        val second = dao.insert(JournalEntry(createdAtEpochMillis = 2L, text = "abends"))

        assertTrue(first != second)
        assertNotNull(dao.findById(first))
        assertNotNull(dao.findById(second))
    }
}
