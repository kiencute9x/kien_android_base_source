package com.kiencute.landmarkremark.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.repository.UserRepository
import com.kiencute.landmarkremark.utils.Resource
import com.kiencute.landmarkremark.utils.USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _note = MutableStateFlow<Resource<List<Note>>>(Resource.Loading())
    val noteData: StateFlow<Resource<List<Note>>> = _note

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getDataAsFlow().collect()
        }
    }

    fun loadNotesForUser(userId: Int) {
        viewModelScope.launch {
            repository.getNoteByUserId(userId).collect { resource ->
                _note.value = resource as Resource<List<Note>>
            }
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        refreshNotes()
                    }
                    is Resource.Err -> {
                    }
                    else -> {}
                }
            }
        }
    }
    private fun refreshNotes() {
        viewModelScope.launch {
            repository.refreshNotes().collect { resource ->
                _note.value = resource
            }
        }
    }

}