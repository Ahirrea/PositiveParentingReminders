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
}
