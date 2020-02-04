package com.aranteknoloji.cachingtraining.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_table")
class MyTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val nameId: String,
    val time: Long
)