package com.example.damirsovic.solutionstester.books

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.damirsovic.solutionstester.R

class BooksViewHolder<T, V : ViewDataBinding>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val titleItemView: TextView
    val authorItemView: TextView
    val publisherItemView: TextView
    val yearItemView: TextView
    val isbnItemView: TextView

    init {
        titleItemView = itemView.findViewById(R.id.txtTitle)
        authorItemView = itemView.findViewById(R.id.txtTitle)
        publisherItemView = itemView.findViewById(R.id.txtTitle)
        yearItemView = itemView.findViewById(R.id.txtTitle)
        isbnItemView = itemView.findViewById(R.id.txtTitle)
    }
}