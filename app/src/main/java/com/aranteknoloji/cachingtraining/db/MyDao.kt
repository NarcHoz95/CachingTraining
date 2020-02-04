package com.aranteknoloji.cachingtraining.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MyDao {

    @Query("select * from my_table where id = 0")
    fun myEntities(): Flowable<List<MyTable>>

    @Query("select * from my_table where id = 0")
    fun singleEntity(): Single<MyTable>

    @Query("select * from my_table where id = 0")
    fun myTable(): MyTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(myTable: MyTable): Completable

    @Delete
    fun delete(myTable: MyTable)
}