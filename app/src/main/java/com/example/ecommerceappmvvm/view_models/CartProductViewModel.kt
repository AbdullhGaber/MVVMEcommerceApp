package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.firebase.FirebaseCommon
import com.example.ecommerceappmvvm.firebase.FirebaseCommon.QuantityChange.DECREASE
import com.example.ecommerceappmvvm.firebase.FirebaseCommon.QuantityChange.INCREASE
import com.example.ecommerceappmvvm.helper.getProductPrice
import com.example.ecommerceappmvvm.model.data_class.CartProduct
import com.example.ecommerceappmvvm.util.Constants
import com.example.ecommerceappmvvm.util.Constants.CART_COLLECTION
import com.example.ecommerceappmvvm.util.Constants.USER_COLLECTION
import com.example.ecommerceappmvvm.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CartProductViewModel @Inject constructor(
    val mFirebaseFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth,
    val mFirestoreCommon: FirebaseCommon
) : ViewModel() {
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    val totalProductPrice = cartProducts.map {
        when(it){
            is Resource.Success -> {
                calculatePrices(it.data!!)
            }

            else -> 0f
        }
    }



    fun deleteCartProduct(cartProduct: CartProduct) {
        val idx = cartProducts.value.data?.indexOf(cartProduct)
        if (idx != null && idx != -1) {
            val documentId = cartDocuments[idx].id
            mFirebaseFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).collection(
                CART_COLLECTION).document(documentId).delete()
        }
    }
    private fun calculatePrices(products : List<CartProduct>) : Float {
      return products.sumOf {
                (it.product.offerPercentage.getProductPrice(it.product.price) * it.quantity).toDouble()
        }.toFloat()
    }

    private var cartDocuments = emptyList<DocumentSnapshot>()
    init{
        getCartProduct()
    }

    private fun getCartProduct(){
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        mFirebaseFirestore.collection(USER_COLLECTION).document(mAuth.uid!!).
        collection(CART_COLLECTION).addSnapshotListener{
            data, error ->

            if(error != null || data == null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Failure(error?.message.toString()))
                }
            }else{
                cartDocuments = data.documents
                val cartProduct = data.toObjects(CartProduct::class.java)
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Success(cartProduct))
                }
            }
        }
    }

    fun changeQuantity(cartProduct: CartProduct ,
                       qtyChanging : FirebaseCommon.QuantityChange
    ){

       val idx = cartProducts.value.data?.indexOf(cartProduct)
        if(idx != null && idx != -1 ){
            val documentId = cartDocuments[idx].id

            when(qtyChanging){
                INCREASE -> {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }
                DECREASE -> {
                    if(cartProduct.quantity == 1 ){
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    fun increaseQuantity(documentId : String){
        mFirestoreCommon.increaseProductQuantity(documentId){ _ , error ->
            if(error != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Failure(error.message.toString()))
                }
            }
        }
    }

    fun decreaseQuantity(documentId : String){
        mFirestoreCommon.decreaseProductQuantity(documentId){
                _ , error ->
            if(error != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Failure(error.message.toString()))
                }
            }
        }
    }
}