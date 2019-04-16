package com.example.damirsovic.solutionstester.book_db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Publisher")
data class Publisher(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String? = null
)