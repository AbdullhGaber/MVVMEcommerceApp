package com.example.ecommerceappmvvm.ui.fragments.settings_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentOrdersBinding
import com.example.ecommerceappmvvm.ui.adapters.AllOrdersRVAdapter
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.closeFragment
import com.example.ecommerceappmvvm.view_models.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {
    private lateinit var mBinding : FragmentOrdersBinding
    private val mViewModel by viewModels<AllOrdersViewModel>()
    private val mOrdersAdapter by lazy { AllOrdersRVAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOrdersBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOrdersRVAdapter()
        setOnCloseIconClickListener()
        observeOnOrders()
    }

    private fun setOnCloseIconClickListener() {
        mBinding.imageCloseOrders.setOnClickListener {
            closeFragment()
        }

    }

    private fun observeOnOrders() {
        lifecycleScope.launchWhenStarted {
            mViewModel.allOrders.collectLatest{
                when(it){
                    is Resource.Loading -> {
                        mBinding.progressbarAllOrders.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        mBinding.progressbarAllOrders.visibility = View.GONE
                        mOrdersAdapter.differ.submitList(it.data)
                        if(it.data.isNullOrEmpty()){
                            mBinding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Failure -> {
                        mBinding.progressbarAllOrders.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpOrdersRVAdapter() {
        mBinding.rvAllOrders.apply {
            adapter = mOrdersAdapter.apply {
                onItemClick = {
                val b = Bundle().apply { putParcelable("order" , it) }
                findNavController().navigate(R.id.action_allOrdersFragment_to_orderDetailsFragment , b)
            } }
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL , false)
        }
    }
}