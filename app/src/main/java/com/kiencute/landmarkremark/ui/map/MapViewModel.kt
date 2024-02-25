package com.kiencute.landmarkremark.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.repository.UserRepository
import com.kiencute.landmarkremark.utils.Resource
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
}