package com.poststroke.communicatorapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.poststroke.communicatorapp.DictionaryWord


@Dao
interface DictionaryDao {

    // Insert a new word into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: DictionaryWord)

    // Insert multiple words into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<DictionaryWord>)

    /* Query to get all words from the dictionary */
    @Query("SELECT * FROM dictionary_words")
    suspend fun getAllWords(): List<DictionaryWord>

    // Query to get all survival words where 'isSurvivalWord' is true
    @Query("SELECT * FROM dictionary_words WHERE isSurvivalWord = 1")
    suspend fun getSurvivalWords(): List<DictionaryWord>

    // Query to get a specific word by the word value
    @Query("SELECT * FROM dictionary_words WHERE word = :word LIMIT 1")
    suspend fun getWordByWord(word: String): DictionaryWord?

    // Update an existing word in the dictionary
    @Update
    suspend fun updateWord(word: DictionaryWord): Int

    // Delete a word from the dictionary
    @Delete
    suspend fun deleteWord(word: DictionaryWord): Int
}
