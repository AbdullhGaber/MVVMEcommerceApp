package com.example.ecommerceappmvvm.helper

fun Float?.getProductPrice(price: Float): Float {
    if (this == null) return price

    val remainingPercentage = 1f - this

    return remainingPercentage * price
}