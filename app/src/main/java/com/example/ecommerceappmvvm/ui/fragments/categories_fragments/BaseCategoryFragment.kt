package com.example.ecommerceappmvvm.ui.fragments.categories_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentBaseCategoryBinding
import com.example.ecommerceappmvvm.model.data_class.Product
import com.example.ecommerceappmvvm.ui.adapters.BestProductsAdapter

open class BaseCategoryFragment : Fragment() {
    
    protected lateinit var mBinding : FragmentBaseCategoryBinding
    protected val mOfferAdapter by lazy { BestProductsAdapter() }
    protected val mBestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBaseCategoryBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOfferRv()
        setUpBestProductsRv()

        onOfferRvScroll()

        onNestedLayoutScroll()

        setOnBestProductsItemClick()

        setOnOfferProductsItemClick()
    }

    private fun setOnBestProductsItemClick() {
        mBestProductsAdapter.mOnItemClick = {
            navigateToProductDetailsFragment(it)
        }
    }

  private fun setOnOfferProductsItemClick() {
        mOfferAdapter.mOnItemClick = {
            navigateToProductDetailsFragment(it)
        }
    }

    private fun navigateToProductDetailsFragment(product: Product) {
        val bundle = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
    }

    protected fun onNestedLayoutScroll() {
        mBinding.baseCategoryNestedScrollLayout.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (v.getChildAt(0).bottom <= v.height + scrollY) {
                    onBestProductsPagingRequest()
                }
            }
        )
    }

    protected fun onOfferRvScroll() {
        mBinding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferPagingRequest()
                }

            }
        })
    }

    open fun onOfferPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }

    protected fun showBestProductsProgressbar(){
        mBinding.bestProductsProgressbar.visibility = View.VISIBLE
    }

    protected fun hideBestProductsProgressbar(){
        mBinding.bestProductsProgressbar.visibility = View.GONE
    }

    protected fun showOfferProductsProgressbar(){
        mBinding.offerProductsProgressbar.visibility = View.VISIBLE
    }

    protected fun hideOfferProductsProgressbar(){
        mBinding.offerProductsProgressbar.visibility = View.GONE
    }

    private fun setUpOfferRv(){
        mBinding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL ,false)
            adapter = mOfferAdapter
        }
    }

    private fun setUpBestProductsRv(){
        mBinding.bestProductsRv.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL ,false)
            adapter = mBestProductsAdapter
        }
    }
}

