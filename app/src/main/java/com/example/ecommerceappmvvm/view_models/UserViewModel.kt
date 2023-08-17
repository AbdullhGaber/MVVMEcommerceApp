package com.example.ecommerceappmvvm.view_models

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.model.data_class.User
import com.example.ecommerceappmvvm.util.Constants.USER_COLLECTION
import com.example.ecommerceappmvvm.util.RegisterValidationState
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.getEmailValidationState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val mFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth,
    val mStorage : StorageReference,
    val mApp : Application
) : AndroidViewModel(mApp) {
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        mFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                viewModelScope.launch {
                    _user.emit(Resource.Success(user))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Failure(it.message))
                }
            }
    }

     fun updateUser(user : User , imageUri : Uri?) {

        val areInputsValid = (
                        getEmailValidationState(user.email) is RegisterValidationState.Success
                        && user.firstName.trim().isNotEmpty()
                        && user.lastName.trim().isNotEmpty()
                )

        if(!areInputsValid){
            viewModelScope.launch {
                _updateInfo.emit(Resource.Failure("check your inputs"))
            }
            return
        }
        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

       if(imageUri == null)
           saveUserInformation(user , true)
       else{
            saveUserInformationWithNewImage(user , imageUri)
       }
    }

    private fun saveUserInformationWithNewImage(user : User , imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imageBitmap =  MediaStore.Images.Media.getBitmap(
                    mApp.contentResolver , imageUri
                )

                val byteArrayOutputStream = ByteArrayOutputStream()

                imageBitmap.compress(Bitmap.CompressFormat.JPEG , 96 , byteArrayOutputStream)

                val imageByteArray = byteArrayOutputStream.toByteArray()

                val imageDirectory = mStorage.child("profileImages/").child("${mAuth.uid}/")
                    .child("${UUID.randomUUID()}")

                val result = imageDirectory.putBytes(imageByteArray).await()

                val imageUrl = result.storage.downloadUrl.await().toString()

                saveUserInformation(user.copy(imgPath = imageUrl) , false)

                _updateInfo.emit(Resource.Success(user))

            }catch (ex : Exception){
                _updateInfo.emit(Resource.Failure(ex.message))
            }
        }
    }

    private fun saveUserInformation(user: User, shouldGetOldImage: Boolean) {
         mFirestore.runTransaction { transaction ->
             val docRef = mFirestore.collection(USER_COLLECTION).document(mAuth.uid!!)
             if(shouldGetOldImage){
                 val currentUser = transaction.get(docRef).toObject(User::class.java)
                 val newUser = user.copy(imgPath = currentUser?.imgPath?: "" )
                 transaction.set(docRef,newUser)
             }else{
                 transaction.set(docRef,user)
             }
         }.addOnSuccessListener {
             viewModelScope.launch {
                 _updateInfo.emit(Resource.Success(user))
             }
         }.addOnFailureListener {
             viewModelScope.launch {
                 _updateInfo.emit(Resource.Failure(it.message))
             }
         }
    }


}