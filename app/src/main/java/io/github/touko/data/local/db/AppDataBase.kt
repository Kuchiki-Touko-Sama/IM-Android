package io.github.touko.data.local.db

import androidx.room3.Database
import androidx.room3.RoomDatabase
import io.github.touko.data.local.dao.MessageDao
import io.github.touko.data.local.entity.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 1
)
abstract class AppDataBase: RoomDatabase() {
    abstract fun messageDao(): MessageDao
}