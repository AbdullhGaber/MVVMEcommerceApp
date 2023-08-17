package com.example.ecommerceappmvvm.ui.fragments.shopping_fragments

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentBillingBinding
import com.example.ecommerceappmvvm.model.data_class.Address
import com.example.ecommerceappmvvm.model.data_class.CartProduct
import com.example.ecommerceappmvvm.model.data_class.order.Order
import com.example.ecommerceappmvvm.model.data_class.order.OrderStatus
import com.example.ecommerceappmvvm.ui.adapters.AddressAdapter
import com.example.ecommerceappmvvm.ui.adapters.BillingProductAdapter
import com.example.ecommerceappmvvm.util.HorizntallItemDecoration
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.closeFragment
import com.example.ecommerceappmvvm.view_models.BillingViewModel
import com.example.ecommerceappmvvm.view_models.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var mBinding : FragmentBillingBinding
    private val mNavArgs by navArgs<BillingFragmentArgs>()
    private var mTotalPrice : Float = 0f
    private lateinit var mProducts : Array<CartProduct>
    private var mSelectedAddress : Address? = null
    private val mAddressAdapter by lazy { AddressAdapter() }
    private val mBillingAdapter by lazy { BillingProductAdapter() }
    private val mBillingViewModel by viewModels<BillingViewModel>()
    private val mOrderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeOnUserAddresses()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding    = FragmentBillingBinding.inflate(inflater , container , false)
        mProducts   = mNavArgs.products
        mTotalPrice = mNavArgs.totalPrice
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpAddressAdapterRV()
        setUpBillingAdapterRV()
        if(mNavArgs.products.isEmpty()){
            mBinding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
            mAddressAdapter.onItemClick = {
                val b = Bundle().apply { putParcelable("address" , it) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment,b)
            }
        }
        setTotalPriceText()
        setOnBillingProductClickListener()
        setOnAddAddressIconClickListener()
        setOnBillingCloseIconListener()
        setOnPlaceOrderButtonClickListener()
        setOnAddressClickListener()
        observeOnPlacingOrder()
    }

    private fun observeOnPlacingOrder() {
        lifecycleScope.launchWhenStarted {
            mOrderViewModel.order.collect{
                when(it){
                    is Resource.Loading -> {
                       mBinding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                       mBinding.buttonPlaceOrder.revertAnimation()
                       Snackbar.make(requireView() , "Your order was placed successfully" , Snackbar.LENGTH_LONG)
                           .show()

                      closeFragment()
                    }

                    is Resource.Failure -> {
                       Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }

                   else -> Unit
               }
           }
       }
    }

    private fun showOrderConfirmationDialog(){
        val alertDialog = AlertDialog.Builder(requireContext()).apply {

            setTitle("Place your order")
            setMessage("Do you want to place these products to your order ?")
            setNegativeButton("Cancel"){dialog , _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){dialog , _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    mTotalPrice,
                    mProducts.toList(),
                    mSelectedAddress!!
                )

                mOrderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }

        alertDialog.create()
        alertDialog.show()
    }


    private fun setOnPlaceOrderButtonClickListener() {
        mBinding.buttonPlaceOrder.setOnClickListener {
            if(mSelectedAddress == null) {
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            showOrderConfirmationDialog()
        }
    }

    private fun setOnAddressClickListener() {
        if(mProducts.isNotEmpty()){
            mAddressAdapter.onItemClick = {
                mSelectedAddress = it
            }
        }
    }

    private fun setOnBillingCloseIconListener() {
        mBinding.imageCloseBilling.setOnClickListener {
            closeFragment()
        }
    }

    private fun setOnAddAddressIconClickListener() {
        mBinding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }
    }

    private fun setOnBillingProductClickListener() {
        mBillingAdapter.onItemClick = {
            val b = Bundle().apply { putParcelable("product" , it.product) }
            findNavController().navigate(R.id.action_billingFragment_to_productDetailsFragment , b)
        }
    }

    private fun setTotalPriceText() {
        mBinding.tvTotalPrice.text = mTotalPrice.toString()
    }


    private fun observeOnUserAddresses(){
        lifecycleScope.launchWhenStarted {
            mBillingViewModel.address.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        mBinding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        mBinding.progressbarAddress.visibility = View.GONE
                        mAddressAdapter.differ.submitList(it.data)
                    }

                    is Resource.Failure -> {
                        mBinding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }



    private fun setUpAddressAdapterRV() {
        mBinding.rvAddress.apply {
            adapter = mAddressAdapter
            layoutManager = LinearLayoutManager(requireContext() , RecyclerView.HORIZONTAL , false)
            addItemDecoration(HorizntallItemDecoration())
        }
    }

    private fun setUpBillingAdapterRV(){
        mBinding.rvProducts.apply {
            adapter = mBillingAdapter
            layoutManager = LinearLayoutManager(requireContext() , RecyclerView.HORIZONTAL , false)
            addItemDecoration(HorizntallItemDecoration())
        }

        mBillingAdapter.differ.submitList(mProducts.toList())
    }
}