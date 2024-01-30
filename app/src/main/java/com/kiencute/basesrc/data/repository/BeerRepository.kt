package com.kiencute.basesrc.data.repository


import com.kiencute.basesrc.extentions.performGetOperation
import com.kiencute.basesrc.data.remote.BeerRemoteDataSource
import com.kiencute.basesrc.data.local.BeerDao
import javax.inject.Inject

class BeerRepository @Inject constructor(
    private val remoteDataSource: BeerRemoteDataSource,
    private val localDataSource: BeerDao
) {

    fun getBeerForId(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getBeer(id) },
        networkCall = { remoteDataSource.getBeer(id) },
        saveCallResult = { localDataSource.insert(it) }
    )

    fun getAllBeers() = performGetOperation(
        databaseQuery = { localDataSource.getAllBeers() },
        networkCall = { remoteDataSource.getBeers() },
        saveCallResult = { localDataSource.insertAll(it)
        }
    )
}