package com.example.ecommerceappmvvm.ui.fragments.login_register_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentRegisterBinding
import com.example.ecommerceappmvvm.model.data_class.User
import com.example.ecommerceappmvvm.util.RegisterValidationState
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.view_models.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var mBinding : FragmentRegisterBinding
    private val mViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRegisterBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onLoginTextClick()
        onRegisterButtonClick()

        observeOnRegisterState()
        observeOnRegisterValidationState()
    }

    private fun onLoginTextClick() {
       mBinding.loginTv.setOnClickListener{
           findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
       }
    }

    private fun observeOnRegisterValidationState(){
       lifecycleScope.launchWhenStarted {
          mViewModel.registerValidationState.collect{
              validationState ->

              if(validationState.email is RegisterValidationState.Failed){
                  withContext(Dispatchers.Main){
                      mBinding.emailEt.apply {
                          requestFocus()
                          error = validationState.email.message
                      }
                  }
              }

              if(validationState.password is RegisterValidationState.Failed){
                  withContext(Dispatchers.Main){
                      mBinding.passwordEtEt.apply {
                          requestFocus()
                          error = validationState.password.message
                      }
                  }
              }
          }
       }

    }

    private fun observeOnRegisterState() {
        lifecycleScope.launchWhenResumed {
            mViewModel.registerStateFlow.collect{
                 register ->

                 when(register){
                     is Resource.Loading -> {
                         mBinding.btnRegister.startAnimation()
                         Log.v("TAG" , "registration in progress")
                     }

                     is Resource.Success -> {
                         mBinding.btnRegister.revertAnimation()
                         Log.v("TAG" , register.data.toString())
                     }

                     is Resource.Failure -> {
                         mBinding.btnRegister.revertAnimation()
                         Toast.makeText(requireContext() , register.message , Toast.LENGTH_LONG ).show()
                     }

                     else -> Log.v("TAG" , "registration not started")
                 }
           }
       }
    }

    private fun onRegisterButtonClick() {
       mBinding.apply {
          btnRegister.setOnClickListener{
              mViewModel.createAccount(getUserData() , getUserPassword())
          }
       }

    }

    private fun getUserPassword(): String {
        return mBinding.passwordEtEt.text.toString()
    }

    private fun getUserData(): User {
        return User(
             mBinding.fNameEt.text.toString().trim(),
             mBinding.lNameEt.text.toString().trim(),
             mBinding.emailEt.text.toString().trim()
        )
    }
}