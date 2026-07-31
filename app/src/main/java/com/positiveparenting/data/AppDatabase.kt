package com.positiveparenting.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JournalEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        // Excluded from Android backup in res/xml/backup_rules.xml and
        // data_extraction_rules.xml — entries must never leave the device.
        const val DATABASE_NAME = "journal.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
    }
}
