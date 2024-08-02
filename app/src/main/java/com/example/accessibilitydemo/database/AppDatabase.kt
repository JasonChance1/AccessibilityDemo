package com.example.accessibilitydemo.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.accessibilitydemo.MainApplication
import com.example.accessibilitydemo.database.dao.RecordDao
import com.example.accessibilitydemo.database.entity.JsonDataConverter
import com.example.accessibilitydemo.database.entity.Record

@Database(entities = [Record::class], version = 1, exportSchema = false)
@TypeConverters(JsonDataConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun get(): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    MainApplication.instance(),
                    AppDatabase::class.java,
                    "accessibility"
                ).allowMainThreadQueries()
//                    .fallbackToDestructiveMigration()  // 强制升级，清空数据库
                    .build()
            }
            return instance!!
        }
    }
}