package com.aranteknoloji.cachingtraining

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.aranteknoloji.cachingtraining.db.MyTable
import com.aranteknoloji.cachingtraining.db.myDatabase
import com.aranteknoloji.cachingtraining.repositories.RoomRepository
import com.aranteknoloji.cachingtraining.sources.DataSource
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null
//    private var disposable2: Disposable? = null
    private val roomRepository : RoomRepository by lazy { RoomRepository(this) }
    private val dataSource: DataSource by lazy { DataSource() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //db listener
        disposable = myDatabase(this).myDao().myEntities()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("CachingDen", "new item has been shown")
                db_text.text = it.firstOrNull()?.nameId
            }


        //db initial item
//        Completable.fromCallable {
//            val dao = myDatabase(context = this).myDao()
//            val item: MyTable? = dao.myTable()
//            if (item == null) {
//                Log.i("CachingDen", "initial item is added")
//                dao.insert(MyTable(id = 0, nameId = "Initial", time = System.currentTimeMillis()))
//            }
//        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()


        //Main text onLick listener
//        main_text.setOnClickListener {
////            dataSource.getCachingObservable(this)
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe ({ main_text.text = it.nameId }, Throwable::printStackTrace)
//            dataSource.getCachedFlowable(this)
//                .observeOn(AndroidSchedulers.mainThread())
////                .take(2)
//                .subscribe ({
//                    Log.w("CachingDen", "new item has been shown for listener")
//                    main_text.text = it.nameId
//                }, Throwable::printStackTrace)
//        }
    }

//    override fun onResume() {
//        super.onResume()
//        Log.e("MainActivity", "initial call")
//        disposable2 = roomRepository.getObservableDataFromDb()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { main_text.text = it.nameId }
//    }

    private var dbDisposable: Disposable? = null

    override fun onResume() {
        Log.e("CachingDen", "onResume()")
        super.onResume()
        dbDisposable = dataSource.roomCachedItems(this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ main_text.text = it.firstOrNull()?.nameId }, Throwable::printStackTrace)
    }

    override fun onPause() {
        Log.e("CachingDen", "onPause()")
        super.onPause()
        dbDisposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
//        disposable2?.dispose()
        roomRepository.disposaCallsIfAny()
    }

    fun makeNetCall(view: View) {
        dataSource.makeNetworkCall(view.context)
    }

    fun secondSubClick(view: View) {
        val dis = dataSource.roomCachedItems(view.context)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ (view as Button).text = it.firstOrNull()?.nameId }, Throwable::printStackTrace)
    }
}
