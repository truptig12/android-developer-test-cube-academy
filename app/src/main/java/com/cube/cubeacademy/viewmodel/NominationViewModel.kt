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

    // Initialize the ViewModel and start data synchronization.
    init {
        viewModelScope.launch {
            repository.refreshData()
        }
    }

    // Using MutableSharedFlow to manage UI events allows for a robust way to handle multiple UI updates
    // based on data changes from the repository.
    private val _events = MutableSharedFlow<UiEvent<List<NominationWithNominee>>>()
    val events: SharedFlow<UiEvent<List<NominationWithNominee>>> = _events.asSharedFlow()

    /**
     * Initiates the collection of nomination data with their corresponding nominees.
     * Emits different UI events based on the data state, which the UI layers listen to
     * in order to update the displayed content.
     */
    fun collectNominations() = viewModelScope.launch {
        _events.emit(UiEvent.Loading)
        try {
            // Fetch and observe changes to nominations with nominees. This stream is collected continuously.
            repository.getNominationsWithNominees().collect { nominationsWithNominees ->
                try {
                    // If data is successfully fetched and processed, emit a Success event with the data.
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