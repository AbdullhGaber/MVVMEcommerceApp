package com.example.ecommerceappmvvm.ui.fragments.categories_fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceappmvvm.model.data_class.Category
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.view_models.CategoryViewModel
import com.example.ecommerceappmvvm.view_models.factory.CategoryViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FurnitureFragment : BaseCategoryFragment() {

    @Inject
    lateinit var mFirestore : FirebaseFirestore

    private val mViewModel by viewModels<CategoryViewModel> {
        CategoryViewModelFactory(mFirestore , Category.Furniture)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOnBestProducts()

        observeOnOfferProducts()
    }

    private fun observeOnOfferProducts() {
        lifecycleScope.launch {
            mViewModel.offerProducts.collect {
                when (it) {
                    is Resource.Loading -> showOfferProductsProgressbar()

                    is Resource.Failure -> {
                        hideOfferProductsProgressbar()
                        Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_LONG).show()
                        Log.v("TAG", it.message.toString())
                    }

                    is Resource.Success -> {
                        hideOfferProductsProgressbar()
                        mOfferAdapter.differ.submitList(it.data)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun observeOnBestProducts() {
        lifecycleScope.launch {
            mViewModel.bestProducts.collect {
                when (it) {
                    is Resource.Loading -> showBestProductsProgressbar()

                    is Resource.Failure -> {
                        hideBestProductsProgressbar()
                        Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_LONG).show()
                        Log.v("TAG", it.message.toString())
                    }

                    is Resource.Success -> {
                        hideBestProductsProgressbar()
                        mBestProductsAdapter.differ.submitList(it.data)
                    }

                    else -> Unit
                }
            }
        }
    }
}

