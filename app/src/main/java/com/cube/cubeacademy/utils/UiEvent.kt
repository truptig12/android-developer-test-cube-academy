package com.cube.cubeacademy.utils

sealed class UiEvent<out T> {
    data class Success<T>(val data: T) : UiEvent<T>()
    data class Error(val message: String) : UiEvent<Nothing>()
    data object Loading : UiEvent<Nothing>()
}