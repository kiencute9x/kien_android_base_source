package com.kiencute.landmarkremark.data.remote.user

import com.kiencute.landmarkremark.data.base.BaseDataSource
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userService: UserService
) : BaseDataSource() {

    suspend fun getUsers() = getResult { userService.getUsers()}
    suspend fun getUser(id: Int) = getResult { userService.getUser(id) }
}