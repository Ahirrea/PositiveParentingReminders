package com.positiveparenting.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface JournalEntryDao {

    @Insert
    suspend fun insert(entry: JournalEntry): Long

    /** Single-row lookup for the DAO test; list queries arrive with A-2. */
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun findById(id: Long): JournalEntry?
}
