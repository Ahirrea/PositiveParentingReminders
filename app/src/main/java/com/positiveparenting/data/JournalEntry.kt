package com.positiveparenting.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAtEpochMillis: Long,
    val text: String,
    /** Mood on a 1 (very hard) to 5 (very good) scale; null = not given. */
    val mood: Int? = null,
    /** The daily prompt shown while writing, so later reviews know what was answered. */
    val prompt: String? = null,
)
