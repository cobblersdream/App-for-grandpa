package com.poststroke.communicatorapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.CheckBox


class CategoryAdapter(private val categories: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryButton: Button = itemView.findViewById(R.id.categoryButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryButton.text = category
        holder.categoryButton.setOnClickListener {
            onClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}

