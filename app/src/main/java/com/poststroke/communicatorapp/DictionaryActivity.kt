package com.poststroke.communicatorapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class DictionaryActivity : AppCompatActivity() {

    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var dictionaryRecyclerView: RecyclerView
    private lateinit var tts: TextToSpeech
    private lateinit var dictionaryDao: DictionaryDao
    private var allWordsList: MutableList<DictionaryWord> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        // Initialize TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
            }
        }

        // Initialize RecyclerView and Adapter
        dictionaryRecyclerView = findViewById(R.id.dictionaryRecyclerView)
        dictionaryRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Room database and DAO
        val db = DictionaryDatabase.getDatabase(this)
        dictionaryDao = db.dictionaryDao()

        // Load words from the database
        lifecycleScope.launch {
            loadWordsFromDatabase()
        }

        // Handle search queries
        val searchView: SearchView = findViewById(R.id.dictionarySearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterWords(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterWords(newText)
                return true
            }
        })

        // Handle category filtering
        findViewById<Button>(R.id.btnFeelings).setOnClickListener { filterWordsByCategory("Feelings") }
        findViewById<Button>(R.id.btnObjects).setOnClickListener { filterWordsByCategory("Objects") }
        findViewById<Button>(R.id.btnPeople).setOnClickListener { filterWordsByCategory("People") }
        findViewById<Button>(R.id.btnPlaces).setOnClickListener { filterWordsByCategory("Places") }
        findViewById<Button>(R.id.btnVerbs).setOnClickListener { filterWordsByCategory("Verbs") }

        // Back button logic
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()  // Go back to the previous screen
        }

        // Add New Word button logic
        findViewById<Button>(R.id.addWordButton).setOnClickListener {
            showAddWordDialog()
        }
    }

    // Load words from the Room database
    private suspend fun loadWordsFromDatabase() {
        withContext(Dispatchers.IO) {
            val words = dictionaryDao.getAllWords()
            withContext(Dispatchers.Main) {
                allWordsList.addAll(words)
                dictionaryAdapter = DictionaryAdapter(this@DictionaryActivity, allWordsList, tts, { wordItem ->
                    incrementWordFrequency(wordItem.word)
                }, false)
                dictionaryRecyclerView.adapter = dictionaryAdapter
            }
        }
    }

    // Show dialog to add a new word
    private fun showAddWordDialog() {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_word, null)
        val wordInput = dialogView.findViewById<EditText>(R.id.newWordInput)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
        val survivalCheckbox = dialogView.findViewById<CheckBox>(R.id.survivalCheckbox)

        // Set up the dropdown for category selection
        val categories = arrayOf("Feelings", "Objects", "People", "Places", "Verbs")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Create the dialog
        AlertDialog.Builder(this)
            .setTitle("Add New Word")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val newWord = wordInput.text.toString().trim()
                val selectedCategory = categorySpinner.selectedItem.toString()
                val isSurvivalWord = survivalCheckbox.isChecked

                if (newWord.isNotEmpty()) {
                    addWordToDictionary(newWord, selectedCategory, isSurvivalWord)
                } else {
                    Toast.makeText(this, "Please enter a word", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    // Add new word to the database and refresh the list
    private fun addWordToDictionary(word: String, category: String, isSurvivalWord: Boolean) {
        val newWord = DictionaryWord(word = word, category = category, isSurvivalWord = isSurvivalWord)
        lifecycleScope.launch(Dispatchers.IO) {
            dictionaryDao.insertWord(newWord)
            withContext(Dispatchers.Main) {
                allWordsList.add(newWord)
                dictionaryAdapter.updateWords(allWordsList)
                Toast.makeText(this@DictionaryActivity, "$word added to $category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to filter words based on the search query
    private fun filterWords(query: String?) {
        val filteredList = allWordsList.filter { it.word.contains(query ?: "", ignoreCase = true) }
        dictionaryAdapter.updateWords(filteredList)
    }

    // Function to filter words by category
    private fun filterWordsByCategory(category: String) {
        val filteredList = allWordsList.filter { it.category == category }
        dictionaryAdapter.updateWords(filteredList)
    }

    // Function to increment word frequency (optional, adjust logic based on your needs)
    private fun incrementWordFrequency(word: String) {
        // You can update word frequency logic here if needed, or handle it in the Room database
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
