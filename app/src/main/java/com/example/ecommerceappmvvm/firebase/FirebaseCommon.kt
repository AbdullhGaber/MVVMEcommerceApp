package com.example.ecommerceappmvvm.firebase

import com.example.ecommerceappmvvm.model.data_class.CartProduct
import com.example.ecommerceappmvvm.util.Constants.CART_COLLECTION
import com.example.ecommerceappmvvm.util.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.getField

class FirebaseCommon(
    val mFirebaseFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth
) {
    private val mCartCollection =
    mFirebaseFirestore.
    collection(USER_COLLECTION).
    document(mAuth.uid!!).
    collection(CART_COLLECTION)

    fun addProductToCart(cartProduct: CartProduct , onResult : ( (CartProduct?,Exception?) -> Unit)){
        mCartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct , null)
            }.addOnFailureListener {
                onResult(null , it)
            }
    }

    fun increaseProductQuantity(documentId : String , onResult : ( (String?,Exception?) -> Unit)){
       mFirebaseFirestore.runTransaction {
           transaction ->

          val docRef = mCartCollection.document(documentId)

          val cartProduct  = getCartProductByDocumentReference(docRef, transaction)

          cartProduct?.let {
              val newQuantity = it.quantity + 1
              val updatedProduct = it.copy(quantity = newQuantity)
              transaction.set(docRef,updatedProduct)
          }

       }.addOnSuccessListener {
           onResult(it.toString() , null)
       }.addOnFailureListener {
           onResult(null , it)
       }
    }

    fun decreaseProductQuantity(documentId : String , onResult : ( (String?,Exception?) -> Unit)){
       mFirebaseFirestore.runTransaction {
           transaction ->

          val docRef = mCartCollection.document(documentId)

          val cartProduct  = getCartProductByDocumentReference(docRef, transaction)

          cartProduct?.let {
              val newQuantity = it.quantity - 1
              val updatedProduct = it.copy(quantity = newQuantity)
              transaction.set(docRef,updatedProduct)
          }

       }.addOnSuccessListener {
           onResult(it.toString() , null)
       }.addOnFailureListener {
           onResult(null , it)
       }
    }

    private fun getCartProductByDocumentReference (
        documentRef: DocumentReference,
        transaction: Transaction
    ) : CartProduct? {
        val cartDocument = transaction.get(documentRef)
        return cartDocument.toObject(CartProduct::class.java)
    }

    enum class QuantityChange {INCREASE , DECREASE}
}