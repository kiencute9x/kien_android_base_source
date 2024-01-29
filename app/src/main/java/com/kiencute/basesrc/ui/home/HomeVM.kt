package com.kiencute.basesrc.ui.home

import androidx.lifecycle.ViewModel
import com.kiencute.basesrc.data.repository.BeerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: BeerRepository
) : ViewModel(){
    val data = repository.getAllBeers()
}