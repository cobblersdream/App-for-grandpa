package com.poststroke.communicatorapp

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.poststroke.communicatorapp.R
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Adapter class for displaying dictionary words in a RecyclerView
class DictionaryAdapter(
    private val context: Context,
    private var words: List<DictionaryWord>,
    private val tts: TextToSpeech,
    private val dictionaryDao: DictionaryDao,  // Pass dictionaryDao
    private val coroutineScope: CoroutineScope,  // Pass the lifecycleScope or any CoroutineScope
    private val onWordClick: (DictionaryWord) -> Unit,
    private val isSurvivalWordsActivity: Boolean // Added as flag to distinguish
) : RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    // ViewHolder class to hold references to each item view in the RecyclerView
    inner class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        val speakButton: Button = itemView.findViewById(R.id.speakButton)
        val addToSurvivalButton: Button = itemView.findViewById(R.id.addToSurvivalButton) // Button to mark word as survival
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.dictionary_item, parent, false)
        return DictionaryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        val wordItem = words[position]
        holder.wordTextView.text = wordItem.word

        // Handle Text-to-Speech for each word
        holder.speakButton.setOnClickListener {
            onWordClick(wordItem)
            tts.speak(wordItem.word, TextToSpeech.QUEUE_FLUSH, null, "")
        }

        // Handle adding word to survival list
        if (isSurvivalWordsActivity) {
            // If it's the survival words activity, hide the "add to survival" button
            holder.addToSurvivalButton.visibility = View.GONE
        } else {
            // If it's dictionary activity, show the button to mark as survival word
            holder.addToSurvivalButton.visibility = View.VISIBLE
            holder.addToSurvivalButton.setOnClickListener {
                addWordToSurvivalWords(wordItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    // Update the list of words when filtered or searched
    fun updateWords(newWords: List<DictionaryWord>) {
        words = newWords
        notifyDataSetChanged()
    }

    // Function to add a word to the survival words list
    private fun addWordToSurvivalWords(wordItem: DictionaryWord) {
        if (wordItem.isSurvivalWord) {
            Toast.makeText(context, "${wordItem.word} is already a survival word.", Toast.LENGTH_SHORT).show()
        } else {
            val updatedWord = wordItem.copy(isSurvivalWord = true) // Create a new copy with the updated survival status

            // Use the coroutineScope passed from the activity
            coroutineScope.launch(Dispatchers.IO) {
                dictionaryDao.updateWord(updatedWord)  // Update the word in the database

                withContext(Dispatchers.Main) {
                    // Update the list and refresh the adapter with the new word
                    updateWords(words.map { if (it.word == wordItem.word) updatedWord else it })
                    Toast.makeText(context, "${wordItem.word} added to survival words.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
