package com.aranteknoloji.cachingtraining.repositories

import android.content.Context
import android.util.Log
import com.aranteknoloji.cachingtraining.db.MyTable
import com.aranteknoloji.cachingtraining.db.myDatabase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RoomRepository(
    private val context: Context,
    private val networkProvider: NetworkProvider = NetworkProvider()
) {

    private var disposable: Disposable? = null

//    fun getObservableDataFromDb(): Observable<MyTable> {
//        return myDatabase(context).myDao().myEntities()
//            .subscribeOn(Schedulers.io())
//            .concatMap {
//                val timeDiff = System.currentTimeMillis() - it.time
//                Log.e("RoomRepo", "time diff = $timeDiff")
//                if (timeDiff < 15000) {
//                    makeNetworkCall()
//                }
//                Observable.just(it)
//            }
//            .distinctUntilChanged()
//    }

    private fun makeNetworkCall() {
        disposable = networkProvider.getSingleObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                myDatabase(context).myDao()
                    .insert(MyTable(nameId = it, time = System.currentTimeMillis()))
            }
    }

    fun disposaCallsIfAny() {
        disposable?.dispose()
    }
}