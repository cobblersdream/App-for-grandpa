package com.poststroke.communicatorapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DictionaryWord::class], version = 1, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        // Get the database instance
        fun getDatabase(context: Context): DictionaryDatabase {
            // Return the existing database or create a new one, thread-safe
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "dictionary_database"  // Database name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
