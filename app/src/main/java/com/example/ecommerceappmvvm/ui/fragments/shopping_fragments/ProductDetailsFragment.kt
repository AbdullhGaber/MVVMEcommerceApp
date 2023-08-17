package com.example.ecommerceappmvvm.ui.fragments.shopping_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentProductDetailsBinding
import com.example.ecommerceappmvvm.model.data_class.CartProduct
import com.example.ecommerceappmvvm.ui.adapters.ColorsAdapter
import com.example.ecommerceappmvvm.ui.adapters.SizesAdapter
import com.example.ecommerceappmvvm.ui.adapters.ViewPagerImageAdapter
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.closeFragment
import com.example.ecommerceappmvvm.util.hideBottomNavigation
import com.example.ecommerceappmvvm.view_models.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private lateinit var mBinding : FragmentProductDetailsBinding
    private val mNavArgs by navArgs<ProductDetailsFragmentArgs>()
    private val mProduct by lazy { mNavArgs.product }
    private val mColorsAdapter by lazy { ColorsAdapter() }
    private val mSizesAdapter by lazy { SizesAdapter() }
    private val mViewPagerAdapter by lazy { ViewPagerImageAdapter() }
    private val mViewModel by viewModels<ProductDetailsViewModel>()
    private var mSelectedColor : Int? = null
    private var mSelectedSize : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation()
        mBinding = FragmentProductDetailsBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpColorsAdapter()
        setUpSizesAdapter()
        setUpViewPagerAdapter()

        bindUI()

        setOnImageCloseIconClickListener()

        mColorsAdapter.onItemClick = {
            mSelectedColor = it
        }

        mSizesAdapter.onItemClick = {
            mSelectedSize = it
        }

        onAddToCartButtonClick()

        observeOnAddToCartButtonClick()

    }

    private fun observeOnAddToCartButtonClick() {
        lifecycleScope.launchWhenStarted {
            mViewModel.addToCart.collectLatest{
                when(it){
                    is Resource.Loading -> {
                       mBinding.btnAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        mBinding.btnAddToCart.revertAnimation()
                        Toast.makeText(requireContext() , "Product added successfully to your cart",
                            Toast.LENGTH_LONG).show()

                        mBinding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.black))

                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext() , it.message,
                            Toast.LENGTH_LONG).show()

                        mBinding.btnAddToCart.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onAddToCartButtonClick() {
        mBinding.btnAddToCart.setOnClickListener{
            mViewModel.addUpdateProductInCart(CartProduct(mProduct , 1 , mSelectedColor , mSelectedSize))
        }
    }

    private fun setOnImageCloseIconClickListener() {
        mBinding.closeProductIcon.setOnClickListener {
            closeFragment()
        }
    }

    private fun bindUI() {
        mBinding.apply {
            productNameTv.text = mProduct.name
            productPriceTv.text = "$ ${mProduct.price}"
            productDescriptionTv.text = mProduct.description

            if(mProduct.colors.isNullOrEmpty())
                productColorsTv.visibility = View.GONE

            if(mProduct.sizes.isNullOrEmpty())
                productSizeTv.visibility = View.GONE
        }

        mViewPagerAdapter.differ.submitList(mProduct.images)
        mColorsAdapter.differ.submitList(mProduct.colors)
        mSizesAdapter.differ.submitList(mProduct.sizes)
    }

    private fun setUpViewPagerAdapter() {
      mBinding.viewPagerProductsImages.apply {
          adapter = mViewPagerAdapter
          orientation = ViewPager2.ORIENTATION_HORIZONTAL
      }
    }

    private fun setUpSizesAdapter() {
        mBinding.sizeRv.apply {
            adapter = mSizesAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
        }
    }

    private fun setUpColorsAdapter() {
        mBinding.colorRv.apply {
            adapter = mColorsAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
        }
    }
}