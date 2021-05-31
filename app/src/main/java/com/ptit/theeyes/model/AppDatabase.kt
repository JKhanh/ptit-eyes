package com.ptit.theeyes.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DetectModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun modelDao(): DetectModelDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun buildDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "theeyes.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}