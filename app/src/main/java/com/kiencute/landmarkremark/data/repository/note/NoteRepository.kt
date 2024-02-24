package com.kiencute.landmarkremark.data.repository.note

import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.local.NoteDao
import com.kiencute.landmarkremark.data.remote.note.NoteService
import com.kiencute.landmarkremark.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteService: NoteService,
    private val noteDao: NoteDao
) {
    fun getUserNotesById(userId: Int): Flow<Resource<List<Note>>> = flow {
        emit(Resource.Loading())
        val localNotes = noteDao.getUserNotesById(userId)
        emitAll(localNotes.map { Resource.Success(it) })
    }.catch { e ->
        emit(Resource.Err(e.localizedMessage ?: "An error occurred", null))
    }.flowOn(Dispatchers.IO)

    suspend fun refreshNotes() {
        try {
            val response = noteService.getNotes()
            if (response.isSuccessful) {
                response.body()?.let { notes ->
                    notes.forEach { note ->
                        noteDao.insert(note)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    fun getNoteById(noteId: Int): Flow<Resource<Note>> = flow {
        emit(Resource.Loading())
        val localNote = noteDao.getNoteById(noteId)
        emitAll(localNote.map { Resource.Success(it) })
    }.catch { e ->
        emit(Resource.Err(e.localizedMessage ?: "An error occurred", null))
    }.flowOn(Dispatchers.IO)

    suspend fun saveNote(note: Note) {
        noteDao.insert(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun fetchNoteById(id: Int): Resource<Note> {
        return try {
            val response = noteService.getNote(id)
            if (response.isSuccessful) {
                response.body()?.let { note ->
                    noteDao.insert(note)
                    Resource.Success(note)
                } ?: Resource.Err("Note not found", null)
            } else {
                Resource.Err("API call failed with error: ${response.message()}", null)
            }
        } catch (e: Exception) {
            Resource.Err("Failed to fetch note: ${e.localizedMessage}", null)
        }
    }
}
