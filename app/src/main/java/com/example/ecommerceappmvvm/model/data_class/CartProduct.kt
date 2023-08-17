package com.example.ecommerceappmvvm.model.data_class

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product = Product(),
    val quantity : Int = 1 ,
    val selectedColor : Int? = null,
    val selectedSize : String? = null
) : Parcelable