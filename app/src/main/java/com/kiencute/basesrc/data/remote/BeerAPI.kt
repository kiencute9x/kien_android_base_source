package com.kiencute.basesrc.data.remote


import com.kiencute.basesrc.data.entities.Beer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface BeerAPI {

    @Headers("Content-Type: application/json")
    @GET("beers")
    suspend fun getAllBeers() : Response <List<Beer>>

    @GET("beer/{id}")
    suspend fun getBeer(@Path("id") id: Int): Response<Beer>
}