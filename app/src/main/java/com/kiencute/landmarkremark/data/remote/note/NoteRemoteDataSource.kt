package com.kiencute.landmarkremark.data.remote.note

import com.kiencute.landmarkremark.data.base.BaseDataSource
import javax.inject.Inject

class NoteRemoteDataSource @Inject constructor(
    private val noteService: NoteService
) : BaseDataSource() {

    suspend fun getNotes() = getResult { noteService.getNotes() }
    suspend fun getNote(id: Int) = getResult { noteService.getNote(id) }
}