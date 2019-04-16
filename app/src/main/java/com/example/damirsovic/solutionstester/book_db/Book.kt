package com.example.damirsovic.solutionstester.book_db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Book")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String? = null,
    val author: Author? = null,
    val publisher: Publisher? = null,
    val year: Int,
    val isbn: String? = null
)