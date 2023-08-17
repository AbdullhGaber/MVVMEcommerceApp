package com.example.ecommerceappmvvm.ui.fragments.shopping_fragments

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
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.BuildConfig
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentProfileBinding
import com.example.ecommerceappmvvm.model.data_class.User
import com.example.ecommerceappmvvm.ui.activities.LoginRegisterActivity
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.util.showBottomNavigation
import com.example.ecommerceappmvvm.view_models.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var mBinding : FragmentProfileBinding
    private val mViewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        observeOnUserData()
    }

    private fun observeOnUserData() {
        lifecycleScope.launchWhenStarted {
            mViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        mBinding.progressbarSettings.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        mBinding.progressbarSettings.visibility = View.GONE
                        bindUI(it.data!!)
                    }

                    is Resource.Failure -> {
                        mBinding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setOnClickListeners() {
        mBinding.apply {
            constraintProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
            }

            linearAllOrders.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
            }

            linearBilling.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                    0f,
                    emptyArray()
                )
                findNavController().navigate(action)
            }

            linearLogOut.setOnClickListener {
                mViewModel.logout()

                val intent = Intent(context, LoginRegisterActivity::class.java)

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                startActivity(intent)

                requireActivity().finish()
            }

        }
    }

    private fun bindUI(user: User) {
        mBinding.apply {
            tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"
            Glide.with(requireContext()).load(user.imgPath).into(imageUser)
            tvUserName.text = "${user.firstName} ${user.lastName}"
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}