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

/**
 * ViewModel for handling the creation of nominations within the application.
 * This class manages UI-related data and handles business logic to communicate with the repository layer
 * for data operations.
 *
 * @Inject allows Hilt to inject the required Repository dependency.
 */
@HiltViewModel
class CreateNominationViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    // Holds a list of UI events related to nominee retrieval which can be observed by the UI.
    private val _events = MutableSharedFlow<UiEvent<List<Nominee>>>()
    val events: SharedFlow<UiEvent<List<Nominee>>> = _events.asSharedFlow()

    // Tracks the state of the form, such as the validation states of inputs.
    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    // Emits events related to the nomination submission process.
    private val _nominationEvent = MutableSharedFlow<UiEvent<Nomination>>()
    val nominationEvent: SharedFlow<UiEvent<Nomination>> = _nominationEvent.asSharedFlow()

    // Maps textual representations of process options to their respective data keys.
    private val radioValueMap = mapOf(
        "Very Unfair" to "very_unfair",
        "Unfair" to "unfair",
        "Not Sure" to "not_sure",
        "Fair" to "fair",
        "Very Fair" to "very_fair"
    )

    /**
     * Collects nominees from the repository and emits them through a SharedFlow.
     * Emits events based on the outcome.
     */
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
        } catch (e: Exception) {
            _events.emit(UiEvent.Error("Failed to fetch data: ${e.message}"))
        }

    }

    /**
     * Validates the nominee ID from spinner data and updates the form state accordingly.
     * @param nomineeId The ID to validate.
     */
    fun validateNominee(nomineeId: String?) {
        val isValid = !nomineeId.isNullOrEmpty()
        _formState.value = _formState.value.copy(
            nomineeId = nomineeId,
            isNomineeValid = isValid
        )
        updateSubmitButtonState()
        updateValueAddedState()
    }

    /**
     * Validates the reason for the nomination from the edittext.
     * @param reason The reason input by the user.
     */
    fun validateReason(reason: String?) {
        val isValid = !reason.isNullOrEmpty()
        _formState.value = _formState.value.copy(
            reason = reason,
            isReasonValid = isValid
        )
        updateSubmitButtonState()
        updateValueAddedState()
    }

    /**
     * Validates the process selection from radiogroup and updates the form state.
     * @param process selected by the user.
     */
    fun validateProcess(process: String?) {
        val isValid = !process.isNullOrEmpty()
        _formState.value = _formState.value.copy(
            process = radioValueMap[process],
            isProcessValid = isValid
        )
        updateSubmitButtonState()
        updateValueAddedState()
    }

    // Updates the enable state of the submit button based on the validity of form inputs.
    private fun updateSubmitButtonState() {
        _formState.value = _formState.value.let {
            it.copy(isSubmitEnabled = it.isNomineeValid && it.isReasonValid && it.isProcessValid)
        }
    }

    // Updates the state indicating if any value has been added to the form to show the bottom sheet when data is present.
    private fun updateValueAddedState() {
        _formState.value = _formState.value?.let {
            it.copy(
                isValueAdded = it.isNomineeValid || it.isReasonValid || it.isProcessValid
            )
        }!!
    }

    /**
     * Submits the nomination data to the repository and handling the response.
     */
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