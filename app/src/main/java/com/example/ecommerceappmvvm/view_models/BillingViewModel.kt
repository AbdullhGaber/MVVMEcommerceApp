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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    val mFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth
) : ViewModel() {
    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getAddresses()
    }

    private fun getAddresses() {
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }

        mFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).collection(ADDRESS_COLLECTION)
            .addSnapshotListener { value, error ->

                if(error != null) {
                    viewModelScope.launch { _address.emit(Resource.Failure(error.message)) }
                    return@addSnapshotListener
                }

                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch {
                    _address.emit(Resource.Success(addresses))
                }


            }
    }
}