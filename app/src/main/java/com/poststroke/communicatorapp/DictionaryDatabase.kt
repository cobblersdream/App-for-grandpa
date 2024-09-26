package com.poststroke.communicatorapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.migration.Migration

@Database(entities = [DictionaryWord::class], version = 2, exportSchema = false) // Update the version to 2
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        // Get the database instance with migration support and prepopulate data
        fun getDatabase(context: Context): DictionaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "dictionary_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate with default categories and words
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.dictionaryDao()
                                    // Preload words into the database with 'null' for imagePath
                                    dao.insertWord(DictionaryWord(word = "happy", category = "Feelings", isSurvivalWord = false, imagePath = null))
                                    dao.insertWord(DictionaryWord(word = "bed", category = "Objects", isSurvivalWord = false, imagePath = null))
                                    dao.insertWord(DictionaryWord(word = "cary", category = "People", isSurvivalWord = false, imagePath = null))
                                    dao.insertWord(DictionaryWord(word = "outside", category = "Places", isSurvivalWord = false, imagePath = null))
                                    dao.insertWord(DictionaryWord(word = "leave", category = "Verbs", isSurvivalWord = false, imagePath = null))
                                }
                            }
                        }
                    })
                    .addMigrations(MIGRATION_1_2)  // Include migration support
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Define the migration object for version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration: adding a new column 'imagePath' to the 'dictionary_words' table
                database.execSQL("ALTER TABLE dictionary_words ADD COLUMN imagePath TEXT")
            }
        }
    }
}
