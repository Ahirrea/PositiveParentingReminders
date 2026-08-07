package com.positiveparenting.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The app's local database (ADR-004). Schema changes from version 2 on go
 * through migrations — never `fallbackToDestructiveMigration`: the entries
 * are the product, data loss is unacceptable.
 */
@Database(entities = [JournalEntry::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        const val DATABASE_NAME = "journal.db"

        /**
         * Version 2 adds the optional theme (A-5). Existing entries keep
         * everything they have and get `NULL` — they can be tagged later
         * from the overview.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_entries ADD COLUMN theme TEXT")
            }
        }

        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).addMigrations(MIGRATION_1_2)
                    .build().also { instance = it }
            }
    }
}
