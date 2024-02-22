package com.kiencute.landmarkremark.data.remote


import com.kiencute.landmarkremark.data.entities.Entity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface APIService {
    @Headers("Content-Type: application/json")
    @GET("users.json?key=854d9b80")
    suspend fun getEntities(): Response<List<Entity>>

    @Headers("Content-Type: application/json")
    @GET("beer/{id}")
    suspend fun getEntity(@Path("id") id: Int): Response<Entity>
}