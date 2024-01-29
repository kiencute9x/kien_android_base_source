package com.kiencute.basesrc.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import com.kiencute.basesrc.utils.Resource.Status.*

fun <T, A> performGetOperation(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> = liveData(Dispatchers.IO) {
    emit(Resource.loading())

    val source = databaseQuery.invoke().map { Resource.success(it) }
    emitSource(source)

    val responseStatus = networkCall.invoke()

    if (responseStatus.status == SUCCESS) {
        responseStatus.data?.let {
            saveCallResult(it)
        }
    } else if (responseStatus.status == ERROR) {
        emit(Resource.error(responseStatus.message!!))
        emitSource(source)
    }
}