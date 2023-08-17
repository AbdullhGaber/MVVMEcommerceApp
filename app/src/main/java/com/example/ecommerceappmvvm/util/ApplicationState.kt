package com.example.ecommerceappmvvm.util

sealed class ApplicationState{
    object UserAuthenticated : ApplicationState()
    object StartButtonClicked : ApplicationState()
    object UserFirstTime : ApplicationState()
}
