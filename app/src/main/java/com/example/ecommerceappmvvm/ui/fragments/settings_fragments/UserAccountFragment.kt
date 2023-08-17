package com.example.ecommerceappmvvm.ui.fragments.settings_fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.databinding.FragmentUserAccountBinding
import com.example.ecommerceappmvvm.model.data_class.User
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.closeFragment
import com.example.ecommerceappmvvm.view_models.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var mBinding : FragmentUserAccountBinding
    private val mViewModel by viewModels<UserViewModel>()
    private var mImageUri : Uri? = null
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            mImageUri = it.data?.data
            Glide.with(this@UserAccountFragment).load(mImageUri).error(ColorDrawable(Color.BLACK)).into(mBinding.imageUser)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserAccountBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeOnUserData()
        observeOnUserUpdate()
        onCloseIconClickListener()
        onEditButtonClick()
        onSaveButtonClick()
    }

    private fun onCloseIconClickListener() {
        mBinding.imageCloseUserAccount.setOnClickListener {
            closeFragment()
        }
    }

    private fun onEditButtonClick() {
        mBinding.imageEdit.setOnClickListener {
            mActivityResultLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).also{ it.type = "image/*" })
        }
    }

    private fun onSaveButtonClick(){
       mBinding.buttonSave.setOnClickListener {
           mBinding.apply {
               val firstName = edFirstName.text.toString()
               val lastName = edLastName.text.toString()
               val email = edEmail.text.toString()
               mViewModel.updateUser(User(firstName , lastName , email) , mImageUri)
           }
       }
    }

    private fun observeOnUserData(){
        lifecycleScope.launchWhenStarted {
            mViewModel.user.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showUserProgressLoading()
                    }

                    is Resource.Success -> {
                        hideUserProgressLoading()
                        bindUserInfo(it.data!!)
                    }

                    is Resource.Failure -> {
                        mBinding.progressbarAccount.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun observeOnUserUpdate(){
        lifecycleScope.launchWhenStarted {
            mViewModel.updateInfo.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        mBinding.buttonSave.startAnimation()
                    }

                    is Resource.Success -> {
                        mBinding.buttonSave.revertAnimation()
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

    private fun bindUserInfo(user: User) {
        mBinding.apply {
            Glide.with(this@UserAccountFragment).load(user.imgPath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
            edEmail.setText(user.email)
        }
    }

    private fun showUserProgressLoading() {
        mBinding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideUserProgressLoading() {
        mBinding.apply {
            progressbarAccount.visibility = View.INVISIBLE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

}