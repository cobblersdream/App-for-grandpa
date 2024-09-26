package com.poststroke.communicatorapp

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQL query to migrate from version 1 to version 2
        database.execSQL("ALTER TABLE dictionary_words ADD COLUMN imagePath TEXT")
    }
}
