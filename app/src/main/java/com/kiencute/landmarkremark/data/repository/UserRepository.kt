package com.kiencute.landmarkremark.data.repository

import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.entities.User
import com.kiencute.landmarkremark.data.local.UserDao
import com.kiencute.landmarkremark.data.remote.APIService
import com.kiencute.landmarkremark.data.remote.UserRemoteDataSource
import com.kiencute.landmarkremark.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
                result.data?.let { it ->
                    userDao.insertAllNote(it.map { it.toNote() })
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

    fun insertNote(note: Note): Flow<Resource<out Any>> = flow {
        emit(Resource.Loading())
        val remoteNote = note.toRemoteNote()
        when (val result = userRemote.createNote(remoteNote)) {
            is Resource.Success -> {
                result.data?.let { it ->
                    userDao.insertNote(it.toNote())
                    emit(Resource.Success(it.toNote()))
                }
            }

            is Resource.Err -> {
//                emit(Resource.Err(result.message ?: "Save Note Error !"))

                // if error call server save in local (just test data local)
                userDao.insertNote(remoteNote.toNote())
                emit(Resource.Success(remoteNote.toNote()))

            }

            else -> {}
        }
    }.flowOn(Dispatchers.IO)

    fun refreshNotes(): Flow<Resource<List<Note>>> = flow {
        emit(Resource.Loading())
        try {
            val notes = userDao.getAllNotes().first()
            emit(Resource.Success(notes))
        } catch (e: Exception) {
            emit(Resource.Err("Could not fetch notes: ${e.message}"))
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

    private fun List<APIService.RemoteNote>.toNoteList(): List<Note> = this.map { it.toNote() }

    private fun Note.toRemoteNote(): APIService.RemoteNote {
        return APIService.RemoteNote(
            id = this.noteId,
            userId = this.userId,
            title = this.title,
            latitude = this.latitude,
            longitude = this.longitude,
            description = this.description
        )
    }


}
