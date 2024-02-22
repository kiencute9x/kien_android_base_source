package com.kiencute.landmarkremark.data.repository

import com.kiencute.landmarkremark.data.local.EntityDao
import com.kiencute.landmarkremark.data.remote.EntityRemoteDataSource
import com.kiencute.landmarkremark.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EntityRepository @Inject constructor(
    private val remoteDataSource: EntityRemoteDataSource,
    private val dao: EntityDao
) {

    fun getEntitiesAsFlow(): Flow<Any> = flow {
        emit(Resource.Loading())
        when (val result = remoteDataSource.getAllData()) {
            is Resource.Success -> {
                result.data?.let { dao.insertAll(it) }
                emitAll(dao.getAllEntities().map { Resource.Success(it) })
            }

            is Resource.Err -> {
                emit(Resource.Err(result.message!!, null))
                emitAll(dao.getAllEntities().map { Resource.Success(it) })
            }

            else -> emit(Resource.Loading())
        }
    }.flowOn(Dispatchers.IO)

}