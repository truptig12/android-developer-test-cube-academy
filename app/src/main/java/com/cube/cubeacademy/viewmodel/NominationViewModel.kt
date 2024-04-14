package com.cube.cubeacademy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cube.cubeacademy.lib.db.NominationWithNominee
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NominationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.refreshData()
        }
    }

    private val _events = MutableSharedFlow<UiEvent<List<NominationWithNominee>>>()
    val events: SharedFlow<UiEvent<List<NominationWithNominee>>> = _events.asSharedFlow()

    fun collectNominations() = viewModelScope.launch {
        _events.emit(UiEvent.Loading)
        try {
            repository.getNominationsWithNominees().collect { nominationsWithNominees ->
                try {
                    _events.emit(UiEvent.Success(nominationsWithNominees))
                } catch (e: Exception) {
                    _events.emit(UiEvent.Error("Failed to fetch data: ${e.message}"))
                }
            }
        } catch (e: Exception) {
            _events.emit(UiEvent.Error("Failed to fetch data: ${e.message}"))
        }
    }
}