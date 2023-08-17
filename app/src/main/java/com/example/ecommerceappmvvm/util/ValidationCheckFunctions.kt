package com.example.ecommerceappmvvm.util

import android.util.Patterns


fun getEmailValidationState(email : String) : RegisterValidationState{
    if(email.isEmpty())
        return RegisterValidationState.Failed("Email can't be empty")

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidationState.Failed("Email format not valid")

    return RegisterValidationState.Success
}

fun getPasswordValidationState(password : String) : RegisterValidationState{
    if(password.isEmpty())
        return RegisterValidationState.Failed("Password can't be empty")

    if(password.length < 6)
        return RegisterValidationState.Failed("Password shouldn't be less than 6 characters")

    return RegisterValidationState.Success
}

fun getFieldsState(email : String, password: String) : FieldState{

    return FieldState(
        getEmailValidationState(email) ,  getPasswordValidationState(password)
    )

}

fun isValid(email: String , password: String) : Boolean{
    val fieldsState = getFieldsState(email , password)
    return fieldsState.email    is RegisterValidationState.Success &&
           fieldsState.password is RegisterValidationState.Success
}