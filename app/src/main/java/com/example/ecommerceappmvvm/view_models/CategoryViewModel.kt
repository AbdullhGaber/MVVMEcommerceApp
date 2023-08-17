package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.model.data_class.Category
import com.example.ecommerceappmvvm.model.data_class.Product
import com.example.ecommerceappmvvm.util.Constants
import com.example.ecommerceappmvvm.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryViewModel (
    private val mFireStore : FirebaseFirestore,
    private val mCategory: Category
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts(){

        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        mFireStore.collection(Constants.PRODUCTS_COLLECTION)
            .whereEqualTo("category" , mCategory.category)
            .whereNotEqualTo("offerPercentage" , null).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(it.toObjects(Product::class.java)))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Failure(it.toString()))
                }
            }

    }

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        mFireStore.collection(Constants.PRODUCTS_COLLECTION).whereEqualTo("category" , mCategory.category)
            .whereEqualTo("offerPercentage" , null).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(it.toObjects(Product::class.java)))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Failure(it.toString()))
                }
            }

    }


}