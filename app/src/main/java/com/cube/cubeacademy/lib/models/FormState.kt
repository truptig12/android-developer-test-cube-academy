package com.cube.cubeacademy.lib.models

data class FormState(
    val nomineeId: String? = null,
    val reason: String? = null,
    val process: String? = null,
    val isNomineeValid: Boolean = false,
    val isReasonValid: Boolean = false,
    val isProcessValid: Boolean = false,
    val isSubmitEnabled: Boolean = false,
    val isValueAdded:Boolean = false
)