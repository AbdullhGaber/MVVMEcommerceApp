package com.example.ecommerceappmvvm.util

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.closeFragment(){
    findNavController().navigateUp()
}