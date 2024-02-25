package com.kiencute.landmarkremark.data.remote

import com.kiencute.landmarkremark.data.base.BaseDataSource
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val api: APIService
) : BaseDataSource() {

    suspend fun getUsers() = getResult { api.getUsers() }
    suspend fun getNotes() = getResult { api.getNotes() }

    suspend fun getNotesByUserId(userId: Int)  = getResult { api.getNotesByUserId(userId) }


}