package com.kiencute.landmarkremark.data.remote.note


import com.kiencute.landmarkremark.data.entities.Note
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface NoteService {
    @Headers("Content-Type: application/json")
    @GET("users.json?key=854d9b80")
    suspend fun getNotes(): Response<List<Note>>

    @Headers("Content-Type: application/json")
    @GET("beer/{id}")
    suspend fun getNote(@Path("id") id: Int): Response<Note>
}