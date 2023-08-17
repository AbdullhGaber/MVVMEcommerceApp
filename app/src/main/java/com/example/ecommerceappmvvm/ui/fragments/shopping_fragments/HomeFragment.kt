package com.example.ecommerceappmvvm.ui.fragments.shopping_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerceappmvvm.databinding.FragmentHomeBinding
import com.example.ecommerceappmvvm.ui.adapters.HomeViewPagerAdapter
import com.example.ecommerceappmvvm.ui.fragments.categories_fragments.*
import com.example.ecommerceappmvvm.util.showBottomNavigation
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var mBinding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPagerAdapter()
        mBinding.viewPager2.isUserInputEnabled = false
        setTabLayoutMediator()

    }

    private fun setTabLayoutMediator() {
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2) { tab, position ->

            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Accessory"
                4 -> tab.text = "Furniture"
                5 -> tab.text = "Table"
            }

        }.attach()
    }

    private fun setUpViewPagerAdapter() {
        val fragments = setUpFragmentsList()
        mBinding.viewPager2.adapter = HomeViewPagerAdapter(fragments , childFragmentManager , lifecycle)
    }

    private fun setUpFragmentsList() : List<Fragment> {
           return arrayListOf(
                MainCategoryFragment(),
                ChairFragment(),
                CupboardFragment(),
                AccessoryFragment(),
                FurnitureFragment(),
                TableFragment()
            )
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}