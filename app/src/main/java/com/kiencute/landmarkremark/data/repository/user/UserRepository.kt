package com.kiencute.landmarkremark.data.repository.user

import com.kiencute.landmarkremark.data.entities.User
import com.kiencute.landmarkremark.data.local.UserDao
import com.kiencute.landmarkremark.data.local.UserWithNotes
import com.kiencute.landmarkremark.data.remote.user.UserService
import com.kiencute.landmarkremark.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao
) {
    suspend fun refreshUsers() {
        try {
            val response = userService.getUsers()
            if (response.isSuccessful) {
                response.body()?.let { users ->
                    users.forEach { user ->
                        userDao.insert(user)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    fun getAllUsers(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        val localUsers = userDao.getAllUsers()
        emitAll(localUsers.map { Resource.Success(it) })
    }.catch { e ->
        emit(Resource.Err(e.localizedMessage ?: "An error occurred", null))
    }.flowOn(Dispatchers.IO)

    fun getUserWithNotes(userId: Int): Flow<Resource<UserWithNotes>> = flow {
        emit(Resource.Loading())
        val userWithNotes = userDao.getUserWithNotes(userId)
        emitAll(userWithNotes.map { Resource.Success(it) })
    }.catch { e ->
        emit(Resource.Err(e.localizedMessage ?: "An error occurred", null))
    }.flowOn(Dispatchers.IO)

    suspend fun fetchUserById(id: Int): Resource<User> {
        return try {
            val response = userService.getUser(id)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    userDao.insert(user)
                    Resource.Success(user)
                } ?: Resource.Err("User not found", null)
            } else {
                Resource.Err("API call failed with error: ${response.message()}", null)
            }
        } catch (e: Exception) {
            Resource.Err("Failed to fetch user: ${e.localizedMessage}", null)
        }
    }
}
