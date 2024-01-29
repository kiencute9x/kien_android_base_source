package com.kiencute.basesrc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kiencute.basesrc.data.entities.Beer
import com.kiencute.basesrc.utils.ROOM_DB_NAME


@Database(entities = [Beer::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun beerDao(): BeerDao

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
