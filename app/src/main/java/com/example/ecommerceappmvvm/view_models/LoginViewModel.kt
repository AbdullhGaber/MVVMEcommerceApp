package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.util.FieldState
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.getFieldsState
import com.example.ecommerceappmvvm.util.isValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val mFirebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginStateFlow = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val loginStateFlow = _loginStateFlow.asStateFlow()

    private val _loginStateChannel = Channel<FieldState>()
    val loginStateChannel = _loginStateChannel.receiveAsFlow()

    private val _resetPasswordEmailSharedFlow = MutableSharedFlow<Resource<String>>()
    val resetPasswordEmail = _resetPasswordEmailSharedFlow.asSharedFlow()


    fun login(email : String , password : String){

        if(isValid(email , password)){
            runBlocking {
                _loginStateFlow.emit(Resource.Loading())
            }

            mFirebaseAuth.signInWithEmailAndPassword(email , password)
                .addOnSuccessListener {
                        _loginStateFlow.value = Resource.Success(it?.user)

                }.addOnFailureListener{
                        _loginStateFlow.value = Resource.Failure(it.message)
                }
        }else{
            runBlocking {
                _loginStateChannel.send(getFieldsState(email , password))
            }
        }


    }

   fun resetPassword(email: String){
       viewModelScope.launch {
           _resetPasswordEmailSharedFlow.emit(Resource.Loading())
       }

       mFirebaseAuth.sendPasswordResetEmail(email)
           .addOnSuccessListener {
               viewModelScope.launch {
                   _resetPasswordEmailSharedFlow.emit(Resource.Success(email))
               }
           }.addOnFailureListener{
               viewModelScope.launch {
                   _loginStateFlow.emit(Resource.Failure(it.message))
               }
           }
   }

}