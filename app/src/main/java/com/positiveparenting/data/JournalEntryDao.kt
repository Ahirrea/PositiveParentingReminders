package com.positiveparenting.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface JournalEntryDao {

    @Insert
    suspend fun insert(entry: JournalEntry): Long

    /** Single-row lookup for the DAO test. */
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun findById(id: Long): JournalEntry?

    /**
     * All entries for the overview (A-2), newest first. Several entries per
     * day are allowed, so ties on the timestamp fall back to the insert order.
     */
    @Query("SELECT * FROM journal_entries ORDER BY createdAtEpochMillis DESC, id DESC")
    suspend fun entriesNewestFirst(): List<JournalEntry>

    /**
     * Number of entries created at or after [epochMillis] — the reminder's
     * skip rule (A-3: no notification when today already has an entry).
     * Non-suspend on purpose: the receiver calls it from a plain worker
     * thread without a coroutine scope.
     */
    @Query("SELECT COUNT(*) FROM journal_entries WHERE createdAtEpochMillis >= :epochMillis")
    fun countSinceBlocking(epochMillis: Long): Int

    /**
     * Adds, changes or removes (null) the theme of an existing entry (A-5).
     * Deliberately a targeted query instead of `@Update`: the overview may
     * only touch the theme — text, mood, prompt and timestamp of a written
     * entry stay immutable.
     */
    @Query("UPDATE journal_entries SET theme = :theme WHERE id = :id")
    suspend fun updateTheme(id: Long, theme: String?)
}
