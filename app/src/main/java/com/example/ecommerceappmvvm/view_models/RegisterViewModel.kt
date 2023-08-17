package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import com.example.ecommerceappmvvm.model.data_class.User
import com.example.ecommerceappmvvm.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val mFirebaseAuth: FirebaseAuth,
    private val mFireStoreDb : FirebaseFirestore
) : ViewModel() {
    private val _registerStateFlow = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val registerStateFlow : Flow<Resource<User>> = _registerStateFlow

    private val _registerValidationStateChannel = Channel<FieldState>()
    val registerValidationState = _registerValidationStateChannel.receiveAsFlow()

    fun createAccount(user : User, password : String){

        if(isValid(user.email , password)){
            runBlocking {
                _registerStateFlow.emit(Resource.Loading())
            }

            mFirebaseAuth.createUserWithEmailAndPassword(user.email , password)
                .addOnSuccessListener {
                _registerStateFlow.value = Resource.Success(user)
                insertAccountIntoFireStore(it.user!!.uid , user)

            }.addOnFailureListener{
                _registerStateFlow.value = Resource.Failure(it.message.toString())
            }
        }else{
           runBlocking {
               _registerValidationStateChannel.send(getFieldsState(user.email, password))
           }
        }

    }


   private fun insertAccountIntoFireStore(userUID : String , user : User){
        mFireStoreDb.collection(Constants.USER_COLLECTION).
                document(userUID).
                set(user).
                addOnSuccessListener {
                    _registerStateFlow.value = Resource.Success(user)
                }.addOnFailureListener{
                    _registerStateFlow.value = Resource.Failure(it.message.toString())
                }
   }
}