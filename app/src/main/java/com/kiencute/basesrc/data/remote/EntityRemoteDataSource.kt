package com.kiencute.basesrc.data.remote

import javax.inject.Inject

class EntityRemoteDataSource @Inject constructor(
    private val eAPI: APIService
): BaseDataSource() {

    suspend fun getAllData() = getResult { eAPI.getEntities() }
    suspend fun getDataForId(id: Int) = getResult { eAPI.getEntity(id) }
}