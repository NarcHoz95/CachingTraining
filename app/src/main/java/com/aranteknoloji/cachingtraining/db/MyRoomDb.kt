package com.aranteknoloji.cachingtraining.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private var database: MyRoomDb? = null

fun myDatabase(context: Context): MyRoomDb {
    return database ?: Room.databaseBuilder(context, MyRoomDb::class.java, "my_db_name")
        .build().also { database = it }
}

@Database(entities = [MyTable::class], version = 1)
abstract class MyRoomDb : RoomDatabase() {
    abstract fun myDao(): MyDao
}