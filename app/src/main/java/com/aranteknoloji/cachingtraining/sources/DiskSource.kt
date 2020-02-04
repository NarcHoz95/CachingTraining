package com.aranteknoloji.cachingtraining.sources

import android.content.Context
import android.util.Log
import androidx.room.EmptyResultSetException
import com.aranteknoloji.cachingtraining.db.MyTable
import com.aranteknoloji.cachingtraining.db.myDatabase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class DiskSource {

    fun getDiskSourceObservable(context: Context): Observable<MyTable> =
        myDatabase(context).myDao().singleEntity()
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext {
                if (it is EmptyResultSetException) Single.just(MyTable(nameId = "", time = 0))
                else Single.error(it)
            }
            .toObservable()
            .doOnNext {
                Log.i("CachingDen", "item emitted by disk source")
            }
            .filter {
                val timeDiff = System.currentTimeMillis() - it.time
                Log.i("CachingDen", "item in disk time diff = $timeDiff")
                timeDiff < 15000
            }
            .doOnNext {
                Log.i("CachingDen", "disk source has been completed")
            }

    fun getDbFlowable(context: Context) =
        myDatabase(context).myDao().myEntities()
//            .last(MyTable(nameId = "", time = 0))
//            .subscribeOn(Schedulers.io())
//            .toObservable()
//            .defaultIfEmpty(
//                let {
//                    Log.i("CachingDen", "disk source has NO ITEM")
//                    return@let MyTable(nameId = "", time = 0)
//                }
//                if (it is EmptyResultSetException) Single.just(MyTable(nameId = "", time = 0))
//                else Single.error(it)
//            )

    fun roomCachedItems(context: Context) =
        myDatabase(context).myDao().myEntities()
//            .distinctUntilChanged()
//            .startWith(MyTable(0,"Initial", 0))
}