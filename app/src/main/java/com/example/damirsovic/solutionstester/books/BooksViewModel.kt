package com.example.damirsovic.solutionstester.books

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.damirsovic.solutionstester.books.book_db.BooksRoomDatabase
import com.example.damirsovic.solutionstester.books.book_db.dao.BookDao

class BooksViewModel(application: Application) : AndroidViewModel(application) {

    val booksRoomDatabase = BooksRoomDatabase.getInstance(application)
    val bookDao: BookDao by lazy {
        booksRoomDatabase.bookDao()
    }
}