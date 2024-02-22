package com.kiencute.landmarkremark.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kiencute.landmarkremark.data.entities.Entity
import com.kiencute.landmarkremark.utils.ROOM_DB_NAME


@Database(entities = [Entity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entityDao(): EntityDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, ROOM_DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}
