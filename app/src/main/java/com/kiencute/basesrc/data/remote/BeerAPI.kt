package com.kiencute.basesrc.data.remote


import com.kiencute.basesrc.data.entities.Beer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface BeerAPI {
    @Headers("Content-Type: application/json")
    @GET("random")
    suspend fun getAllBeers(@Query("fields") fields: String) : Response<List<Beer>>

    @GET("beer/{id}")
    suspend fun getBeer(@Path("id") id: Int): Response<Beer>
}