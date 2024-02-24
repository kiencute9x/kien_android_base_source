package com.kiencute.landmarkremark.ui.map

import com.kiencute.landmarkremark.data.repository.user.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            repository.getUserWithNotes(2).collect { data ->
                _note.value = data as Resource<List<Note>>
            }
        }
    }
}