package com.poststroke.communicatorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import android.widget.CheckBox


class WordAdapter(private val words: List<DictionaryWord>, private val onSurvivalWordChange: (DictionaryWord, Boolean) -> Unit) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordText: TextView = itemView.findViewById(R.id.wordText)
        val survivalWordCheckBox: CheckBox = itemView.findViewById(R.id.survivalWordCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.wordText.text = word.word
        holder.survivalWordCheckBox.isChecked = word.isSurvivalWord

        holder.survivalWordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onSurvivalWordChange(word, isChecked)
        }
    }

    override fun getItemCount(): Int = words.size
}
