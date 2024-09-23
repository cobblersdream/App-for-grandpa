package com.poststroke.communicatorapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary_words")
data class DictionaryWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-generated ID for primary key
    val word: String,  // Word in the dictionary
    val category: String,  // Category the word belongs to
    var isSurvivalWord: Boolean,  // Whether it's a survival word
    val imagePath: String?,  // Path to the image file (nullable)
    var frequency: Int = 0  // Frequency count (optional)
)
