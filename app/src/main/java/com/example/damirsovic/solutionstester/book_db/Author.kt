package com.example.damirsovic.solutionstester.book_db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Author")
data class Author(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String? = null,
    val middleName: String? = null,
    val lastname: String? = null
)