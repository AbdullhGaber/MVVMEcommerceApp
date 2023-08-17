package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.model.data_class.order.Order
import com.example.ecommerceappmvvm.util.Constants
import com.example.ecommerceappmvvm.util.Constants.CART_COLLECTION
import com.example.ecommerceappmvvm.util.Constants.ORDER_COLLECTION
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
class OrderViewModel @Inject constructor(
    val mFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order : Order){
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        mFirestore.runBatch { batch ->

            //Add order to user-orders collection
            mFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).
            collection(ORDER_COLLECTION).document().set(order)

            //Add order to orders collection
            mFirestore.collection(ORDER_COLLECTION).document().set(order)

            //Delete all cart products from user collection
            mFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).collection(CART_COLLECTION).get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Failure(it.message))
            }
        }
    }
}