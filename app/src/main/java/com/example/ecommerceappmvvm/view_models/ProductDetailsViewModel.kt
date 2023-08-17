package com.example.ecommerceappmvvm.view_models


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.firebase.FirebaseCommon
import com.example.ecommerceappmvvm.model.data_class.CartProduct
import com.example.ecommerceappmvvm.util.Constants.CART_COLLECTION
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
class ProductDetailsViewModel @Inject constructor(
    val mFirebaseFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth,
    val mFirestoreCommon: FirebaseCommon
) : ViewModel() {
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct){

        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }

        mFirebaseFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).collection(CART_COLLECTION)
            .whereEqualTo("product.id" , cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                   docList ->

                    try{
                        val firebaseCartProduct = docList.first().toObject(cartProduct::class.java)

                        if(isSelectedColorSizeNotSame(firebaseCartProduct , cartProduct))
                            addNewProduct(cartProduct)
                        else {
                            val documentId = it.first().id
                            increaseProductQuantity(documentId, cartProduct)
                        }

                        return@addOnSuccessListener
                    }catch (e : NoSuchElementException){
                        addNewProduct(cartProduct)
                        return@addOnSuccessListener
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Failure(it.message.toString()))
                }
            }
    }

    private fun isSelectedColorSizeNotSame(
        firebaseCartProduct : CartProduct?,
        cartProduct: CartProduct
    ) = firebaseCartProduct?.selectedColor != cartProduct.selectedColor ||
        firebaseCartProduct?.selectedSize != cartProduct.selectedSize

    private fun addNewProduct(cartProduct: CartProduct){
        mFirestoreCommon.addProductToCart(cartProduct){
            addedProduct , e ->
            if(e == null)
                viewModelScope.launch {
                    _addToCart.emit(Resource.Success(addedProduct))
                }
            else
                viewModelScope.launch {
                    _addToCart.emit(Resource.Failure(e.message.toString()))
                }
        }
    }

    private fun increaseProductQuantity(documentId: String, cartProduct: CartProduct){

        mFirestoreCommon.increaseProductQuantity(documentId){
            _ , e ->
            if(e == null)
                viewModelScope.launch {
                    _addToCart.emit(Resource.Success(cartProduct))
                }
            else
                viewModelScope.launch {
                    _addToCart.emit(Resource.Failure(e.message.toString()))
                }
        }
    }
}