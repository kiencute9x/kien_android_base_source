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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
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
            .addInterceptor(createMockInterceptor())
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

    private fun createMockInterceptor() = Interceptor { chain ->
        val uri = chain.request().url.toUri().toString()
        when {
            uri.endsWith("users") -> {
                val mockResponse = """
            {
              "users": [
                {
                  "id": 123,
                  "name": "John Doe",
                  "email": "john@example.com",
                  "notes": [
                    {
                      "id": 1,
                      "userId": 123,
                      "title": "Note 1",
                      "latitude": 21.028511,
                      "longitude": 105.804817,
                      "description": "Content 1"
                    },
                    {
                      "id": 4,
                      "userId": 123,
                      "title": "Note 2",
                      "latitude": 10.762622,
                      "longitude": 106.660172,
                      "description": "Content 2"
                    }
                  ]
                },
                {
                  "id": 456,
                  "name": "Jane Smith",
                  "email": "jane@example.com",
                  "notes": [
                    {
                      "id": 2,
                      "userId": 456,
                      "title": "Note 1",
                      "latitude": 16.047079,
                      "longitude": 108.206230,
                      "description": "Content 3"
                    },
                    {
                      "id": 5,
                      "userId": 456,
                      "title": "Note 2",
                      "latitude": 20.844912,
                      "longitude": 106.688084,
                      "description": "Content 4"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
                mockResponse.toMockResponse(chain)
            }

            uri.endsWith("/notes") -> {
                val mockResponse = """
            [
                {
                  "id": 1,
                  "userId": 123,
                  "title": "Note 1",
                  "latitude": 21.028511,
                  "longitude": 105.804817,
                  "description": "Content 8"
                },
                {
                  "id": 4,
                  "userId": 123,
                  "title": "Note 2",
                  "latitude": 10.762622,
                  "longitude": 106.660172,
                  "description": "Content 7"
                }
            ]
        """.trimIndent()
                mockResponse.toMockResponse(chain)
            }
            else -> chain.proceed(chain.request())
        }
    }

    private fun String.toMockResponse(chain: Interceptor.Chain): okhttp3.Response =
        okhttp3.Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(
                this.toByteArray()
                    .toResponseBody("application/json".toMediaTypeOrNull())
            )
            .addHeader("content-type", "application/json")
            .build()


    private fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}