package com.kiencute.basesrc.ui.home

import androidx.lifecycle.ViewModel
import com.kiencute.basesrc.data.repository.EntityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    repository: EntityRepository
) : ViewModel(){
    val data = repository.getAllEntities()
}