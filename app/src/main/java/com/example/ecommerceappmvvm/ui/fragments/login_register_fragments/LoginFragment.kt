package com.example.ecommerceappmvvm.ui.fragments.login_register_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentLoginBinding
import com.example.ecommerceappmvvm.databinding.ResetPasswordDialogBinding
import com.example.ecommerceappmvvm.ui.activities.ShoppingActivity
import com.example.ecommerceappmvvm.util.RegisterValidationState
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.view_models.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var mBinding : FragmentLoginBinding
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLoginBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onRegisterTextClick()
        onLoginButtonClick()
        onResetPasswordTextClick()
        observeLoginState()
        observeOnLoginValidation()
        observeResetPasswordState()
    }

    private fun observeOnLoginValidation() {
        lifecycleScope.launchWhenStarted {
            mViewModel.loginStateChannel.collect{
                state ->
                        if(state.email is RegisterValidationState.Failed){
                            withContext(Dispatchers.Main){
                                mBinding.emailEt.apply {
                                    requestFocus()
                                    error = state.email.message
                                }
                            }
                        }

                        if(state.password is RegisterValidationState.Failed){
                            withContext(Dispatchers.Main){
                                mBinding.passwordEt.apply {
                                    requestFocus()
                                    error = state.password.message
                                }
                            }
                        }
                }
            }
        }

    private fun observeResetPasswordState() {
        lifecycleScope.launchWhenStarted {
            mViewModel.resetPasswordEmail.collect{
                    state ->
                when(state){
                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                       Snackbar.make(requireContext() , mBinding.root,"Your reset link has been sent to your email" , Snackbar.LENGTH_LONG).show()
                    }

                    is Resource.Failure -> {
                        Snackbar.make(requireContext() , mBinding.root, "Error : " + state.message.toString() , Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onRegisterTextClick() {
        mBinding.registerTv.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun onLoginButtonClick() {
        mBinding.apply {
            btnLogin.setOnClickListener{
                mViewModel.login(getUserEmail() , getUserPassword())
            }
        }
    }

    private fun observeLoginState(){
        lifecycleScope.launchWhenStarted {
            mViewModel.loginStateFlow.collect{
                state ->
                when(state){
                    is Resource.Loading -> mBinding.btnLogin.startAnimation()

                    is Resource.Success -> {
                        mBinding.btnLogin.revertAnimation()
                        Intent(requireActivity() , ShoppingActivity::class.java).also{
                            it.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
                            startActivity(it)
                        }
                    }

                    is Resource.Failure -> {
                        mBinding.btnLogin.revertAnimation()
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun getUserPassword() = mBinding.passwordEt.text.toString()

    private fun getUserEmail() = mBinding.emailEt.text.toString().trim()

    private fun onResetPasswordTextClick(){
        mBinding.forgotPasswordTv.setOnClickListener {
            setUpResetPasswordBottomSheetFragment()
        }
    }

    private fun setUpResetPasswordBottomSheetFragment(){
        val dialog = BottomSheetDialog(requireContext() , R.style.DialogStyle )

        val resetPasswordBinding = ResetPasswordDialogBinding.inflate(layoutInflater)

        dialog.apply {
            setContentView(resetPasswordBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            show()
        }

        resetPasswordBinding.apply {
            cancelPasswordRestBtn.setOnClickListener {
                dialog.dismiss()
            }

            sendPasswordRestBtn.setOnClickListener {
                mViewModel.resetPassword(resetPasswordBinding.restPasswordEt.text.toString())
                dialog.dismiss()
            }
        }

    }
}