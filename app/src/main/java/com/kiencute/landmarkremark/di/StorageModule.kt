package com.kiencute.landmarkremark.di

import android.content.Context
import com.kiencute.landmarkremark.data.local.AppDatabase
import com.kiencute.landmarkremark.data.local.NoteDao
import com.kiencute.landmarkremark.data.local.UserDao
import com.kiencute.landmarkremark.data.remote.note.NoteService
import com.kiencute.landmarkremark.data.remote.user.UserService
import com.kiencute.landmarkremark.data.repository.note.NoteRepository
import com.kiencute.landmarkremark.data.repository.user.UserRepository
import com.kiencute.landmarkremark.datastore.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideNoteDao(db: AppDatabase) = db.noteDao()

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    fun provideNoteService(retrofit: Retrofit): NoteService =
        retrofit.create(NoteService::class.java)

    @Singleton
    @Provides
    fun provideNoteRepository(remoteDataSource: NoteService, localDataSource: NoteDao) =
        NoteRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideUserRepository(remoteDataSource: UserService, localDataSource: UserDao) =
        UserRepository(remoteDataSource, localDataSource)
}