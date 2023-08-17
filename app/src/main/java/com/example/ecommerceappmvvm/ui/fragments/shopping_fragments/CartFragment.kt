package com.example.ecommerceappmvvm.ui.fragments.shopping_fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentCartBinding
import com.example.ecommerceappmvvm.firebase.FirebaseCommon
import com.example.ecommerceappmvvm.ui.adapters.CartProductAdapter
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.VerticalItemDecoration
import com.example.ecommerceappmvvm.util.closeFragment
import com.example.ecommerceappmvvm.view_models.CartProductViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment() {

    private lateinit var mBinding : FragmentCartBinding
    private var mTotalPrice : Float = 0f
    private lateinit var mCartAdapter : CartProductAdapter
    private val mViewModel by activityViewModels<CartProductViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCartBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpCartRV()

        observeOnCartProducts()

        observeOnTotalProductsPrice()

        observeOnLastProductQuantity()

        setOnPlusIconClick()
        setOnMinusIconClick()
        setOnItemClickListener()
        setOnImageCloseIconClickListener()
        setOnCheckoutButtonClickListener()


    }

    private fun setOnCheckoutButtonClickListener() {
        mBinding.buttonCheckout.setOnClickListener {
           val action = CartFragmentDirections.actionCartFragmentToBillingFragment(mTotalPrice , mCartAdapter.diff.currentList.toTypedArray())
           findNavController().navigate(action)
        }
    }

    private fun setOnImageCloseIconClickListener() {
        mBinding.imageCloseCart.setOnClickListener {
            closeFragment()
        }
    }

    private fun setOnItemClickListener() {
        mCartAdapter.mOnItemClick = {
            val b = Bundle().apply { putParcelable("product" , it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment , b)
        }
    }

    private fun observeOnLastProductQuantity() {
        lifecycleScope.launchWhenStarted {
            mViewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {

                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart ?")
                    setNegativeButton("Cancel"){dialog , _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){dialog , _ ->
                        mViewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }

                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    private fun setOnPlusIconClick() {
        mCartAdapter.onPlusClick = {
            mViewModel.changeQuantity(it , FirebaseCommon.QuantityChange.INCREASE)
        }
    }

    private fun setOnMinusIconClick() {
        mCartAdapter.onMinusClick = {
            mViewModel.changeQuantity(it , FirebaseCommon.QuantityChange.DECREASE)
        }
    }

    private fun observeOnTotalProductsPrice() {
        lifecycleScope.launchWhenStarted {
            mViewModel.totalProductPrice.collectLatest {
                mBinding.tvTotalPrice.text = "$ $it"
                mTotalPrice = it
            }
        }
    }

    private fun observeOnCartProducts() {
        lifecycleScope.launchWhenStarted {
            mViewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        mBinding.progressbarCart.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                       mBinding.progressbarCart.visibility = View.GONE
                        if(it.data!!.isNotEmpty()){
                            hideEmptyCartView()
                            showOtherViews()
                            mCartAdapter.diff.submitList(it.data)
                        }else{
                            showEmptyCartView()
                            hideOtherViews()
                        }
                    }

                    is Resource.Failure -> {
                        mBinding.progressbarCart.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun hideEmptyCartView() {
        mBinding.layoutCarEmpty.visibility = View.GONE
    }

    private fun showEmptyCartView() {
        mBinding.layoutCarEmpty.visibility = View.VISIBLE
    }

    private fun showOtherViews(){
        mBinding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews(){
        mBinding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun setUpCartRV() {
        mCartAdapter = CartProductAdapter()
        mBinding.rvCart.apply {
            adapter = mCartAdapter
            layoutManager = LinearLayoutManager(requireContext() , RecyclerView.VERTICAL , false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}