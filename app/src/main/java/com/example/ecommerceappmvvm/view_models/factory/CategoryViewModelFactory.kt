package com.example.ecommerceappmvvm.view_models.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerceappmvvm.model.data_class.Category
import com.example.ecommerceappmvvm.view_models.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModelFactory(
    val firestore: FirebaseFirestore,
    val category : Category
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore,category) as T
    }
}