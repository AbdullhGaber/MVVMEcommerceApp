package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.model.data_class.Address
import com.example.ecommerceappmvvm.util.Constants
import com.example.ecommerceappmvvm.util.Constants.ADDRESS_COLLECTION
import com.example.ecommerceappmvvm.util.Constants.USER_COLLECTION
import com.example.ecommerceappmvvm.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    val mFireStore : FirebaseFirestore,
    val mAuth: FirebaseAuth
) : ViewModel() {
    private val _address = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    private val _addressError = MutableSharedFlow<String>()
    val addressError = _addressError.asSharedFlow()

    fun addAddress(address : Address){
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }

        if(isAddressValid(address)){
            mFireStore.collection(USER_COLLECTION).document(mAuth.uid!!).
            collection(ADDRESS_COLLECTION).document().set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Success(address))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _address.emit(Resource.Failure(it.message.toString()))
                    }
                }
        }else{
            viewModelScope.launch {
                _addressError.emit("All fields are required !")
            }
        }
    }

    private fun isAddressValid(address : Address) : Boolean{
        return address.addressTitle.trim().isNotEmpty()
                && address.fullName.trim().isNotEmpty()
                && address.city.trim().isNotEmpty()
                && address.state.trimIndent().isNotEmpty()
                && address.phone.trim().isNotEmpty()
                && address.street.trim().isNotEmpty()
    }
}