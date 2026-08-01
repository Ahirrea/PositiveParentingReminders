package com.positiveparenting.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single journal entry. Stored locally only (ADR-004: Room on device,
 * PRD non-goal: no entry ever leaves the device). Several entries per day
 * are allowed — there is deliberately no unique constraint on the date.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAtEpochMillis: Long,
    val text: String,
    /** Mood 1 (very hard) … 5 (very good); null = not given, it is optional. */
    val mood: Int? = null,
    /** The daily prompt shown while writing, so the review (A-7) knows what was answered. */
    val prompt: String? = null,
)
