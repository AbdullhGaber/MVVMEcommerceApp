package com.example.ecommerceappmvvm.ui.fragments.login_register_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.FragmentIntroductionBinding
import com.example.ecommerceappmvvm.ui.activities.ShoppingActivity
import com.example.ecommerceappmvvm.view_models.IntroductionViewModel
import com.example.ecommerceappmvvm.view_models.IntroductionViewModel.Companion.OPTION_ACTIVITY
import com.example.ecommerceappmvvm.view_models.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment() {
    private lateinit var mBinding : FragmentIntroductionBinding
    private val mViewModel by viewModels<IntroductionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentIntroductionBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOnApplicationState()


        mBinding.startBtn.setOnClickListener {
            mViewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }

    private fun observeOnApplicationState() {
        lifecycleScope.launch {
            mViewModel.navigateState.collect{
                when(it){
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity() , ShoppingActivity::class.java).also{
                            it.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
                            startActivity(it)
                        }
                    }

                    OPTION_ACTIVITY -> {
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
                    }

                    else -> Unit
                }
            }
        }
    }
}