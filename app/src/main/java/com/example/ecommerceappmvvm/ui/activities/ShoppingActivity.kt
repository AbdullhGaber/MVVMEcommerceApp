package com.example.ecommerceappmvvm.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.ActivityShoppingBinding
import com.example.ecommerceappmvvm.util.Resource
import com.example.ecommerceappmvvm.view_models.CartProductViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private val mBinding: ActivityShoppingBinding by lazy{
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val mViewModel by viewModels<CartProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shopping_fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        mBinding.shoppingBottomNav.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            mViewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Success -> {
                        val count = it.data?.size?: 0
                        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.shopping_bottom_nav)

                        bottomNavigationView.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = getColor(R.color.g_blue)
                        }
                    }
                    else -> {Unit}
                }
            }
        }
    }
}