package io.github.touko

import android.app.Application
import android.util.Log
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tencent.mmkv.MMKV
import io.github.touko.data.local.db.AppDataBase
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executors

class App : Application() {

    companion object {
        lateinit var db: AppDataBase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val databasePath = applicationContext.getDatabasePath("touko.db")
        db = Room.databaseBuilder<AppDataBase>(databasePath.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        MMKV.initialize(this)
    }
}