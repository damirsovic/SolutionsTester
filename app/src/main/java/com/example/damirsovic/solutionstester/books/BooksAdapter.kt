package com.example.damirsovic.solutionstester.books

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.damirsovic.solutionstester.R
import com.example.damirsovic.solutionstester.books.book_db.entities.Book
import io.reactivex.Flowable

class BooksAdapter internal constructor(context: Context) : RecyclerView.Adapter<BooksViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var bookList: Flowable<Book>? = null

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val itemView = layoutInflater.inflate(R.layout.book_item, parent, false)
        return BooksViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        if (bookList != null) {
            val (book) = bookList!!.skip(position).take(1)
            holder.titleItemView.text = bookList
        } else {
            holder.titleItemView.text = "No Word"
        }
    }

    override fun getItemCount(): Int = if (bookList != null) bookList!!.size else 0
}
}