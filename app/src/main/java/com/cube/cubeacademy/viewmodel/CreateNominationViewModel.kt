package com.cube.cubeacademy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.FormState
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNominationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _events = MutableSharedFlow<UiEvent<List<Nominee>>>()
    val events: SharedFlow<UiEvent<List<Nominee>>> = _events.asSharedFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    private val _nominationEvent = MutableSharedFlow<UiEvent<Nomination>>()
    val nominationEvent: SharedFlow<UiEvent<Nomination>> = _nominationEvent.asSharedFlow()

    private val radioValueMap = mapOf(
        "Very Unfair" to "very_unfair",
        "Unfair" to "unfair",
        "Not Sure" to "not_sure",
        "Fair" to "fair",
        "Very Fair" to "very_fair"
    )


    fun collectNominees() = viewModelScope.launch {
        _events.emit(UiEvent.Loading)
        try {
            repository.getNominees().collect { nominationsWithNominees ->
                try {
                    _events.emit(UiEvent.Success(nominationsWithNominees))
                } catch (e: Exception) {
                    _events.emit(UiEvent.Error("Failed to fetch data: ${e.message}"))
                }
            }
        }catch (e: Exception) {
            _events.emit(UiEvent.Error("Failed to fetch data: ${e.message}"))
        }

    }

    fun validateNominee(nomineeId: String?) {
        val isValid = !nomineeId.isNullOrEmpty()
        _formState.value = _formState.value.copy(
            nomineeId = nomineeId,
            isNomineeValid = isValid
        )
        updateSubmitButtonState()
        updateValueAddedState()
    }

    fun validateReason(reason: String?) {
        val isValid = !reason.isNullOrEmpty()
        _formState.value = _formState.value.copy(
            reason = reason,
            isReasonValid = isValid
        )
        updateSubmitButtonState()
        updateValueAddedState()
    }

    fun validateProcess(process: String?) {
        val isValid = !process.isNullOrEmpty()
        _formState.value = _formState.value.copy(
            process = radioValueMap[process],
            isProcessValid = isValid
        )
        updateSubmitButtonState()
        updateValueAddedState()
    }

    private fun updateSubmitButtonState() {
        _formState.value = _formState.value.let {
            it.copy(isSubmitEnabled = it.isNomineeValid && it.isReasonValid && it.isProcessValid)
        }
    }

    private fun updateValueAddedState() {
        _formState.value = _formState.value?.let {
            it.copy(
                isValueAdded = it.isNomineeValid || it.isReasonValid || it.isProcessValid
            )
        }!!
    }

    fun submitData() {
        viewModelScope.launch {
            when (val response = repository.createNomination(
                _formState.value.nomineeId.orEmpty(),
                _formState.value.reason.orEmpty(),
                _formState.value.process.orEmpty()
            )) {
                is UiEvent.Success -> {
                    _nominationEvent.emit(UiEvent.Success(response.data))
                }
                is UiEvent.Error -> {
                    _nominationEvent.emit(UiEvent.Error(response.message))
                }

                UiEvent.Loading -> {}
            }
        }
    }

}