package com.example.ecommerceappmvvm.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.ecommerceappmvvm.firebase.FirebaseCommon
import com.example.ecommerceappmvvm.util.Constants.INTRODUCTION_SP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun getFireStoreDb() = Firebase.firestore

    @Provides
    fun getSharedPreferences(application: Application) = application.getSharedPreferences(INTRODUCTION_SP , MODE_PRIVATE)

    @Provides
    @Singleton
    fun getFirebaseCommon(
       firebaseAuth: FirebaseAuth ,
       firebaseFirestore: FirebaseFirestore
    ) = FirebaseCommon(firebaseFirestore,firebaseAuth)

    @Provides
    @Singleton
    fun getFirebaseStorage() = FirebaseStorage.getInstance().reference
}