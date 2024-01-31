package com.kiencute.basesrc.di

import android.content.Context
import com.kiencute.basesrc.data.local.AppDatabase
import com.kiencute.basesrc.data.local.EntityDao
import com.kiencute.basesrc.data.remote.APIService
import com.kiencute.basesrc.data.remote.EntityRemoteDataSource
import com.kiencute.basesrc.data.repository.EntityRepository
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
    fun provideEmployeeDao(db: AppDatabase) = db.entityDao()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): APIService =
        retrofit.create(APIService::class.java)

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: EntityRemoteDataSource,
        localDataSource: EntityDao
    ) = EntityRepository(remoteDataSource, localDataSource)
}