package com.example.ecommerceappmvvm.ui.fragments.categories_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentMainCategoryBinding
import com.example.ecommerceappmvvm.model.data_class.Product
import com.example.ecommerceappmvvm.ui.adapters.BestDealsAdapter
import com.example.ecommerceappmvvm.ui.adapters.BestProductsAdapter
import com.example.ecommerceappmvvm.ui.adapters.SpecialProductsRVAdapter
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.view_models.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {

    private lateinit var mBinding : FragmentMainCategoryBinding
    private lateinit var mSpecialProductsAdapter : SpecialProductsRVAdapter
    private lateinit var mBestDealsAdapter: BestDealsAdapter
    private lateinit var mBestProductsAdapter: BestProductsAdapter
    private val mViewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainCategoryBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductsRV()
        observeOnSpecialProducts()

        setUpBestProductsRV()
        observeOnBestProducts()

        setUpBestDealsRV()
        observeOnBestDeals()

        fetchBestProductsOnScroll()

        setOnSpecialProductsItemClick()
        setOnBestProductsItemClick()
        setOnBestDealsItemClick()

    }

    private fun setOnBestDealsItemClick() {
        mBestDealsAdapter.mOnItemClick = {
            navigateToProductDetailsFragment(it)
        }
    }

    private fun setOnBestProductsItemClick() {
        mBestProductsAdapter.mOnItemClick = {
            navigateToProductDetailsFragment(it)
        }
    }

    private fun setOnSpecialProductsItemClick() {
        mSpecialProductsAdapter.mOnItemClick = {
            navigateToProductDetailsFragment(it)
        }
    }

    private fun navigateToProductDetailsFragment(product: Product) {
        val bundle = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
    }

    private fun observeOnSpecialProducts() {
        lifecycleScope.launch {

            mViewModel.specialProductsState.collectLatest {
                when (it) {
                    is Resource.Loading -> showProgressbar()

                    is Resource.Failure -> {
                        hideProgressbar()
                        Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_LONG).show()
                        Log.v("TAG" , it.message.toString())
                    }

                    is Resource.Success -> {
                        hideProgressbar()
                        mSpecialProductsAdapter.diff.submitList(it.data)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun observeOnBestProducts(){
        lifecycleScope.launch{
            mViewModel.bestProductsState.collectLatest {
                when(it){
                    is Resource.Loading -> mBinding.bestProductsProgressbar.visibility = View.VISIBLE

                    is Resource.Success -> {
                        mBinding.bestProductsProgressbar.visibility = View.GONE
                        mBestProductsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Failure -> {
                        mBinding.bestProductsProgressbar.visibility = View.GONE
                        Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_LONG).show()
                        Log.v("TAG" , it.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun observeOnBestDeals(){
        lifecycleScope.launch{
            mViewModel.bestDealsState.collectLatest {
                when(it){
                    is Resource.Loading -> showProgressbar()

                    is Resource.Success -> {
                        hideProgressbar()
                        mBestDealsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Failure -> {
                        hideProgressbar()
                        Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_LONG).show()
                        Log.v("TAG" , it.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchBestProductsOnScroll(){
        mBinding.nestedScrollLayout.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener{
                v , _ , scrollY,_,_ ->
                if(v.getChildAt(0).bottom <= v.height + scrollY){
                    mViewModel.fetchBestProducts()
                }
            }
        )
    }

    private fun showProgressbar() {
        mBinding.progressbar.visibility = View.VISIBLE
    }

    private fun hideProgressbar() {
        mBinding.progressbar.visibility = View.GONE
    }

    private fun setUpSpecialProductsRV() {
        mSpecialProductsAdapter = SpecialProductsRVAdapter()

        mBinding.specialProductsRv.apply {
            adapter = mSpecialProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setUpBestProductsRV() {
        mBestProductsAdapter = BestProductsAdapter()

        mBinding.bestProductsRv.apply {
            adapter = mBestProductsAdapter
            layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL ,false)
        }
    }

    private fun setUpBestDealsRV() {
        mBestDealsAdapter = BestDealsAdapter()

        mBinding.bestDealsRv.apply {
            adapter = mBestDealsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}

