package com.example.spacetraveler.core.ui

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}
