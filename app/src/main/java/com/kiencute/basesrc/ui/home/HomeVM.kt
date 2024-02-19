package com.kiencute.basesrc.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiencute.basesrc.data.entities.Entity
import com.kiencute.basesrc.data.repository.EntityRepository
import com.kiencute.basesrc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: EntityRepository
) : ViewModel() {
    private val _entities = MutableStateFlow<Resource<List<Entity>>>(Resource.Loading())
    val entities: StateFlow<Resource<List<Entity>>> = _entities

    init {
        loadEntities()
    }

    private fun loadEntities() {
        viewModelScope.launch {
            repository.getEntitiesAsFlow().collect { data ->
                _entities.value = data as Resource<List<Entity>>
            }
        }
    }
}