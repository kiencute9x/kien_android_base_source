package com.kiencute.landmarkremark.data.remote


import com.kiencute.landmarkremark.data.entities.Note
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface APIService {
    @GET("users")
    suspend fun getUsers(): Response<UserResponse>

    suspend fun getNotes(): Response<List<RemoteNote>>

    @GET("{userId}/notes")
    suspend fun getNotesByUserId(@Path("userId") userId: Int): Response<List<RemoteNote>>

    data class UserResponse(
        val users: List<RemoteUser>
    )

    data class RemoteUser(
        val id: Int,
        val name: String,
        val email: String,
        val notes: List<RemoteNote>
    )

    data class RemoteNote(
        val id: Int,
        val userId: Int,
        val title: String,
        val latitude: Double,
        val longitude: Double,
        val description: String
    )
}
