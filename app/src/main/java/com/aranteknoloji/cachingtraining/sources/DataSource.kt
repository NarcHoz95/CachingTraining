package com.aranteknoloji.cachingtraining.sources

import android.content.Context
import android.util.Log
import com.aranteknoloji.cachingtraining.db.MyTable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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
//                val timeDiff = System.currentTimeMillis() - (it.firstOrNull()?.time ?: 0)
//                Log.i("CachingDen", "item in disk time diff = $timeDiff")
//                if (timeDiff > 15000) {
//                    Log.w("CachingDen", "item in disk time diff bigger than 15s")
//                    makeNetworkCall(context)
//                }
//                it.firstOrNull()?.let { item -> setInMap(context, item) }
                it.firstOrNull()?.let { item -> setInMap2(context, item) }
            }

    fun makeNetworkCall(context: Context, action: (() -> Int)? = null) {
        networkSource.networkSourceFlowable(context)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnComplete { Log.i("CachingDen", "action remove id from map with id = ${action?.invoke()}") }
            .subscribe()
    }

    private fun setInMap(context: Context, myTable: MyTable) {
        Log.e("CachingDen", "ConcurrentMap has item inside with id = ${myTable.id}")
        map.putIfAbsent(myTable.id!!, let {
            val timeDiff = System.currentTimeMillis() - myTable.time
            Log.i("CachingDen", "item in disk time diff = $timeDiff")
            if (timeDiff > 15000) {
                Log.w("CachingDen", "item in disk time diff bigger than 15s")
                makeNetworkCall(context) { map.remove(myTable.id);5 }
            }
            true
        })
    }

    private fun setInMap2(context: Context, myTable: MyTable) {
//        Log.e("CachingDen", "ConcurrentMap has item inside with id = ${myTable.id}")
        synchronized(map2) {

            val timeDiff = System.currentTimeMillis() - myTable.time
            Log.i("CachingDen", "item in disk time diff = $timeDiff")
            if (timeDiff > 15000) {
                Log.w("CachingDen", "item in disk time diff bigger than 15s")
                map2[myTable.id]?.let {
                    Log.e("CachingDen", "Network call is in progress with id = ${myTable.id}")
                } ?: let {
                    map2.put(myTable.id!!, let {
                        Log.i("CachingDen", "Network call is added into progress with id = ${myTable.id}")
                        makeNetworkCall(context) { myTable.id.also { map2.remove(it) } }
                        true
                    })
                }
            }



//            map2[myTable.id]?.let {
//                Log.e("CachingDen", "Network call is in progress with id = ${myTable.id}")
//            } ?: let {
//                map2.put(myTable.id!!, let {
//                    val timeDiff = System.currentTimeMillis() - myTable.time
//                    Log.i("CachingDen", "item in disk time diff = $timeDiff")
//                    if (timeDiff > 15000) {
//                        Log.w("CachingDen", "item in disk time diff bigger than 15s")
//                        makeNetworkCall(context) { myTable.id.also { map2.remove(it) }}
//                    }
//                    true
//                })
//            }
        }
    }

    private val map: ConcurrentMap<Int, Boolean> = ConcurrentHashMap()
    private val map2 = mutableMapOf<Int, Boolean>()
}