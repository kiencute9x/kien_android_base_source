package com.kiencute.landmarkremark.data.repository

import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.entities.User
import com.kiencute.landmarkremark.data.local.UserDao
import com.kiencute.landmarkremark.data.remote.APIService
import com.kiencute.landmarkremark.data.remote.UserRemoteDataSource
import com.kiencute.landmarkremark.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemote: UserRemoteDataSource,
    private val userDao: UserDao

) {
    fun getDataAsFlow(): Flow<Resource<out List<Any>>> = flow {
        emit(Resource.Loading())
        when (val result = userRemote.getUsers()) {
            is Resource.Success -> {
                result.data?.users?.let { users ->
                    users.forEach { user ->

                        val userEntity = User(user.id, user.name, user.email)
                        userDao.insertUser(userEntity)

                        user.notes.forEach { note ->
                            val noteEntity = Note(
                                note.id,
                                user.id,
                                note.latitude,
                                note.longitude,
                                note.title,
                                note.description
                            )
                            userDao.insertNote(noteEntity)
                        }
                    }
                    val allNotes = users.flatMap { it.notes }
                    emit(Resource.Success(allNotes))
                }
            }

            is Resource.Err -> {
                emit(Resource.Err(result.message ?: "An unknown error occurred"))
                val localData = userDao.getAllNotes()
                    .firstOrNull()
                localData?.let {
                    emit(Resource.Success(it))
                }
            }

            else -> {}
        }
    }.flowOn(Dispatchers.IO)

    fun getNoteByUserId(userId: Int): Flow<Resource<out List<Any>>> = flow {
        emit(Resource.Loading())
        when (val result = userRemote.getNotesByUserId(userId)) {
            is Resource.Success -> {
                result.data?.let{ it ->
                    userDao.insertAllNote( it.map { it.toNote()} )
                    emit(Resource.Success(it.toNoteList()))
                }

            }

            is Resource.Err -> {
                emit(Resource.Err(result.message ?: "An unknown error occurred"))
                val localData = userDao.getAllNotes()
                    .firstOrNull()
                localData?.let {
                    emit(Resource.Success(it))
                }
            }

            else -> {}
        }

    }.flowOn(Dispatchers.IO)

    private fun APIService.RemoteNote.toNote(): Note = Note(
        noteId = this.id,
        userId = this.userId,
        latitude = this.latitude,
        longitude = this.longitude,
        title = this.title,
        description = this.description
    )

    fun List<APIService.RemoteNote>.toNoteList(): List<Note> = this.map { it.toNote() }

}
