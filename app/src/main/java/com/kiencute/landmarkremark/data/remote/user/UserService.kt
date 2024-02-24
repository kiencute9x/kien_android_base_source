package com.kiencute.landmarkremark.data.remote.user


import com.kiencute.landmarkremark.data.entities.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface UserService {
    @Headers("Content-Type: application/json")
    @GET("users.json?key=854d9b80")
    suspend fun getUsers(): Response<List<User>>

    @Headers("Content-Type: application/json")
    @GET("beer/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>
}