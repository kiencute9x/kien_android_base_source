package com.kiencute.basesrc.data.repository


import com.kiencute.basesrc.extentions.performGetOperation
import com.kiencute.basesrc.data.remote.EntityRemoteDataSource
import com.kiencute.basesrc.data.local.EntityDao
import javax.inject.Inject

class EntityRepository @Inject constructor(
    private val remoteDataSource: EntityRemoteDataSource,
    private val localDataSource: EntityDao
) {

    fun getEntityForId(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getEntity(id) },
        networkCall = { remoteDataSource.getDataForId(id) },
        saveCallResult = { localDataSource.insert(it) }
    )

    fun getAllEntities() = performGetOperation(
        databaseQuery = { localDataSource.getAllEntities() },
        networkCall = { remoteDataSource.getAllData() },
        saveCallResult = { localDataSource.insertAll(it)
        }
    )
}