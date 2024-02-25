package com.kiencute.landmarkremark.di

import android.content.Context
import com.kiencute.landmarkremark.data.local.AppDatabase
import com.kiencute.landmarkremark.data.local.UserDao
import com.kiencute.landmarkremark.data.remote.APIService
import com.kiencute.landmarkremark.data.remote.UserRemoteDataSource
import com.kiencute.landmarkremark.data.repository.UserRepository
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
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    fun provideAPIService(retrofit: Retrofit): APIService =
        retrofit.create(APIService::class.java)

    @Singleton
    @Provides
    fun provideUserRepository(remoteDataSource: UserRemoteDataSource, userDao: UserDao ) =
        UserRepository(remoteDataSource, userDao)

}