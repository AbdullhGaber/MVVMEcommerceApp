package com.example.ecommerceappmvvm.util

sealed class RegisterValidationState {

    object Success : RegisterValidationState()
    data class Failed(val message : String) : RegisterValidationState()
}

data class FieldState(
    val email : RegisterValidationState,
    val password : RegisterValidationState
)