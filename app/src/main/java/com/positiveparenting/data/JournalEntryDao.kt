package com.positiveparenting.data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface JournalEntryDao {
    // Read queries arrive with A-2 (journal overview).
    @Insert
    suspend fun insert(entry: JournalEntry): Long
}
