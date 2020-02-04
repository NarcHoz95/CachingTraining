package com.aranteknoloji.cachingtraining.sources

import android.content.Context
import android.util.Log
import com.aranteknoloji.cachingtraining.db.MyTable
import com.aranteknoloji.cachingtraining.db.myDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class NetworkSource {

    private var counter = 0

    private val maps = listOf("First Item", "Second Item", "Third Item",
        "Forth Item", "Fifth Item", "Sixth Item", "Seventh Item", "Eighth Item", "Nine Item")

    fun networkSourceObservable(context: Context): Observable<MyTable> =
        Observable.create<String> {
            it.onNext(maps[counter % maps.size].also { counter++ })
            Log.i("CachingDen", "model has been emitted by network source")
            it.onComplete()
            Log.i("CachingDen", "network source has been completed")
        }
            .delay(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val item = MyTable(id = 0, nameId = it, time = System.currentTimeMillis())
                Log.i("CachingDen", "network source saved entity to DB -> name=${item.nameId} with $item")
                myDatabase(context).myDao().insert(item)
            }
            .toObservable<MyTable>()
            .flatMapSingle { myDatabase(context).myDao().singleEntity() }
            .doOnNext {
                Log.i("CachingDen", "network source has emitted item from db")
            }

    fun networkSourceFlowable(context: Context) =
        Observable.create<String> {
            it.onNext(maps[counter % maps.size].also { counter++ })
            Log.i("CachingDen", "model has been emitted by network source")
//            it.onComplete()
//            Log.i("CachingDen", "network source has been completed")
        }
            .delay(5, TimeUnit.SECONDS)
//            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val item = MyTable(id = 0, nameId = it, time = System.currentTimeMillis())
                Log.i("CachingDen", "network source saved entity to DB -> name=${item.nameId} with $item")
                myDatabase(context).myDao().insert(item)
            }
            .doOnComplete {
                Log.i("CachingDen", "network source has been completed")
            }
//            .to { myDatabase(context).myDao().singleEntity() }
//            .flatMapSingle { myDatabase(context).myDao().singleEntity() }
//            .doOnNext {
//                Log.i("CachingDen", "network source has emitted item from db")
//            }
}