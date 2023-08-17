package com.example.ecommerceappmvvm.ui.fragments.shopping_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.ecommerceappmvvm.databinding.FragmentAddressBinding
import com.example.ecommerceappmvvm.model.data_class.Address
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.closeFragment
import com.example.ecommerceappmvvm.view_models.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var mBinding : FragmentAddressBinding
    val mViewModel by viewModels<AddressViewModel>()
    val mNavArgs by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeOnSendingAddress()
        observeOnAddressDataValidation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        mBinding = FragmentAddressBinding.inflate(inflater , container ,false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mBinding.buttonSave.setOnClickListener {
            saveUserAddress()
        }

        if(mNavArgs.address != null){
            val address = mNavArgs.address
            mBinding.apply {
                edAddressTitle.setText(address?.addressTitle)
                edFullName.setText(address?.fullName)
                edStreet.setText(address?.street)
                edPhone.setText(address?.phone)
                edCity.setText(address?.city)
                edState.setText(address?.state)
            }
        }

        setOnCloseIconClick()
    }

    private fun setOnCloseIconClick() {
        mBinding.imageAddressClose.setOnClickListener {
            closeFragment()
        }
    }

    private fun saveUserAddress() {
        mBinding.apply {
            val addressTitle = edAddressTitle.text.toString()
            val fullName = edFullName.text.toString()
            val street = edStreet.text.toString()
            val phone = edPhone.text.toString()
            val city = edCity.text.toString()
            val state = edState.text.toString()

            val address = Address(addressTitle, fullName, street, phone, city, state)

            mViewModel.addAddress(address)
        }
    }

    private fun observeOnAddressDataValidation() {
        lifecycleScope.launchWhenStarted {
            mViewModel.addressError.collectLatest {
                Toast.makeText(requireContext() , it , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeOnSendingAddress() {
        lifecycleScope.launchWhenStarted {
            mViewModel.address.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        mBinding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        mBinding.progressbarAddress.visibility = View.INVISIBLE
                        closeFragment()
                    }

                    is Resource.Failure -> {
                        mBinding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

}