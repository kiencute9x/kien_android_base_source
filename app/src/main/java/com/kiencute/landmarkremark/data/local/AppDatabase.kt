package com.kiencute.landmarkremark.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.entities.User
import com.kiencute.landmarkremark.utils.ROOM_DB_NAME


@Database(entities = [User::class , Note::class], version = 5, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
