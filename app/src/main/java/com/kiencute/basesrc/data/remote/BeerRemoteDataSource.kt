package com.kiencute.basesrc.data.remote

import javax.inject.Inject

class BeerRemoteDataSource @Inject constructor(
    private val eAPI: BeerAPI
): BaseDataSource() {

    suspend fun getBeers() = getResult { eAPI.getAllBeers() }
//    suspend fun getBeer(id: Int) = getResult { eAPI.getBeer(id) }
}