package com.poststroke.communicatorapp

import android.content.Context
import androidx.preference.PreferenceManager
import com.poststroke.communicatorapp.R


object WordLogger {

    private const val WORD_LOG_KEY = "word_log"
    private const val WORD_FREQUENCY_KEY_PREFIX = "word_frequency_"

    // Function to log a word's usage
    fun logWord(context: Context, word: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val wordLog = prefs.getStringSet(WORD_LOG_KEY, mutableSetOf()) ?: mutableSetOf()

        // Add the new word to the log
        wordLog.add(word)
        prefs.edit().putStringSet(WORD_LOG_KEY, wordLog).apply()

        // Update the word frequency
        updateWordFrequency(context, word)
    }

    // Function to update the frequency of a word
    private fun updateWordFrequency(context: Context, word: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val frequencyKey = "$WORD_FREQUENCY_KEY_PREFIX$word"
        val currentFrequency = prefs.getInt(frequencyKey, 0)

        // Increment the word's frequency
        prefs.edit().putInt(frequencyKey, currentFrequency + 1).apply()
    }

    // Function to get the frequency of a word
    fun getWordFrequency(context: Context, word: String): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt("$WORD_FREQUENCY_KEY_PREFIX$word", 0)
    }

    // Function to get the full word log
    fun getWordLog(context: Context): Set<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getStringSet(WORD_LOG_KEY, mutableSetOf()) ?: mutableSetOf()
    }
}
