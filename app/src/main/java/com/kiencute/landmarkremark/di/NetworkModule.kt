package com.kiencute.landmarkremark.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kiencute.landmarkremark.utils.APPLICATION_JSON
import com.kiencute.landmarkremark.utils.BASE_URL
import com.kiencute.landmarkremark.utils.CONTENT_TYPE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .addInterceptor(createHeaderInterceptor())
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request()
                .newBuilder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build()
            chain.proceed(newRequest)
        }
    }

    private fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}