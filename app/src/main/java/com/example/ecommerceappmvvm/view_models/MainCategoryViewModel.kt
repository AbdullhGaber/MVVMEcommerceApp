package com.example.ecommerceappmvvm.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceappmvvm.model.data_class.Product
import com.example.ecommerceappmvvm.util.Constants
import com.example.ecommerceappmvvm.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    val mFirestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProductsState = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProductsState : StateFlow<Resource<List<Product>>> = _specialProductsState

    private val mPagingInfo = PagingInfo()


    private val _bestProductsState = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProductsState : StateFlow<Resource<List<Product>>> = _bestProductsState

    private val _bestDealsState = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsState : StateFlow<Resource<List<Product>>> = _bestDealsState

    init {
        fetchSpecialProducts()
        fetchBestProducts()
        fetchBestDeals()
    }

    fun fetchSpecialProducts(){

        viewModelScope.launch {
            _specialProductsState.emit(Resource.Loading())
        }

        mFirestore.collection(Constants.PRODUCTS_COLLECTION).whereEqualTo("category","Special Products").get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _specialProductsState.emit(Resource.Success(it.toObjects(Product::class.java)))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _specialProductsState.emit(Resource.Failure(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts(){
        if(!mPagingInfo.isEndReached){

           viewModelScope.launch {
               _bestProductsState.emit(Resource.Loading())
           }

           mFirestore.collection(Constants.PRODUCTS_COLLECTION).
           whereEqualTo("category" , "Best Products").limit(mPagingInfo.bestProductsPage * 10)
               .get()
               .addOnSuccessListener {
                   val products = it.toObjects(Product::class.java)
                   mPagingInfo.isEndReached = (mPagingInfo.oldProducts == products)
                   mPagingInfo.oldProducts = products

                   viewModelScope.launch {
                       _bestProductsState.emit(Resource.Success(products))
                   }

                   mPagingInfo.bestProductsPage++

               }.addOnFailureListener {
                   viewModelScope.launch {
                       _bestProductsState.emit(Resource.Failure(it.toString()))
                   }
               }
        }
    }

    fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDealsState.emit(Resource.Loading())
        }

        mFirestore.collection(Constants.PRODUCTS_COLLECTION).whereEqualTo("category","Best Deals").get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _bestDealsState.emit(Resource.Success(it.toObjects(Product::class.java)))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsState.emit(Resource.Failure(it.toString()))
                }
            }
    }

}

internal data class PagingInfo(
    var bestProductsPage : Long = 1,
    var oldProducts : List<Product> = emptyList(),
    var isEndReached : Boolean = false
)
