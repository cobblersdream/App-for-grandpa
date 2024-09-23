package com.poststroke.communicatorapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SurvivalWordsActivity : AppCompatActivity() {

    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var survivalWordsRecyclerView: RecyclerView
    private lateinit var tts: TextToSpeech
    private lateinit var dictionaryDao: DictionaryDao

    private var wordsList: MutableList<DictionaryWord> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_words)

        // Initialize TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
            }
        }

        // Initialize RecyclerView and Adapter
        survivalWordsRecyclerView = findViewById(R.id.survivalWordsRecyclerView)
        survivalWordsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Room database and DAO
        val db = DictionaryDatabase.getDatabase(this)
        dictionaryDao = db.dictionaryDao()

        // Load and filter survival words
        lifecycleScope.launch {
            loadAndDisplaySurvivalWords()
        }

        // Back button logic
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()  // Go back to the previous screen
        }
    }

    // Function to load and display survival words from the Room database
    private suspend fun loadAndDisplaySurvivalWords() {
        withContext(Dispatchers.IO) {
            // Retrieve all words that are marked as survival words from the database
            val survivalWords = dictionaryDao.getSurvivalWords()  // This fetches only words where isSurvivalWord == true

            // Sort the survival words by some criteria, if needed (e.g., frequency)
            val sortedSurvivalWords = survivalWords.sortedByDescending { it.frequency }

            withContext(Dispatchers.Main) {
                // Set the adapter with the sorted list of survival words
                wordsList.addAll(sortedSurvivalWords)
                dictionaryAdapter = DictionaryAdapter(this@SurvivalWordsActivity, wordsList, tts, { wordItem ->
                    incrementWordFrequency(wordItem.word)
                }, isSurvivalWordsActivity = true)
                survivalWordsRecyclerView.adapter = dictionaryAdapter
            }
        }
    }

    // Function to increment word frequency in the Room database
    private fun incrementWordFrequency(word: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Get the current word from the database
            val wordItem = dictionaryDao.getWordByWord(word)
            if (wordItem != null) {
                // Increment the frequency
                wordItem.frequency += 1
                // Update the word back into the database
                dictionaryDao.updateWord(wordItem)
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
