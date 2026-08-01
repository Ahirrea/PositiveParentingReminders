package com.positiveparenting.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The app's local database (ADR-004). Schema changes from version 2 on go
 * through migrations — never `fallbackToDestructiveMigration`: the entries
 * are the product, data loss is unacceptable.
 */
@Database(entities = [JournalEntry::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        const val DATABASE_NAME = "journal.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
    }
}
