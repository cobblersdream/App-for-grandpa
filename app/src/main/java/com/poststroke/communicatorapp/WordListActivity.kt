package com.poststroke.communicatorapp

import android.os.Bundle
import android.view.Gravity
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordListActivity : AppCompatActivity() {

    private lateinit var wordGridLayout: GridLayout
    private lateinit var addWordButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private lateinit var dictionaryDao: DictionaryDao
    private var wordList: List<DictionaryWord> = listOf()
    private var selectedCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        // Initialize views
        wordGridLayout = findViewById(R.id.wordGridLayout)
        addWordButton = findViewById(R.id.addWordButton)
        backButton = findViewById(R.id.backButton)

        // Retrieve category name from the Intent
        selectedCategory = intent.getStringExtra("category") ?: ""

        // Initialize DAO
        dictionaryDao = DictionaryDatabase.getDatabase(this).dictionaryDao()

        // Load words for the selected category
        loadWordsForCategory(selectedCategory)

        // Add word button action
        addWordButton.setOnClickListener {
            // TODO: Launch a dialog to add a new word to this category
        }

        // Back button action
        backButton.setOnClickListener {
            finish()  // Go back to the previous screen
        }
    }

    // Load words from the database for the selected category
    private fun loadWordsForCategory(category: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Fetch words from the database
            wordList = dictionaryDao.getWordsByCategory(category)
            withContext(Dispatchers.Main) {
                displayWords(wordList)
            }
        }
    }

    // Display words as tiles in the grid layout
    private fun displayWords(words: List<DictionaryWord>) {
        wordGridLayout.removeAllViews()

        words.forEach { word ->
            // Create a container layout for each word tile
            val wordTile = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }
            }

            // Add a button for the word
            val wordButton = MaterialButton(this).apply {
                text = word.word
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                // You can add functionality here for word click
                setOnClickListener {
                    // TODO: Add action when the word is clicked (e.g., speak the word)
                }
            }
            wordTile.addView(wordButton)

            // Add a checkbox for marking the word as a survival word
            val survivalCheckBox = CheckBox(this).apply {
                text = getString(R.string.survival_word)
                isChecked = word.isSurvivalWord
                setOnCheckedChangeListener { _, isChecked ->
                    word.isSurvivalWord = isChecked
                    lifecycleScope.launch(Dispatchers.IO) {
                        dictionaryDao.updateWord(word)
                    }
                }
            }
            wordTile.addView(survivalCheckBox)

            // Add the word tile to the grid layout
            wordGridLayout.addView(wordTile)
        }
    }
}
