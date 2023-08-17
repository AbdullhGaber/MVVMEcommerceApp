package com.example.ecommerceappmvvm.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.ui.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.showBottomNavigation(){
    ( activity as ShoppingActivity).
    findViewById<BottomNavigationView>(R.id.shopping_bottom_nav).
    visibility = View.VISIBLE
}

fun Fragment.hideBottomNavigation(){
    ( activity as ShoppingActivity).
    findViewById<BottomNavigationView>(R.id.shopping_bottom_nav).
    visibility = View.GONE
}