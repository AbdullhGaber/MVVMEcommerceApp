package com.example.ecommerceappmvvm.model.data_class

data class User(
     val firstName : String ,
     val lastName : String,
     val email : String ,
     val imgPath : String = ""
) {
    constructor() : this ("" , "" , "" , "")
}