package com.example.ecommerceappmvvm.view_models

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.util.ApplicationState
import com.example.ecommerceappmvvm.util.ApplicationState.*
import com.example.ecommerceappmvvm.util.Constants
import com.example.ecommerceappmvvm.util.Constants.INTRODUCTION_SP_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val mSharedPreferences: SharedPreferences,
    private val mFirebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigateState : MutableStateFlow<Int> = MutableStateFlow(0)
    val navigateState : StateFlow<Int> = _navigateState

    init {

        when(getApplicationState()){
            is UserAuthenticated -> {
                viewModelScope.launch {
                    _navigateState.emit(SHOPPING_ACTIVITY)
                }
            }

            is StartButtonClicked -> {
                viewModelScope.launch {
                    _navigateState.emit(OPTION_ACTIVITY)
                }
            }

            is UserFirstTime -> viewModelScope.launch {
                _navigateState.emit(-1)
            }

        }

    }
    companion object{
        const val SHOPPING_ACTIVITY = 15
        const val OPTION_ACTIVITY = R.id.action_introductionFragment_to_accountOptionsFragment
    }

     fun startButtonClick(){
        mSharedPreferences.edit().putBoolean(INTRODUCTION_SP_KEY , true).apply()
    }

    private fun getApplicationState() : ApplicationState {
        if(mFirebaseAuth.currentUser != null)
            return UserAuthenticated

        if(mSharedPreferences.getBoolean(INTRODUCTION_SP_KEY,false))
            return StartButtonClicked

        return UserFirstTime
    }
}