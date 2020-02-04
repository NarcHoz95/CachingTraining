package com.aranteknoloji.cachingtraining.sources

import android.content.Context
import android.util.Log
import com.aranteknoloji.cachingtraining.db.MyTable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.UnicastSubject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class DataSource {

    private val networkSource by lazy { NetworkSource() }
    private val diskSource by lazy { DiskSource() }

    fun getCachingObservable(context: Context) =
        Observable.concat(
            diskSource.getDiskSourceObservable(context),
            networkSource.networkSourceObservable(context)
        ).firstElement()
            .doOnSuccess {
                Log.w("CachingDen", "lastItem it goes $it")
            }

    fun getCachedFlowable(context: Context) =
        diskSource.getDbFlowable(context)
//            .doOnSuccess {
//                val timeDiff = System.currentTimeMillis() - it.time
//                Log.i("CachingDen", "item in disk time diff = $timeDiff")
//                if (timeDiff > 15000) {
//                    networkSource.networkSourceFlowable(context)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(Schedulers.io())
//                        .subscribe()
//                }
//            }
            .subscribeOn(Schedulers.io())
//            .flatMap {
//                val timeDiff = System.currentTimeMillis() - it.time
//                Log.i("CachingDen", "item in disk time diff = $timeDiff")
//                if (timeDiff > 15000) {
//                    networkSource.networkSourceFlowable(context)
//                }
//                else Single.just(it)
//            }

    fun roomCachedItems(context: Context): Flowable<List<MyTable>> =
        diskSource.roomCachedItems(context)
            .doOnNext {
                val timeDiff = System.currentTimeMillis() - (it.firstOrNull()?.time ?: 0)
                Log.i("CachingDen", "item in disk time diff = $timeDiff")
                if (timeDiff > 15000) {
                    Log.w("CachingDen", "item in disk time diff bigger than 15s")
                    makeNetworkCall(context)
                }
            }

    fun makeNetworkCall(context: Context) {
        networkSource.networkSourceFlowable(context)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
    }
}