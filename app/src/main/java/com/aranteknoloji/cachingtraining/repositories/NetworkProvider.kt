package com.aranteknoloji.cachingtraining.repositories

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class NetworkProvider {

    private var counter = 0

    private val maps = listOf("First Item", "Second Item", "Third Item",
        "Forth Item", "Fifth Item", "Sixth Item", "Seventh Item", "Eighth Item", "Nine Item")

    fun getNetworkObservableRefreshing() =
        Observable.interval(0, 10, TimeUnit.SECONDS)
            .map { maps[it.toInt() % maps.size] }

    fun getSingleObservable() =
        Observable.just(maps[counter % maps.size].also { counter++ })
}